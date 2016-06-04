package argonaut

import shapeless.Witness
import utest._
import argonaut.Argonaut._
import argonaut.ArgonautShapeless._

object SingletonTests extends TestSuite {

  val tests = TestSuite {
    'encode {
      'string - {
        val s: Witness.`"aa"`.T = "aa"
        val expectedJson = (s: String).asJson
        val json = s.asJson
        assert(json == expectedJson)
      }
      'int - {
        val n: Witness.`4`.T = 4
        val expectedJson = (n: Int).asJson
        val json = n.asJson
        assert(json == expectedJson)
      }
      'boolean - {
        val b: Witness.`true`.T = true
        val expectedJson = (b: Boolean).asJson
        val json = b.asJson
        assert(json == expectedJson)
      }

      // TODO Test other types
    }
  }

}
