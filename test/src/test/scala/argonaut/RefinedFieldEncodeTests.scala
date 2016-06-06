package argonaut

import eu.timepit.refined._, numeric._, collection._, boolean._

import shapeless.nat._3

import argonaut.Argonaut._
import argonaut.ArgonautRefined._
import argonaut.ArgonautShapeless._

import utest._

object RefinedFieldEncodeTests extends TestSuite {
  import RefinedFieldDefinitions._

  val tests = TestSuite {

    'simple - {
      val simple0 = Simple0(2, "aa", List.range(2, 6))
      val simpleV = SimpleV(refineMV(2), refineMV("aa"), refineV[Size[Greater[_3]]](List.range(2, 6)).right.get)

      val expectedJson = simple0.asJson
      val jsonV = simpleV.asJson

      assert(expectedJson == jsonV)
    }
  }
}
