package argonaut

import eu.timepit.refined._, numeric._, collection._, boolean._
import shapeless.nat.{ _0, _3 }
import Argonaut._, Refined._
import utest._

object Refined211EncodeTests extends TestSuite {

  val tests = TestSuite {
    'posIntT - {
      val n = refineMT[Positive](41)
      val n0 = 41
      val json = n.asJson
      val expectedJson = n0.asJson
      assert(json == expectedJson)
    }

    'stringT - {
      val s = refineMT[NonEmpty]("aa")
      val s0 = "aa"
      val json = s.asJson
      val expectedJson = s0.asJson
      assert(json == expectedJson)
    }

    'intAndT - {
      val n = refineMT[Not[Less[_0]] And Not[Greater[_3]]](2)
      val n0 = 2
      val json = n.asJson
      val expectedJson = n0.asJson
      assert(json == expectedJson)
    }

    'sizeT - {
      val l = refineT[Size[Greater[_3]]](List.range(1, 5)).right.get
      val l0 = List.range(1, 5)
      val json = l.asJson
      val expectedJson = l0.asJson
      assert(json == expectedJson)
    }
  }

}
