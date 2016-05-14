package argonaut

import eu.timepit.refined._, api._, numeric._, collection._, boolean._
import shapeless.tag.@@
import shapeless.nat._3
import Argonaut._, Shapeless._, Refined._
import utest._

object RefinedFieldDefinitions {

  case class Simple0(
    i: Int,
    s: String,
    l: List[Int]
  )

  case class SimpleV(
    i: Refined[Int, Positive],
    s: Refined[String, NonEmpty],
    l: Refined[List[Int], Size[Greater[_3]]]
  )

}

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
