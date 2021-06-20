import sbt._
import sbt.Def.setting
import sbt.Keys.scalaVersion

import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Deps {
  def argonaut = setting("io.argonaut" %%% "argonaut" % "6.3.4")
  def refined = setting("eu.timepit" %%% "refined" % "0.9.25")
  def shapeless = setting("com.chuusai" %%% "shapeless" % "2.3.7")

  def scalacheckShapeless = setting("com.github.alexarchambault" %%% "scalacheck-shapeless_1.15" % "1.3.0")
  def utest = setting("com.lihaoyi" %%% "utest" % "0.7.10")
}
