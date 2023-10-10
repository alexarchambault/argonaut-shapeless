import sbt._
import sbt.Def.setting
import sbt.Keys.scalaVersion

import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Deps {
  def argonaut = setting("io.argonaut" %%% "argonaut" % "6.3.9")
  def refined = setting("eu.timepit" %%% "refined" % "0.10.1")
  def shapeless = setting("com.chuusai" %%% "shapeless" % "2.3.10")

  def scalacheckShapeless = setting("com.github.alexarchambault" %%% "scalacheck-shapeless_1.15" % "1.3.0")
  def utest = setting("com.lihaoyi" %%% "utest" % "0.8.1")
}
