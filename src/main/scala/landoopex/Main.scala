package landoopex

import cats._, cats.data._, cats.implicits._
import cats.effect._, concurrent.Ref

import org.http4s._, org.http4s.dsl.io._
import org.http4s.server.blaze._
import org.http4s.implicits._
import org.http4s.server.Router

import org.http4s.dsl.io
import org.http4s.client.dsl.{io => clientIo}

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends IOApp {

  implicit val cache = new RefMonadState[IO, Cache](Ref.unsafe(Cache.empty))
  implicit val ex    = Exchange.apiBased[IO](clientIo, executionContext = global)

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(rest.routes[IO](io))
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
