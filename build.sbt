name := "landoopex"

scalaVersion := "2.12.8"

scalafmtOnCompile := true

val Http4sVersion = "0.20.0-M6"

resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies ++= Seq(
  // cats for FP stuff
  "org.typelevel"          %% "cats-core"                 % "1.6.0",
  "org.typelevel"          %% "cats-effect"               % "1.2.0",
  "org.typelevel"          %% "cats-mtl-core"             % "0.4.0",
  // http4s for rest heavylifting
  "org.http4s"             %% "http4s-blaze-server"       % Http4sVersion,
  "org.http4s"             %% "http4s-blaze-client"       % Http4sVersion,
  "org.http4s"             %% "http4s-circe"              % Http4sVersion,
  "org.http4s"             %% "http4s-dsl"                % Http4sVersion,
  // circe for json boilerplatte
  "org.http4s"             %% "http4s-circe"              % Http4sVersion,
  "io.circe"               %% "circe-generic"             % "0.11.1",
  // eastico for newtype magic
  "io.estatico"            %% "newtype"                   % "0.4.2",
  "ch.qos.logback"         %  "logback-classic"           % "1.2.3",
  // tests
  "org.scalatest"          %% "scalatest"                 % "3.0.5"        % "test",
  "com.github.tomakehurst" %  "wiremock"                  % "2.21.0"       % "test"
)

// so that circe and esatico magic work
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

scalacOptions ++= Seq("-Ypartial-unification")
