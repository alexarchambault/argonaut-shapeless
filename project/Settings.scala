import sbt._
import sbt.Keys._

object Settings {

  private val scala212 = "2.12.17"
  private val scala213 = "2.13.12"

  lazy val shared = Seq(
    scalaVersion := scala213,
    crossScalaVersions := Seq(scala213, scala212),
    scalacOptions += "-target:jvm-1.8",
    scalacOptions ++= {
      val sbv = scalaBinaryVersion.value
      if (sbv.startsWith("2.12"))
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

  lazy val utest = Seq(
    libraryDependencies += Deps.utest.value % Test,
    testFrameworks += new TestFramework("utest.runner.Framework")
  )

  // '.' in name get replaced by '-' else
  lazy val keepNameAsModuleName = {
    moduleName := name.value
  }

  lazy val isScalaNative = Def.setting {
    sbtcrossproject.CrossPlugin.autoImport.crossProjectPlatform.?.value.contains(scalanativecrossproject.NativePlatform)
  }

}
