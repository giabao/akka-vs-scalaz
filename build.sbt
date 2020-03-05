lazy val commonSettings = commonSmlBuildSettings ++ Seq(
  organization := "com.softwaremill",
  scalaVersion := "2.13.1"
)

val scalaTest = "org.scalatest" %% "scalatest" % "3.1.1" % "test"

lazy val rootProject = (project in file("."))
  .settings(commonSettings: _*)
  .settings(publishArtifact := false, name := "akka-vs-scalaz")
  .aggregate(core)

lazy val akkaVersion = "2.6.3"

lazy val core: Project = (project in file("core"))
  .settings(commonSettings: _*)
  .settings(
    name := "core",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
      "dev.zio" %% "zio" % "1.0.0-RC18-1",
      "org.typelevel" %% "cats-core" % "2.1.1",
      // "dev.zio" %% "zio-interop-cats" % ???,
      "io.monix" %% "monix" % "3.1.0",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
      scalaTest
    )
  )
