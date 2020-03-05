lazy val commonSettings = /*commonSmlBuildSettings ++*/ Seq(
  organization := "com.softwaremill",
  crossScalaVersions := Seq("0.22.0-RC1", "2.13.1"),
  scalaVersion := crossScalaVersions.value.head,
  scalacOptions ++= (
    if (isDotty.value) Seq("-Ykind-projector", "-language:implicitConversions")
    else Nil)
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
      "dev.zio" %% "zio" % "1.0.0-RC18-1",
      // "dev.zio" %% "zio-interop-cats" % ???,
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      scalaTest
    ) ++ Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
      "org.typelevel" %% "cats-core" % "2.1.1",
      "io.monix" %% "monix" % "3.1.0",
    ).map(_.withDottyCompat(scalaVersion.value)) ++ (
      if(isDotty.value) Nil
      else Seq(compilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full))
    )
  )
