
import Aliases._
import Settings._

lazy val core = project
  .settings(
    shared,
    name := "argonaut-shapeless_6.2",
    libs ++= Seq(
      Deps.argonaut,
      Deps.shapeless
    ),
    keepNameAsModuleName,
    scala211_12Sources
  )

lazy val refined = project
  .settings(
    shared, 
    name := "argonaut-refined_6.2",
    libs ++= Seq(
      Deps.argonaut,
      Deps.refined,
      Deps.shapeless
    ),
    keepNameAsModuleName
  )

lazy val `core-test` = project
  .dependsOn(core)
  .settings(
    shared,
    dontPublish,
    utest,
    libs += Deps.scalacheckShapeless % "test",
    scala211_12TestSources
  )

lazy val `refined-test` = project
  .dependsOn(`core-test`, refined)
  .settings(
    shared,
    dontPublish,
    utest,
    libs += Deps.scalacheckShapeless % "test",
    scala211_12TestSources
  )

lazy val doc = project
  .enablePlugins(TutPlugin)
  .dependsOn(core, refined)
  .settings(
    shared,
    dontPublish,
    tutSourceDirectory := baseDirectory.value,
    tutTargetDirectory := baseDirectory.value / ".."
  )


lazy val `argonaut-shapeless` = project
  .in(root)
  .aggregate(
    core,
    `core-test`,
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
