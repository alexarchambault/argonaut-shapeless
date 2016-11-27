import com.typesafe.sbt.pgp.PgpKeys

lazy val `argonaut-shapeless` = project.in(file("."))
  .aggregate(core, refined, test, doc)
  .settings(commonSettings)
  .settings(noPublishSettings)

lazy val core = project
  .settings(commonSettings)
  .settings(coreSettings)

lazy val refined = project
  .dependsOn(core % "test")
  .settings(commonSettings)
  .settings(refinedSettings)

lazy val test = project
  .dependsOn(core, refined)
  .settings(commonSettings)
  .settings(coreSettings)
  .settings(noPublishSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.github.alexarchambault" %% "scalacheck-shapeless_1.13" % "1.1.3" % "test",
      "com.lihaoyi" %% "utest" % "0.4.4" % "test"
    ),
    testFrameworks += new TestFramework("utest.runner.Framework")
  )

lazy val doc = project
  .dependsOn(core, refined)
  .settings(commonSettings)
  .settings(noPublishSettings)
  .settings(tutSettings)
  .settings(
    tutSourceDirectory := baseDirectory.value,
    tutTargetDirectory := baseDirectory.value / ".."
  )

val argonautVersion = "6.2-RC2"
val shapelessVersion = "2.3.2"

lazy val coreName = "argonaut-shapeless_6.2"

lazy val coreSettings = Seq(
  organization := "com.github.alexarchambault",
  name := coreName,
  moduleName := coreName,
  libraryDependencies ++= Seq(
    "io.argonaut" %% "argonaut" % argonautVersion,
    "com.chuusai" %% "shapeless" % shapelessVersion
  )
)

lazy val refinedName = "argonaut-refined_6.2"

lazy val refinedSettings = Seq(
  organization := "com.github.alexarchambault",
  name := refinedName,
  moduleName := refinedName,
  libraryDependencies ++= Seq(
    "io.argonaut" %% "argonaut" % argonautVersion,
    "com.chuusai" %% "shapeless" % shapelessVersion,
    "eu.timepit" %% "refined" % "0.6.0"
  )
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
  credentials ++= {
    Seq("SONATYPE_USER", "SONATYPE_PASS").map(sys.env.get) match {
      case Seq(Some(user), Some(pass)) =>
        Seq(Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", user, pass))
      case _ =>
        Seq.empty
    }
  },
  scalaVersion := "2.11.8",
  scalacOptions += "-target:jvm-1.7",
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases")
  ),
  libraryDependencies +=
    compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
)

lazy val noPublishSettings = Seq(
  publish := (),
  publishLocal := (),
  publishArtifact := false
)

// build.sbt shamelessly inspired by https://github.com/fthomas/refined/blob/master/build.sbt
addCommandAlias("validate", Seq(
  "test",
  "tut"
).mkString(";", ";", ""))
