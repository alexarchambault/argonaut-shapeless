
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
    name := "argonaut-shapeless_6.3",
    keepNameAsModuleName,
    libraryDependencies ++= Seq(
      Deps.argonaut.value,
      Deps.shapeless.value,
      Deps.scalacheckShapeless.value % Test
    ),
    utest,
    mimaPreviousArtifacts := Set.empty
  )

lazy val coreJVM = core.jvm
lazy val coreJS = core.js
lazy val coreNative = core.native

lazy val refined = crossProject(JVMPlatform, JSPlatform)
  .dependsOn(core % "test->test")
  .settings(
    shared,
    name := "argonaut-refined_6.3",
    libraryDependencies ++= Seq(
      Deps.argonaut.value,
      Deps.refined.value,
      Deps.shapeless.value,
      Deps.scalacheckShapeless.value % Test
    ),
    utest,
    keepNameAsModuleName,
    mimaPreviousArtifacts := Set.empty
  )

lazy val refinedJVM = refined.jvm
lazy val refinedJS = refined.js

lazy val doc = project
  .in(file("target/doc"))
  .enablePlugins(MdocPlugin)
  .disablePlugins(MimaPlugin)
  .dependsOn(coreJVM, refinedJVM)
  .settings(
    shared,
    (publish / skip) := true,
    mdocIn := (ThisBuild / baseDirectory).value / "doc",
    mdocOut := (ThisBuild / baseDirectory).value
  )


disablePlugins(MimaPlugin)
(publish / skip) := true
crossScalaVersions := Nil
