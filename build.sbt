name := "landoopex"

scalaVersion := "2.12.8"

scalafmtOnCompile := true

resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies ++= Seq(
  // cats for FP stuff
  "org.typelevel"          %% "cats-core"                 % "1.6.0",
  "org.typelevel"          %% "cats-effect"               % "1.2.0",
  // eastico for newtype magic
  "io.estatico" %% "newtype" % "0.4.2"
)

// so that esatico magic work
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

scalacOptions ++= Seq("-Ypartial-unification")
