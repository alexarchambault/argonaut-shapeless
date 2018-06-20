import sbt._
import sbt.Def.setting

import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Deps {
  def argonaut = setting("io.argonaut" %%% "argonaut" % "6.2.1")
  def refined = "eu.timepit" %% "refined" % "0.8.7"
  def shapeless = setting("com.chuusai" %%% "shapeless" % "2.3.3")

  def scalacheckShapeless = setting("com.github.alexarchambault" %%% "scalacheck-shapeless_1.13" % "1.1.8")
  def utest = setting("com.lihaoyi" %%% "utest" % "0.6.4")
}
