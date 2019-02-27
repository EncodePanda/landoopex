package landoopex

import cats._, cats.data._, cats.implicits._
import cats.effect._
import types._, errors._

import org.http4s.Status._
import org.http4s.circe._
import org.http4s.client._
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.client.blaze._
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import scala.concurrent.ExecutionContext

case class ApiResponse(base: String, rates: Map[String, BigDecimal])

trait ExternalService[F[_]] {
  def fetch(amount: Amount, from: Currency, to: Currency): F[ExchangeErr[Result]]
}

object ExternalService {
  def apply[F[_]: ExternalService]: ExternalService[F] = implicitly[ExternalService[F]]
}

class ERAExternalService[F[_]: ConcurrentEffect](url: Currency => String)(
    dsl: Http4sClientDsl[F],
    executionContext: ExecutionContext
) extends ExternalService[F] {

  import dsl._
  implicit val codec = jsonOf[F, ApiResponse]

  def fetch(amount: Amount, from: Currency, to: Currency): F[ExchangeErr[Result]] = {

    val toResult: ApiResponse => ExchangeErr[Result] = {
      case ApiResponse(base, rates) if (rates.contains(to.value)) =>
        val rate = Rate(rates(to.value))
        Result(rate, amount * rate).asRight[ExchangeError]
      case _ => currencyNotAvailable(from).asLeft[Result]
    }

    val handleError: F[ExchangeErr[Result]] => F[ExchangeErr[Result]] =
      result =>
        ApplicativeError[F, Throwable]
          .attempt(result)
          .map(_.fold({
            case UnexpectedStatus(BadRequest) => currencyNotAvailable(from).asLeft[Result]
            case err                          => exchangeNotPossible(err).asLeft[Result]
          }, identity))

    val apiRequest =
      BlazeClientBuilder[F](executionContext).resource.use(_.expect[ApiResponse](url(from)))

    handleError(apiRequest.map(toResult))

  }
}

class ApiBasedExchange[F[_]: Monad: CacheEff: ExternalService] extends Exchange[F] {

  def convert(
      amount: Amount,
      from: Currency,
      to: Currency
  ): F[ExchangeErr[Result]] = {

    def getFromCache: F[Option[ExchangeErr[Result]]] =
      for {
        maybeExchange <- CacheEff[F].get.map(_.vals.get(CacheEntry(from, to)))
      } yield maybeExchange.map(exchange => Result(exchange, amount * exchange).asRight)

    def updateCache(result: Result): F[Unit] =
      CacheEff[F].modify(
        cache => cache.copy(vals = cache.vals + (CacheEntry(from, to) -> result.exchange))
      )

    def fetchFresh: F[ExchangeErr[Result]] =
      for {
        resultErr <- ExternalService[F].fetch(amount, from, to)
        _         <- resultErr.fold(_.pure[F], result => updateCache(result))
      } yield resultErr

    if (amount < Amount.ZERO) insufficientAmount(amount).asLeft[Result].pure[F]
    else getFromCache >>= (_.fold(fetchFresh)(res => res.pure[F]))
  }

}
