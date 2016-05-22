package argonaut

import eu.timepit.refined._, api._, numeric._, collection._, boolean._
import shapeless.nat.{ _0, _3 }
import argonaut.Argonaut._
import argonaut.ArgonautRefined._
import shapeless.tag.@@
import utest._

object RefinedDecodeTests extends TestSuite {

  val tests = TestSuite {
    'posInt - {
      val n0 = 41
      val nV0 = refineMV[Positive](41)

      val json = n0.asJson

      val n = json.as[Int].toDisjunction.getOrElse(???)
      val nV = json.as[Refined[Int, Positive]].toDisjunction.getOrElse(???)

      assert(n == n0)
      assert(nV == nV0)
    }

    'string - {
      val s0 = "aa"
      val sV0 = refineMV[NonEmpty]("aa")

      val json = s0.asJson

      val s = json.as[String].toDisjunction.getOrElse(???)
      val sV = json.as[Refined[String, NonEmpty]].toDisjunction.getOrElse(???)

      assert(s == s0)
      assert(sV == sV0)
    }

    'intAnd - {
      val n0 = 2
      val nV0 = refineMV[Not[Less[_0]] And Not[Greater[_3]]](2)

      val json = n0.asJson

      val n = json.as[Int].toDisjunction.getOrElse(???)
      val nV = json.as[Refined[Int, Not[Less[_0]] And Not[Greater[_3]]]].toDisjunction.getOrElse(???)

      assert(n == n0)
      assert(nV == nV0)
    }

    'size - {
      val n0 = List.range(1, 5)
      val nV0 = refineV[Size[Greater[_3]]](List.range(1, 5)).right.get

      val json = n0.asJson

      val n = json.as[List[Int]].toDisjunction.getOrElse(???)
      val nV = json.as[Refined[List[Int], Size[Greater[_3]]]].toDisjunction.getOrElse(???)

      assert(n == n0)
      assert(nV == nV0)
    }

    'failures - {
      * - {
        val json = 2 .asJson
        val result0 = json.as[Int]
        val resultV = json.as[Refined[Int, Negative]]
        assert(!result0.isError)
        assert(resultV.isError)
      }

      * - {
        val json1 = (-1) .asJson
        val json2 = 5 .asJson
        val result1 = json1.as[Int]
        val result2 = json2.as[Int]
        val resultV1 = json1.as[Refined[Int, Not[Less[_0]] And Not[Greater[_3]]]]
        val resultV2 = json2.as[Refined[Int, Not[Less[_0]] And Not[Greater[_3]]]]
        assert(!result1.isError)
        assert(!result2.isError)
        assert(resultV1.isError)
        assert(resultV2.isError)
      }
    }

  }

}
