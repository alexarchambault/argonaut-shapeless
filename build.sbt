organization := "com.github.alexarchambault"

name := "argonaut-shapeless"

version := "6.1-SNAPSHOT"

scalaVersion := "2.10.4"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

libraryDependencies ++= Seq(
  "io.argonaut" %% "argonaut" % "6.1-SNAPSHOT" changing(),
  "com.chuusai" %% "shapeless" % "2.1.0-SNAPSHOT" cross CrossVersion.full,
  // For shapeless LabelledGeneric to work
  // You may need to add it to your own project too...
  compilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full),
  "org.scalatest" %% "scalatest" % "2.2.2" % "test",
  "org.scalacheck" %% "scalacheck" % "1.11.5" % "test"
)


xerial.sbt.Sonatype.sonatypeSettings

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

pomExtra := {
  <url>https://github.com/alexarchambault/argonaut-shapeless</url>
    <licenses>
      <license>
        <name>Apache 2.0</name>
        <url>http://opensource.org/licenses/Apache-2.0</url>
      </license>
    </licenses>
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

