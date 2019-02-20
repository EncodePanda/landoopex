package landoopex

import cats._, cats.data._, cats.implicits._
import cats.effect._

import org.http4s._, org.http4s.dsl.io._
import org.http4s.server.blaze._
import org.http4s.implicits._
import org.http4s.server.Router

import org.http4s.dsl.io

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends IOApp {

  implicit val ex = Exchange.dummy[IO]

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(rest.routes[IO](io))
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
