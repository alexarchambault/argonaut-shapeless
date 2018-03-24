import sbt._
import sbt.Keys._

import Aliases._

object Settings {

  private val scala210 = "2.10.7"
  private val scala211 = "2.11.12"
  private val scala212 = "2.12.4"

  lazy val shared = Seq(
    scalaVersion := scala211,
    crossScalaVersions := Seq(scala212, scala211, scala210),
    organization := "com.github.alexarchambault",
    homepage := Some(url("https://github.com/alexarchambault/argonaut-shapeless")),
    licenses := Seq(
      "BSD-3-Clause" -> url("http://www.opensource.org/licenses/BSD-3-Clause")
    ),
    scmInfo := Some(ScmInfo(
      url("https://github.com/alexarchambault/argonaut-shapeless.git"),
      "scm:git:github.com/alexarchambault/argonaut-shapeless.git",
      Some("scm:git:git@github.com:alexarchambault/argonaut-shapeless.git")
    )),
    developers := List(Developer(
      "alexarchambault",
      "Alexandre Archambault",
      "",
      url("https://github.com/alexarchambault")
    )),
    publishMavenStyle := true,
    pomIncludeRepository := { _ => false },
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    credentials ++= {
      Seq("SONATYPE_USER", "SONATYPE_PASS").map(sys.env.get) match {
        case Seq(Some(user), Some(pass)) =>
          Seq(Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", user, pass))
        case _ =>
          Seq.empty
      }
    },
    scalacOptions += "-target:jvm-1.7",
    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases")
    ),
    libraryDependencies +=
      compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.patch)
  )

  lazy val dontPublish = Seq(
    publish := (),
    publishLocal := (),
    publishArtifact := false
  )

  lazy val scala211_12Sources = {
    unmanagedSourceDirectories.in(Compile) ++= {
      scalaBinaryVersion.value match {
        case "2.11" | "2.12" =>
          unmanagedSourceDirectories
            .in(Compile)
            .value
            .filter(_.getName == "scala")
            .map(_.getParentFile / "scala-2.11_2.12")
        case _ =>
          Seq()
      }
    }
  }

  lazy val scala211_12TestSources = {
    unmanagedSourceDirectories.in(Test) ++= {
      scalaBinaryVersion.value match {
        case "2.11" | "2.12" =>
          unmanagedSourceDirectories
            .in(Test)
            .value
            .filter(_.getName == "scala")
            .map(_.getParentFile / "scala-2.11_2.12")
        case _ =>
          Seq()
      }
    }
  }

  lazy val utest = Seq(
    libs += Deps.utest.value % "test",
    testFrameworks += new TestFramework("utest.runner.Framework")
  )

  // '.' in name get replaced by '-' else
  lazy val keepNameAsModuleName = {
    moduleName := name.value
  }

}
