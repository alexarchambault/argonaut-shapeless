import sbt._
import sbt.Def.setting

import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import scala.scalanative.sbtplugin.NativePlatform

object Deps {
  def argonaut = setting("io.argonaut" %%% "argonaut" % "6.2.1")
  def refined = "eu.timepit" %% "refined" % "0.8.5"
  def shapeless = setting {
    if (crossPlatform.value == NativePlatform)
      "com.github.alexarchambault" %%% "shapeless" % "2.3.3-pre-1"
    else
      "com.chuusai" %%% "shapeless" % "2.3.2"
  }

  def scalacheckShapeless = "com.github.alexarchambault" %% "scalacheck-shapeless_1.13" % "1.1.5"
  def utest = "com.lihaoyi" %% "utest" % "0.6.2"
}
