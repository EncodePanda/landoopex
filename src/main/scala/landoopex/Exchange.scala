package landoopex

import cats._, cats.data._, cats.implicits._
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

  // TODO remove this eventually
  def dummy[F[_]: Applicative] = new Exchange[F] {
    def convert(
        amount: Double,
        from: Currency,
        to: Currency
    ): F[ExchangeErr[Result]] =
      if (amount <= 0) insufficientAmount(amount).asLeft[Result].pure[F]
      else Result(2.0, amount * 2).asRight[ExchangeError].pure[F]
  }

}
