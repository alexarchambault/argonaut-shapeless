package argonaut

import eu.timepit.refined._, api._, numeric._, collection._, boolean._
import shapeless.nat.{ _0, _3 }
import argonaut.Argonaut._
import argonaut.ArgonautRefined._
import shapeless.tag.@@
import utest._

object Refined211DecodeTests extends TestSuite {

  val tests = TestSuite {
    'posInt - {
      val n0 = 41
      val nT0 = refineMT[Positive](41)

      val json = n0.asJson

      val n = json.as[Int].toDisjunction.getOrElse(???)
      val nT = json.as[Int @@ Positive].toDisjunction.getOrElse(???)

      assert(n == n0)
      assert(nT == nT0)
    }

    'string - {
      val s0 = "aa"
      val sT0 = refineMT[NonEmpty]("aa")

      val json = s0.asJson

      val s = json.as[String].toDisjunction.getOrElse(???)
      val sT = json.as[String @@ NonEmpty].toDisjunction.getOrElse(???)

      assert(s == s0)
      assert(sT == sT0)
    }

    'intAnd - {
      val n0 = 2
      val nT0 = refineMT[Not[Less[_0]] And Not[Greater[_3]]](2)

      val json = n0.asJson

      val n = json.as[Int].toDisjunction.getOrElse(???)
      val nT = json.as[Int @@ (Not[Less[_0]] And Not[Greater[_3]])].toDisjunction.getOrElse(???)

      assert(n == n0)
      assert(nT == nT0)
    }

    'size - {
      val n0 = List.range(1, 5)
      val nT0 = refineT[Size[Greater[_3]]](List.range(1, 5)).right.get

      val json = n0.asJson

      val n = json.as[List[Int]].toDisjunction.getOrElse(???)
      val nT = json.as[List[Int] @@ Size[Greater[_3]]].toDisjunction.getOrElse(???)

      assert(n == n0)
      assert(nT == nT0)
    }

    'failures - {
      * - {
        val json = 2 .asJson
        val result0 = json.as[Int]
        val resultT = json.as[Int @@ Negative]
        assert(!result0.isError)
        assert(resultT.isError)
      }

      * - {
        val json1 = (-1) .asJson
        val json2 = 5 .asJson
        val result1 = json1.as[Int]
        val result2 = json2.as[Int]
        val resultT1 = json1.as[Int @@ (Not[Less[_0]] And Not[Greater[_3]])]
        val resultT2 = json2.as[Int @@ (Not[Less[_0]] And Not[Greater[_3]])]
        assert(!result1.isError)
        assert(!result2.isError)
        assert(resultT1.isError)
        assert(resultT2.isError)
      }
    }

  }

}
