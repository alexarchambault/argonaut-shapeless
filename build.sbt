
import Aliases._
import Settings._

import sbtcrossproject.crossProject

inThisBuild(List(
  organization := "com.github.alexarchambault",
  homepage := Some(url("https://github.com/alexarchambault/argonaut-shapeless")),
  licenses := Seq("BSD-3-Clause" -> url("http://www.opensource.org/licenses/BSD-3-Clause")),
  developers := List(
    Developer(
      "alexarchambault",
      "Alexandre Archambault",
      "",
      url("https://github.com/alexarchambault")
    )
  )
))

lazy val core = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .settings(
    shared,
    name := "argonaut-shapeless_6.2",
    libs ++= Seq(
      Deps.argonaut.value,
      Deps.shapeless.value
    ),
    keepNameAsModuleName
  )

lazy val coreJVM = core.jvm
lazy val coreJS = core.js
lazy val coreNative = core.native

lazy val refined = project
  .settings(
    shared, 
    name := "argonaut-refined_6.2",
    libs ++= Seq(
      Deps.argonaut.value,
      Deps.refined,
      Deps.shapeless.value
    ),
    keepNameAsModuleName
  )

lazy val coreTest = crossProject(JVMPlatform, JSPlatform)
  .in(file("core-test"))
  .disablePlugins(MimaPlugin)
  .dependsOn(core)
  .settings(
    shared,
    skip.in(publish) := true,
    utest,
    libs += Deps.scalacheckShapeless.value % Test
  )

lazy val coreTestJVM = coreTest.jvm
lazy val coreTestJS = coreTest.js

lazy val `refined-test` = project
  .disablePlugins(MimaPlugin)
  .dependsOn(coreTestJVM, refined)
  .settings(
    shared,
    skip.in(publish) := true,
    utest,
    libs += Deps.scalacheckShapeless.value % Test
  )

lazy val doc = project
  .in(file("target/doc"))
  .enablePlugins(MdocPlugin)
  .disablePlugins(MimaPlugin)
  .dependsOn(coreJVM, refined)
  .settings(
    shared,
    skip.in(publish) := true,
    // Ideally, I'd like
    // crossScalaVersions := crossScalaVersions.value.filter(!_.startsWith("2.11."))
    // but one can't run '++2.11.12 mdoc' anymore then
    mdocIn := {
      val dir = baseDirectory.in(ThisBuild).value / "doc"
      if (scalaVersion.value.startsWith("2.11."))
        dir / "dummy"
      else
        dir
    },
    mdocOut := baseDirectory.in(ThisBuild).value
  )


disablePlugins(MimaPlugin)
skip.in(publish) := true
crossScalaVersions := Nil
