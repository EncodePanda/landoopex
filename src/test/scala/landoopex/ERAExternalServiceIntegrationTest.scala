package landoopex

import types._
import cats.effect._
import cats._, cats.data._, cats.implicits._

import org.http4s.client.dsl.{io => clientIo}
import java.net.ConnectException
import scala.concurrent.ExecutionContext.Implicits.global

import org.scalatest._

class ERAExternalServiceIntegrationTest extends FunSpec with Matchers {

  implicit val cs: ContextShift[IO] = IO.contextShift(global)
  implicit val timer: Timer[IO]     = IO.timer(global)

  private val port = 8080

  describe("ERAExternalService") {

    it("should return error if service not available") {
      // given
      val amount = Amount(BigDecimal("10.0"))
      val from   = Currency("EUR")
      val to     = Currency("USD")

      // when
      val service = new ERAExternalService[IO](url)(clientIo, executionContext = global)

      // then
      val ExchangeNotPossible(throwable) = service.fetch(amount, from, to).unsafeRunSync.left.get
      throwable shouldBe a[ConnectException]
      throwable.getMessage shouldBe ("Connection refused")
    }
  }

  private val url: Currency => String =
    currency => s"http://localhost:${port}/latest?base=${currency.value}"

}
