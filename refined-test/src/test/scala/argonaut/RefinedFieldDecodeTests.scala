package argonaut

import eu.timepit.refined._, numeric._, collection._, boolean._
import shapeless.nat._3
import argonaut.Argonaut._
import argonaut.ArgonautRefined._
import argonaut.ArgonautShapeless._
import utest._

object RefinedFieldDecodeTests extends TestSuite {
  import RefinedFieldDefinitions._

  val tests = TestSuite {

    'simple - {
      val simple0 = Simple0(2, "aa", List.range(2, 6))
      val simpleV0 = SimpleV(refineMV(2), refineMV("aa"), refineV[Size[Greater[_3]]](List.range(2, 6)).right.get)

      val json = simple0.asJson

      val simple = json.as[Simple0].getOr(???)
      val simpleV = json.as[SimpleV].getOr(???)

      assert(simple == simple0)
      assert(simpleV == simpleV0)
    }

    'failure - {
      val simple0 = Simple0(2, "", List.range(2, 6))

      val json = simple0.asJson

      val simple = json.as[Simple0].getOr(???)
      val resultV = json.as[SimpleV]

      assert(simple == simple0)
      assert(resultV.isError)
    }

  }

}
