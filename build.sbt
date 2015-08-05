import com.typesafe.sbt.pgp.PgpKeys

lazy val root = project.in(file("."))
  .aggregate(core)
  .settings(compileSettings)
  .settings(noPublishSettings)

lazy val core = project.in(file("core"))
  .settings(coreSettings)
  .settings(compileSettings)
  .settings(publishSettings)
  .settings(releaseSettings)
  .settings(extraReleaseSettings)

lazy val coreName = "argonaut-shapeless_6.1"

lazy val coreSettings = Seq(
  organization := "com.github.alexarchambault",
  name := coreName,
  moduleName := coreName
)

lazy val compileSettings = Seq(
  scalaVersion := "2.11.6",
  crossScalaVersions := Seq("2.10.5", "2.11.6"),
  scalacOptions += "-target:jvm-1.7",
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  ),
  libraryDependencies ++= Seq(
    "io.argonaut" %% "argonaut" % "6.1",
    "com.chuusai" %% "shapeless" % "2.2.2",
    "org.scalatest" %% "scalatest" % "2.2.5" % "test",
    "org.scalacheck" %% "scalacheck" % "1.12.4" % "test",
    "com.github.alexarchambault" %% "scalacheck-shapeless_1.12" % "0.2.0" % "test"
  ),
  libraryDependencies ++= {
    if (scalaVersion.value startsWith "2.10.")
      Seq(
        compilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full)
      )
    else
      Seq()
  }
)

lazy val publishSettings = Seq(
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
  credentials += {
    Seq("SONATYPE_USER", "SONATYPE_PASS").map(sys.env.get) match {
      case Seq(Some(user), Some(pass)) =>
        Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", user, pass)
      case _ =>
        Credentials(Path.userHome / ".ivy2" / ".credentials")
    }
  }
)

lazy val noPublishSettings = Seq(
  publish := (),
  publishLocal := (),
  publishArtifact := false
)

lazy val extraReleaseSettings = Seq(
  ReleaseKeys.versionBump := sbtrelease.Version.Bump.Bugfix,
  sbtrelease.ReleasePlugin.ReleaseKeys.publishArtifactsAction := PgpKeys.publishSigned.value
)

// build.sbt shamelessly inspired by https://github.com/fthomas/refined/blob/master/build.sbt
