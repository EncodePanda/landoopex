package landoopex

import cats.mtl._
import io.estatico.newtype.macros.newtype

package object types {
  @newtype case class Currency(value: String)

  type CacheEff[F[_]] = MonadState[F, Cache]
  object CacheEff {
    def apply[F[_]: CacheEff]: CacheEff[F] = implicitly[CacheEff[F]]
  }
}
