organization := "com.github.alexarchambault"

val _name = "argonaut-shapeless_6.1"

// See https://groups.google.com/forum/#!msg/simple-build-tool/-AempE1a358/z-sUFFV-cdgJ
moduleName := _name

name := _name

scalaVersion := "2.11.6"

crossScalaVersions := Seq("2.10.5", "2.11.6")

// Keeping some snapshot artifacts around for tests
// (for scalacheck 1.12.4-SNAPSHOT in particular)
resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

libraryDependencies ++= Seq(
  "io.argonaut" %% "argonaut" % "6.1",
  "com.chuusai" %% "shapeless" % "2.2.2",
  "org.scalatest" %% "scalatest" % "2.2.5" % "test",
  /* using scalacheck 1.12.4-SNAPSHOT because of
   * https://github.com/rickynils/scalacheck/issues/165 */
  "org.scalacheck" %% "scalacheck" % "1.12.4-SNAPSHOT" % "test",
  "com.github.alexarchambault" %% "scalacheck-shapeless_1.12" % "0.2.0-SNAPSHOT" % "test"
)

libraryDependencies ++= {
  if (scalaVersion.value startsWith "2.10.")
    Seq(
      compilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full)
    )
  else
    Seq()
}


xerial.sbt.Sonatype.sonatypeSettings

publishMavenStyle := true

licenses := Seq("BSD-3-Clause" -> url("http://www.opensource.org/licenses/BSD-3-Clause"))

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

pomExtra := {
  <url>https://github.com/alexarchambault/argonaut-shapeless</url>
  <scm>
    <connection>scm:git:github.com/alexarchambault/argonaut-shapeless.git</connection>
    <developerConnection>scm:git:git@github.com:alexarchambault/argonaut-shapeless.git</developerConnection>
    <url>github.com/alexarchambault/argonaut-shapeless.git</url>
  </scm>
  <developers>
    <developer>
      <id>alexarchambault</id>
      <name>Alexandre Archambault</name>
      <url>https://github.com/alexarchambault</url>
    </developer>
  </developers>
}

credentials += {
  Seq("SONATYPE_USER", "SONATYPE_PASS").map(sys.env.get) match {
    case Seq(Some(user), Some(pass)) =>
      Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", user, pass)
    case _ =>
      Credentials(Path.userHome / ".ivy2" / ".credentials")
  }
}

releaseSettings

ReleaseKeys.versionBump := sbtrelease.Version.Bump.Bugfix

sbtrelease.ReleasePlugin.ReleaseKeys.publishArtifactsAction := PgpKeys.publishSigned.value

scalacOptions += "-target:jvm-1.7"
