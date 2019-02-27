package landoopex

import cats._, cats.data._, cats.implicits._
import cats.mtl._
import io.estatico.newtype.macros.newtype
import io.estatico.newtype._
import scala.math.BigDecimal

import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._

package object types {
  // format: off
  implicit def coercibleOrder[R, N](
    implicit ev: Coercible[Order[R], Order[N]], R: Order[R]
  ): Order[N] = ev(R)
  // format: on

  @newtype case class Currency(value: String)
  @newtype case class Amount(value: BigDecimal) {
    def *(rate: Rate): Amount = Amount(value * rate.value)
  }

  case class Rate(value: BigDecimal)

  object Amount {
    def ZERO: Amount = Amount(BigDecimal("0"))
  }

  type CacheEff[F[_]] = MonadState[F, Cache]
  object CacheEff {
    def apply[F[_]: CacheEff]: CacheEff[F] = implicitly[CacheEff[F]]
  }
}
