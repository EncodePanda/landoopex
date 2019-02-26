package landoopex

import cats._, cats.data._, cats.implicits._
import cats.effect.concurrent.Ref
import cats.mtl._

class RefMonadState[F[_]: Monad, S](ref: Ref[F, S]) extends MonadState[F, S] {

  val monad: Monad[F] = Monad[F]

  def get: F[S]                   = ref.get
  def set(s: S): F[Unit]          = ref.set(s)
  def inspect[A](f: S => A): F[A] = monad.map(get)(f)
  def modify(f: S => S): F[Unit]  = ref.modify(s => (f(s), ()))
}
