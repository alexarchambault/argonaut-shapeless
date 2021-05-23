import sbt._
import sbt.Keys._

object Settings {

  private val scala211 = "2.11.12"
  private val scala212 = "2.12.11"
  private val scala213 = "2.13.2"

  lazy val shared = Seq(
    scalaVersion := scala212,
    crossScalaVersions := Seq(scala213, scala212, scala211),
    crossScalaVersions := {
      val former = crossScalaVersions.value
      if (isScalaJs1.value)
        former.filter(!_.startsWith("2.11."))
      else if (isScalaNative.value)
        former.filter(_.startsWith("2.11."))
      else
        former
    },
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

  lazy val utest = Seq(
    libraryDependencies += Deps.utest.value % "test",
    testFrameworks += new TestFramework("utest.runner.Framework")
  )

  // '.' in name get replaced by '-' else
  lazy val keepNameAsModuleName = {
    moduleName := name.value
  }

  lazy val isScalaJs1 = Def.setting {
    def scalaJsVersion = Option(System.getenv("SCALAJS_VERSION")).getOrElse("1.0.0")
    sbtcrossproject.CrossPlugin.autoImport.crossProjectPlatform.?.value.contains(scalajscrossproject.JSPlatform) &&
      scalaJsVersion.startsWith("1.")
  }

  lazy val isScalaNative = Def.setting {
    sbtcrossproject.CrossPlugin.autoImport.crossProjectPlatform.?.value.contains(scalanativecrossproject.NativePlatform)
  }

}
