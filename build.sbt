
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
  .dependsOn(core)
  .settings(
    shared,
    dontPublish,
    utest,
    libs += Deps.scalacheckShapeless.value % Test
  )

lazy val coreTestJVM = coreTest.jvm
lazy val coreTestJS = coreTest.js

lazy val `refined-test` = project
  .dependsOn(coreTestJVM, refined)
  .settings(
    shared,
    dontPublish,
    utest,
    libs += Deps.scalacheckShapeless.value % Test
  )

lazy val doc = project
  .enablePlugins(TutPlugin)
  .dependsOn(coreJVM, refined)
  .settings(
    shared,
    dontPublish,
    tutSourceDirectory := baseDirectory.value,
    tutTargetDirectory := baseDirectory.value / ".."
  )


skip.in(publish) := true
crossScalaVersions := Nil
