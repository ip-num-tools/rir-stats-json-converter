import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.3",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "RIRStats",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.4" % Test,
      "com.networknt" % "json-schema-validator" % "0.1.13",
      "geekabyte.io" %% "rir_statistics_exchange_schema" % "0.1"
    ),
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser")
      .map(_ % "0.9.1"),
    wartremoverErrors ++= Warts.unsafe
  )

resolvers += "emueller-bintray" at "http://dl.bintray.com/emueller/maven"
addCompilerPlugin(
  "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
  )