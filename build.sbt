import com.typesafe.sbt.pgp.PgpKeys

lazy val root = project.in(file("."))
  .aggregate(core, refined)
  .settings(compileSettings)
  .settings(noPublishSettings)

lazy val core = project.in(file("core"))
  .settings(coreSettings)
  .settings(projectSettings)
  .settings(publishSettings)

lazy val refined = project.in(file("refined"))
  .dependsOn(core % "test")
  .settings(refinedSettings)
  .settings(projectSettings)
  .settings(publishSettings)
  .settings(only211Settings)

lazy val coreName = "argonaut-shapeless_6.1"

lazy val coreSettings = coreCompileSettings ++ Seq(
  organization := "com.github.alexarchambault",
  name := coreName,
  moduleName := coreName
)

lazy val refinedName = "argonaut-refined_6.1"

lazy val refinedSettings = refinedCompileSettings ++ Seq(
  organization := "com.github.alexarchambault",
  name := refinedName,
  moduleName := refinedName
)

lazy val projectSettings =
  compileSettings ++
  testSettings ++
  releaseSettings ++
  extraReleaseSettings

lazy val compileSettings = Seq(
  scalaVersion := "2.11.7",
  crossScalaVersions := Seq("2.10.5", "2.11.7"),
  scalacOptions += "-target:jvm-1.7",
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
)


lazy val coreCompileSettings = Seq(
  libraryDependencies ++= Seq(
    "io.argonaut" %% "argonaut" % "6.1",
    "com.github.alexarchambault" %% "shapeless" % "2.2.6-SNAPSHOT"
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

lazy val refinedCompileSettings = coreCompileSettings ++ Seq(
  libraryDependencies ++= {
    if (scalaVersion.value.startsWith("2.11."))
      Seq(
        "eu.timepit" %% "refined" % "0.2.2" exclude("com.chuusai", "shapeless_" + scalaBinaryVersion.value)
      )
    else
      Seq.empty
  }
)

lazy val testSettings = Seq(
  libraryDependencies ++= Seq(
    "com.github.alexarchambault" %% "scalacheck-shapeless_1.13" % "1.0.0-SNAPSHOT" % "test",
    "com.lihaoyi" %% "utest" % "0.3.0" % "test"
  ),
  testFrameworks += new TestFramework("utest.runner.Framework")
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

def onlyIn211[T](key: TaskKey[T], default: => T): Setting[Task[T]] = {
  key := {
    if (scalaVersion.value.startsWith("2.11."))
      key.value
    else
      default
  }
}

def onlyIn211[T](key: SettingKey[T], default: => T): Def.Setting[T] = {
  key := {
    if (scalaVersion.value.startsWith("2.11."))
      key.value
    else
      default
  }
}

lazy val only211Settings = Seq(
  onlyIn211(unmanagedSources in Compile, Seq.empty),
  onlyIn211(unmanagedSources in Test, Seq.empty),
  onlyIn211(publish, ()),
  onlyIn211(publishLocal, ()),
  onlyIn211(publishArtifact, false)
)

lazy val extraReleaseSettings = Seq(
  ReleaseKeys.versionBump := sbtrelease.Version.Bump.Bugfix,
  sbtrelease.ReleasePlugin.ReleaseKeys.publishArtifactsAction := PgpKeys.publishSigned.value
)

// build.sbt shamelessly inspired by https://github.com/fthomas/refined/blob/master/build.sbt
