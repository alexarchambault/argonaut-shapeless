import com.typesafe.sbt.pgp.PgpKeys

lazy val `argonaut-shapeless` = project.in(file("."))
  .aggregate(core, refined, doc)
  .settings(commonSettings)
  .settings(compileSettings)
  .settings(noPublishSettings)

lazy val core = project
  .settings(commonSettings)
  .settings(coreSettings)
  .settings(projectSettings)

lazy val refined = project
  .dependsOn(core % "test")
  .settings(commonSettings)
  .settings(refinedSettings)
  .settings(projectSettings)

lazy val doc = project
  .dependsOn(core, refined)
  .settings(commonSettings)
  .settings(compileSettings)
  .settings(noPublishSettings)
  .settings(tutSettings)
  .settings(
    tutSourceDirectory := baseDirectory.value,
    tutTargetDirectory := baseDirectory.value / ".."
  )

lazy val coreName = "argonaut-shapeless_6.1"

val shapelessCompatVersion = "1.0.0-M4"

lazy val coreSettings = Seq(
  organization := "com.github.alexarchambault",
  name := coreName,
  moduleName := coreName,
  libraryDependencies ++= Seq(
    "io.argonaut" %% "argonaut" % "6.1",
    "com.chuusai" %% "shapeless" % "2.2.5",
    "com.github.alexarchambault" %% "shapeless-compat" % shapelessCompatVersion
  )
)

lazy val refinedName = "argonaut-refined_6.1"

lazy val refinedSettings = Seq(
  organization := "com.github.alexarchambault",
  name := refinedName,
  moduleName := refinedName,
  libraryDependencies ++= Seq(
    "io.argonaut" %% "argonaut" % "6.1",
    "com.chuusai" %% "shapeless" % "2.2.5",
    "com.github.alexarchambault" %% "shapeless-compat" % shapelessCompatVersion,
    "eu.timepit" %% "refined" % "0.3.3"
  )
)

lazy val projectSettings =
  compileSettings ++
  testSettings ++
  releaseSettings ++
  extraReleaseSettings

lazy val compileSettings = Seq(
  scalaVersion := "2.11.7",
  crossScalaVersions := Seq("2.10.6", "2.11.7"),
  scalacOptions += "-target:jvm-1.7",
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases")
  ),
  libraryDependencies ++= {
    if (scalaVersion.value startsWith "2.10.")
      Seq(compilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full))
    else
      Seq()
  }
)


lazy val testSettings = Seq(
  libraryDependencies ++= Seq(
    "com.github.alexarchambault" %% "scalacheck-shapeless_1.13" % "1.0.0-RC1" % "test",
    "com.lihaoyi" %% "utest" % "0.3.0" % "test"
  ),
  testFrameworks += new TestFramework("utest.runner.Framework")
)

lazy val commonSettings = Seq(
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
addCommandAlias("validate", Seq(
  "test",
  "tut"
).mkString(";", ";", ""))
