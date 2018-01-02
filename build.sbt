
import Aliases._
import Settings._

import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

lazy val core = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .settings(
    shared,
    name := "argonaut-shapeless_6.2",
    libs ++= Seq(
      Deps.argonaut.value,
      Deps.shapeless.value
    ),
    keepNameAsModuleName,
    scala211_12Sources
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
    libs += Deps.scalacheckShapeless.value % "test",
    scala211_12TestSources
  )

lazy val coreTestJVM = coreTest.jvm
lazy val coreTestJS = coreTest.js

lazy val `refined-test` = project
  .dependsOn(coreTestJVM, refined)
  .settings(
    shared,
    dontPublish,
    utest,
    libs += Deps.scalacheckShapeless.value % "test",
    scala211_12TestSources
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


lazy val native = project
  .in(file("target/native"))
  .aggregate(
    coreNative
  )
  .settings(
    shared,
    dontPublish
  )

lazy val `argonaut-shapeless` = project
  .in(root)
  .aggregate(
    coreJVM,
    coreJS,
    coreTestJVM,
    coreTestJS,
    refined,
    `refined-test`,
    doc
  )
  .settings(
    shared,
    dontPublish
  )

aliases(
  "validate" -> chain("test", "tut")
)
