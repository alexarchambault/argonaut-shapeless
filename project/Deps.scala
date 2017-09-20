import sbt._

object Deps {
  def argonaut = "io.argonaut" %% "argonaut" % "6.2"
  def refined = "eu.timepit" %% "refined" % "0.8.3"
  def shapeless = "com.chuusai" %% "shapeless" % "2.3.2"

  def scalacheckShapeless = "com.github.alexarchambault" %% "scalacheck-shapeless_1.13" % "1.1.5"
  def utest = "com.lihaoyi" %% "utest" % "0.5.3"
}
