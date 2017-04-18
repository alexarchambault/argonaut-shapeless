
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
  .dependsOn(core % "test")
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

lazy val test = project
  .dependsOn(core, refined)
  .settings(
    shared,
    dontPublish,
    utest,
    libs += Deps.scalacheckShapeless % "test",
    scala211_12TestSources
  )

lazy val doc = project
  .dependsOn(core, refined)
  .settings(
    shared,
    dontPublish,
    simpleTut
  )


lazy val `argonaut-shapeless` = project
  .in(root)
  .aggregate(
    core,
    refined,
    test,
    doc
  )
  .settings(
    shared,
    dontPublish
  )

aliases(
  "validate" -> chain("test", "tut")
)
