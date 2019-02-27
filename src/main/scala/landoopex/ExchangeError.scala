package landoopex

import cats._, cats.data._, cats.implicits._
import types._

sealed trait ExchangeError
case class CurrencyNotAvailable(currency: Currency)  extends ExchangeError
case class ExchangeNotPossible(throwable: Throwable) extends ExchangeError
case class InsufficientAmount(amount: Amount)        extends ExchangeError

object errors {
  type ExchangeErr[A] = Either[ExchangeError, A]

  def currencyNotAvailable(currency: Currency): ExchangeError =
    CurrencyNotAvailable(currency)
  def exchangeNotPossible(throwable: Throwable): ExchangeError = ExchangeNotPossible(throwable)
  def insufficientAmount(amount: Amount): ExchangeError =
    InsufficientAmount(amount)
}
