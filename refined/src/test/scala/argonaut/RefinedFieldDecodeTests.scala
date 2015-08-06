package argonaut

import eu.timepit.refined._, numeric._, collection._, boolean._
import shapeless.nat._3
import Argonaut._, Shapeless._, Refined._
import utest._

object RefinedFieldDecodeTests extends TestSuite {
  import RefinedFieldDefinitions._

  val tests = TestSuite {

    'simple - {
      val simple0 = Simple0(2, "aa", List.range(2, 6))
      val simpleT0 = SimpleT(refineMT(2), refineMT("aa"), refineT[Size[Greater[_3]]](List.range(2, 6)).right.get)
      val simpleV0 = SimpleV(refineMV(2), refineMV("aa"), refineV[Size[Greater[_3]]](List.range(2, 6)).right.get)

      val json = simple0.asJson

      val simple = json.as[Simple0].toDisjunction.getOrElse(???)
      // val simpleT = json.as[SimpleT].toDisjunction.getOrElse(???) // Doesn't compile
      val simpleV = json.as[SimpleV].toDisjunction.getOrElse(???)

      assert(simple == simple0)
      // assert(simpleT == simpleT0) // See above
      assert(simpleV == simpleV0)
    }

    'failure - {
      val simple0 = Simple0(2, "", List.range(2, 6))

      val json = simple0.asJson

      val simple = json.as[Simple0].toDisjunction.getOrElse(???)
      // val resultT = json.as[SimpleT] // Doesn't compile
      val resultV = json.as[SimpleV]

      assert(simple == simple0)
      // assert(resultT.isError) // See above
      assert(resultV.isError)
    }

  }

}
