import sbt._
import sbt.Keys._

import Aliases._

object Settings {

  private val scala211 = "2.11.12"
  private val scala212 = "2.12.8"

  lazy val shared = Seq(
    scalaVersion := scala212,
    crossScalaVersions := Seq(scala212, scala211),
    scalacOptions += "-target:jvm-1.7",
    resolvers += Resolver.sonatypeRepo("releases"),
    libraryDependencies +=
      compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.patch)
  )

  lazy val dontPublish = Seq(
    publish := (),
    publishLocal := (),
    publishArtifact := false
  )

  lazy val utest = Seq(
    libs += Deps.utest.value % "test",
    testFrameworks += new TestFramework("utest.runner.Framework")
  )

  // '.' in name get replaced by '-' else
  lazy val keepNameAsModuleName = {
    moduleName := name.value
  }

}
