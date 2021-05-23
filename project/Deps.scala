import sbt._
import sbt.Def.setting
import sbt.Keys.scalaVersion

import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Deps {
  def argonaut = setting("io.argonaut" %%% "argonaut" % "6.3.3")
  def refined = "eu.timepit" %% "refined" % "0.9.12"
  def shapeless = setting("com.chuusai" %%% "shapeless" % "2.3.7")

  def scalacheckShapeless = setting("com.github.alexarchambault" %%% "scalacheck-shapeless_1.14" % "1.2.5")
  def utest = setting("com.lihaoyi" %%% "utest" % "0.7.4")
}
