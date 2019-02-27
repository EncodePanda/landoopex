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
      val amount                                  = -10
      // when
      val result = exchange.convert(amount, EUR, USD)
      // then
      result.runA(Cache.empty).value shouldBe (insufficientAmount(amount)).asLeft
    }
  }

  private val USD = Currency("USD")
  private val EUR = Currency("EUR")

}
