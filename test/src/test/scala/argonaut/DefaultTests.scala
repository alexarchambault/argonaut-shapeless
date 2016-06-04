package argonaut

import utest._

import argonaut.Argonaut._
import argonaut.ArgonautShapeless._

object DefaultTests extends TestSuite {

  case class WithDefaults(
    i: Int,
    s: String = "b"
  )

  val tests = TestSuite {
    'simple - {
      val encoder = EncodeJson.of[WithDefaults]
      val decoder = DecodeJson.of[WithDefaults]

      val value0 = WithDefaults(2, "a")
      val json0 = encoder.encode(value0)
      val expectedJson0 = Json.obj(
        "i" -> Json.jNumber(2),
        "s" -> Json.jString("a")
      )

      assert(json0 == expectedJson0)

      val value1 = WithDefaults(2)
      val json1 = encoder.encode(value1)
      val expectedJson1 = Json.obj(
        "i" -> Json.jNumber(2)
      )

      assert(json1 == expectedJson1)

      val result0 = decoder.decodeJson(expectedJson0)
      val expectedResult0 = DecodeResult.ok(value0)
      assert(result0 == expectedResult0)

      val result1 = decoder.decodeJson(expectedJson1)
      val expectedResult1 = DecodeResult.ok(value1)
      assert(result1 == expectedResult1)
    }
  }

}
