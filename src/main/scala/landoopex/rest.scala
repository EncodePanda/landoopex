package landoopex

import types._

import cats._, cats.data._, cats.implicits._
import cats.effect._

import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._

import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import org.http4s.implicits._

object rest {

  private implicit def exchangeRequestCodec[F[_]: Sync] =
    jsonOf[F, ExchangeRequest]
  private implicit def exchangeResponseCodec[F[_]: Sync] =
    jsonOf[F, ExchangeResponse]

  private case class ExchangeRequest(
      fromCurrency: String,
      toCurrency: String,
      amount: BigDecimal
  )
  private case class ExchangeResponse(
      exchange: BigDecimal,
      amount: BigDecimal,
      original: BigDecimal
  )

  private implicit val show: Show[ExchangeError] = new Show[ExchangeError] {
    def show(error: ExchangeError): String = error match {
      case CurrencyNotAvailable(currency) =>
        s"Requested currency $currency does not exist"
      case ExchangeNotPossible(th) => s"Exchange is not possible right now: ${th.getMessage}"
      case InsufficientAmount(amount) =>
        s"Provided amount $amount is insufficient to convert"
    }
  }

  private def roundOutput(bd: BigDecimal): BigDecimal =
    bd.setScale(4, BigDecimal.RoundingMode.HALF_EVEN)

  def routes[F[_]: Monad: Effect: Exchange](dsl: Http4sDsl[F]) = {
    import dsl._
    HttpRoutes
      .of[F] {
        case rawReq @ POST -> Root / "api" / "convert" =>
          for {
            req <- rawReq.as[ExchangeRequest]
            resultErr <- Exchange[F].convert(
                          Amount(req.amount),
                          Currency(req.fromCurrency),
                          Currency(req.toCurrency)
                        )
            resp <- resultErr.fold(
                     error => BadRequest(error.show),
                     result =>
                       Ok(
                         ExchangeResponse(
                           exchange = roundOutput(result.exchange.value),
                           amount = roundOutput(result.amount.value),
                           original = roundOutput(req.amount)
                         ).asJson
                       )
                   )
          } yield (resp)
      }
      .orNotFound
  }
}
