import sbt._
import sbt.Keys._

import Aliases._

object Settings {

  private val scala211 = "2.11.12"
  private val scala212 = "2.12.8"
  private val scala213 = "2.13.0-RC1"

  lazy val shared = Seq(
    scalaVersion := scala212,
    crossScalaVersions := Seq(scala213, scala212, scala211),
    scalacOptions += "-target:jvm-1.8",
    scalacOptions ++= {
      val sbv = scalaBinaryVersion.value
      if (sbv.startsWith("2.11") || sbv.startsWith("2.12"))
        Nil
      else
        Seq("-Ymacro-annotations")
    },
    javacOptions ++= Seq(
      "-source", "1.8",
      "-target", "1.8"
    ),
    libraryDependencies ++= {
      if (scalaVersion.value.startsWith("2.13."))
        Nil
      else
        Seq(
          compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.patch)
        )
    }
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
