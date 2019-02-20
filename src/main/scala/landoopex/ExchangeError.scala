package landoopex

import cats._, cats.data._, cats.implicits._
import types._

sealed trait ExchangeError
case class CurrencyNotAvailable(currency: Currency) extends ExchangeError
case object ExchangeNotPossible                     extends ExchangeError
case class InsufficientAmount(amount: Double)       extends ExchangeError

object errors {
  type ExchangeErr[A] = Either[ExchangeError, A]

  def currencyNotAvailable(currency: Currency): ExchangeError =
    CurrencyNotAvailable(currency)
  def exchangeNotPossible: ExchangeError = ExchangeNotPossible
  def insufficientAmount(amount: Double): ExchangeError =
    InsufficientAmount(amount)
}
