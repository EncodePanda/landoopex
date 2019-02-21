package landoopex

import cats._, cats.data._, cats.implicits._
import cats.effect._
import types._, errors._

case class Result(exchange: Double, amount: Double)

trait Exchange[F[_]] {
  def convert(
      amount: Double,
      from: Currency,
      to: Currency
  ): F[ExchangeErr[Result]]
}

object Exchange {

  def apply[F[_]: Exchange]: Exchange[F] = implicitly[Exchange[F]]

  import org.http4s.Status._
  import org.http4s.circe._
  import org.http4s.client._
  import org.http4s.client.dsl.Http4sClientDsl
  import org.http4s.client.blaze._
  import io.circe._
  import io.circe.generic.auto._
  import io.circe.syntax._
  import scala.concurrent.ExecutionContext

  case class ApiResponse(base: String, rates: Map[String, Double])

  def apiBased[F[_]: Applicative: ConcurrentEffect](
      dsl: Http4sClientDsl[F],
      executionContext: ExecutionContext
  ): Exchange[F] = new Exchange[F] {
    def convert(
        amount: Double,
        from: Currency,
        to: Currency
    ): F[ExchangeErr[Result]] = {
      import dsl._
      implicit val codec = jsonOf[F, ApiResponse]

      val toResult: ApiResponse => ExchangeErr[Result] = {
        case ApiResponse(base, rates) if (rates.contains(to.value)) =>
          val rate = rates(to.value)
          Result(rate, amount * rate).asRight[ExchangeError]
        case _ => currencyNotAvailable(from).asLeft[Result]
      }

      val handleError: F[ExchangeErr[Result]] => F[ExchangeErr[Result]] =
        result =>
          ApplicativeError[F, Throwable]
            .attempt(result)
            .map(_.fold({
              case UnexpectedStatus(BadRequest) => currencyNotAvailable(from).asLeft[Result]
              case err                          => exchangeNotPossible.asLeft[Result]
            }, identity))

      val url = s"https://api.exchangeratesapi.io/latest?base=${from.value}"

      val apiRequest =
        BlazeClientBuilder[F](executionContext).resource.use(_.expect[ApiResponse](url))

      if (amount > 0) handleError(apiRequest.map(toResult))
      else insufficientAmount(amount).asLeft[Result].pure[F]
    }
  }
}
