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

}
