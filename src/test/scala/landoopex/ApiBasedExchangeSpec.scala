package landoopex

import errors._, types._

import cats._, cats.data._, cats.implicits._
import cats.mtl._
import cats.mtl.instances.state._
import org.scalatest._

class ApiBasedExchangeSpec extends FunSpec with Matchers {

  type Eff[A] = State[Cache, A]

  describe("ApiBasedExchange") {
    it("should fail if amount is smaller then 0") {
      // given
      implicit val external: ExternalService[Eff] = null
      val exchange                                = new ApiBasedExchange[Eff]
      val amount                                  = MINUS_TEN
      // when
      val result = exchange.convert(amount, EUR, USD)
      // then
      result.runA(Cache.empty).value shouldBe (insufficientAmount(amount)).asLeft
    }

    it("should return result from cache if value already in cache") {
      // given
      implicit val external: ExternalService[Eff] = null
      val exchange                                = new ApiBasedExchange[Eff]
      val amount                                  = TEN
      val exchangeRate                            = RATE_2_5
      val cache                                   = Cache.init(CacheEntry(EUR, USD) -> exchangeRate)
      // when
      val result = exchange.convert(amount, EUR, USD)
      // then
      result.runA(cache).value shouldBe (Result(exchangeRate, amount * exchangeRate)).asRight
    }

    it("should return error if not in cache but external service returns an error") {
      // given
      val error                                   = exchangeNotPossible(new RuntimeException("err"))
      implicit val external: ExternalService[Eff] = alwaysFail(error)
      val exchange                                = new ApiBasedExchange[Eff]
      val amount                                  = TEN
      // when
      val result = exchange.convert(amount, EUR, USD)
      // then
      result.runA(Cache.empty).value shouldBe (error).asLeft
    }

    it(
      "should return result from external service if not in cache and external service returns a result"
    ) {
      // given
      val exchangeRate                            = RATE_2_5
      implicit val external: ExternalService[Eff] = alwaysSucceeds(exchangeRate)
      val exchange                                = new ApiBasedExchange[Eff]
      val amount                                  = TEN
      // when
      val result = exchange.convert(amount, EUR, USD)
      // then
      result.runA(Cache.empty).value shouldBe (Result(exchangeRate, amount * exchangeRate)).asRight
    }
  }

  private def alwaysFail(err: ExchangeError): ExternalService[Eff] = {
    case _ => err.asLeft.pure[Eff]
  }

  private def alwaysSucceeds(rate: Rate): ExternalService[Eff] = {
    case (amount, from, to) => Result(rate, amount * rate).asRight.pure[Eff]
  }

  private val USD = Currency("USD")
  private val EUR = Currency("EUR")

  private val TEN       = Amount(BigDecimal("10"))
  private val MINUS_TEN = Amount(BigDecimal("-10"))

  private val RATE_2_5 = Rate(BigDecimal("2.5"))
}
