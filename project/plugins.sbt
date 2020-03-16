addSbtPlugin("com.geirsson" % "sbt-ci-release" % "1.5.2")
addSbtPlugin("org.scalameta" % "sbt-mdoc" % "2.1.4")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.0.0")
addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.3.9")
addSbtPlugin("org.portable-scala" % "sbt-scala-native-crossproject" % "1.0.0")

val scalaJsVersion = Option(System.getenv("SCALAJS_VERSION")).getOrElse("1.0.0")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % scalaJsVersion)
