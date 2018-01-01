package argonaut

import eu.timepit.refined._, numeric._, collection._, boolean._
import shapeless.nat.{ _0, _3 }
import argonaut.Argonaut._
import argonaut.ArgonautRefined._
import utest._

object RefinedEncodeTests extends TestSuite {

  val tests = TestSuite {
    'posIntV - {
      val n = refineMV[Positive](43)
      val n0 = 43
      val json = n.asJson
      val expectedJson = n0.asJson
      assert(json == expectedJson)
    }

    'stringV - {
      val s = refineMV[NonEmpty]("aa")
      val s0 = "aa"
      val json = s.asJson
      val expectedJson = s0.asJson
      assert(json == expectedJson)
    }

    'intAndV - {
      val n = refineMV[Not[Less[_0]] And Not[Greater[_3]]](2)
      val n0 = 2
      val json = n.asJson
      val expectedJson = n0.asJson
      assert(json == expectedJson)
    }

    'sizeV - {
      val l = refineV[Size[Greater[_3]]](List.range(1, 5)).right.get
      val l0 = List.range(1, 5)
      val json = l.asJson
      val expectedJson = l0.asJson
      assert(json == expectedJson)
    }
  }

}
