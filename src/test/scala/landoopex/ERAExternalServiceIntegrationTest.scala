package landoopex

import types._
import cats.effect._
import cats._, cats.data._, cats.implicits._

import org.http4s.client.dsl.{io => clientIo}
import java.net.ConnectException
import scala.concurrent.ExecutionContext.Implicits.global

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import org.scalatest._

class ERAExternalServiceIntegrationTest extends FunSpec with Matchers with BeforeAndAfterEach {

  implicit val cs: ContextShift[IO] = IO.contextShift(global)
  implicit val timer: Timer[IO]     = IO.timer(global)

  private val port   = 8080
  private val server = new WireMockServer(options.port(port))

  override def beforeEach {
    server.start()
  }

  override def afterEach {
    server.stop()
  }

  describe("ERAExternalService") {

    it("should return error if service not available") {
      // given
      val amount = Amount(BigDecimal("10.0"))
      val from   = Currency("EUR")
      val to     = Currency("USD")

      // when
      val service = new ERAExternalService[IO](url)(clientIo, executionContext = global)
      server.stop()

      // then
      val ExchangeNotPossible(throwable) = service.fetch(amount, from, to).unsafeRunSync.left.get
      throwable shouldBe a[ConnectException]
      throwable.getMessage shouldBe ("Connection refused")
    }

    it("should return currency rate if it exists") {
      // given
      val amount = Amount(BigDecimal("10.0"))
      val from   = Currency("EUR")
      val to     = Currency("USD")
      val rate   = Rate(BigDecimal("1.1386"))

      generateStub(from, (to -> rate))
      // when
      val service = new ERAExternalService[IO](url)(clientIo, executionContext = global)
      // then
      val result = service.fetch(amount, from, to).unsafeRunSync
      result shouldBe Result(rate, amount * rate).asRight
    }
  }

  private val url: Currency => String =
    currency => s"http://localhost:${port}/latest?base=${currency.value}"

  private def generateStub(from: Currency, toRate: (Currency, Rate)): Unit = {
    val (to, rate) = toRate
    val body =
      s"""{"rates":{"${to.value}":${rate.value}},"base":"${to.value}","date":"2019-02-27"}"""
    stubFor(
      get(urlEqualTo(s"/latest?base=${from.value}"))
        .willReturn(
          aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody(
              body
            )
            .withStatus(200)
        )
    )
  }

}
