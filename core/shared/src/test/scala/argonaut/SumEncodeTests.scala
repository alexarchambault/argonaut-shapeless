package argonaut

import utest._

import argonaut.Argonaut._
import argonaut.ArgonautShapeless._
import org.scalacheck.ScalacheckShapeless._

import shapeless._

import derive._


object SumEncodeTests extends TestSuite {
  import ProductEncodeTests.{ compareEncodeJsons, jsonIs }

  lazy val expectedBaseISEncodeJson =
    MkEncodeJson.product(
      ProductEncodeJson.generic(
        LabelledGeneric[BaseIS],
        Default.AsOptions[BaseIS],
        Lazy(
          HListProductEncodeJson.hcons(
            Witness('i),
            Strict(IntEncodeJson),
            HListProductEncodeJson.hcons(
              Witness('s),
              Strict(StringEncodeJson),
              HListProductEncodeJson.hnil
            )
          )
        )
      ),
      JsonProductCodecFor.default
    ).encodeJson

  lazy val expectedBaseDBEncodeJson =
    MkEncodeJson.product(
      ProductEncodeJson.generic(
        LabelledGeneric[BaseDB],
        Default.AsOptions[BaseDB],
        Lazy(
          HListProductEncodeJson.hcons(
            Witness('d),
            Strict(DoubleEncodeJson),
            HListProductEncodeJson.hcons(
              Witness('b),
              Strict(BooleanEncodeJson),
              HListProductEncodeJson.hnil
            )
          )
        )
      ),
      JsonProductCodecFor.default
    ).encodeJson

  lazy val expectedBaseLastEncodeJson =
    MkEncodeJson.product(
      ProductEncodeJson.generic(
        LabelledGeneric[BaseLast],
        Default.AsOptions[BaseLast],
        Lazy(
          HListProductEncodeJson.hcons(
            Witness('c),
            Strict(ProductEncodeTests.expectedSimpleEncodeJson),
            HListProductEncodeJson.hnil
          )
        )
      ),
      JsonProductCodecFor.default
    ).encodeJson

  lazy val expectedBaseEncodeJson = expectedBaseEncodeJsonFor(JsonSumCodecFor.default)
  lazy val expectedBaseEncodeJsonTypeField = expectedBaseEncodeJsonFor(JsonSumCodecFor(JsonSumCodec.typeField))
  def expectedBaseEncodeJsonFor(codecFor: JsonSumCodecFor[Base]) =
    MkEncodeJson.sum(
      SumEncodeJson.generic(
        LabelledGeneric[Base],
        CoproductSumEncodeJson.ccons(
          Witness('BaseDB),
          expectedBaseDBEncodeJson,
          CoproductSumEncodeJson.ccons(
            Witness('BaseIS),
            expectedBaseISEncodeJson,
            CoproductSumEncodeJson.ccons(
              Witness('BaseLast),
              expectedBaseLastEncodeJson,
              CoproductSumEncodeJson.cnil
            )
          )
        )
      ),
      codecFor
    ).encodeJson

  val derivedBaseEncodeJsonTypeField = {
    implicit val codecFor = JsonSumCodecFor[Base](JsonSumCodec.typeField)
    EncodeJson.of[Base]
  }

  val tests = TestSuite {

    'codec {
      'base - {
        compareEncodeJsons(EncodeJson.of[Base], expectedBaseEncodeJson)
      }

      'baseTypeField - {
        compareEncodeJsons(derivedBaseEncodeJsonTypeField, expectedBaseEncodeJsonTypeField)
      }
    }

    'output {
      'base - {
        jsonIs(
          BaseLast(Simple(41, "aa", blah = false)): Base,
          Json.obj(
            "BaseLast" -> Json.obj(
              "c" -> Json.obj(
                "i" -> Json.jNumber(41),
                "s" -> Json.jString("aa"),
                "blah" -> Json.jBool(false)
              )
            )
          )
        )

        jsonIs(
          BaseIS(43, "aa"): Base,
          Json.obj(
            "BaseIS" -> Json.obj(
              "i" -> Json.jNumber(43),
              "s" -> Json.jString("aa")
            )
          )
        )

        jsonIs(
          BaseDB(3.2, false): Base,
          Json.obj(
            "BaseDB" -> Json.obj(
              "d" -> Json.jNumber(3.2),
              "b" -> Json.jBool(false)
            )
          )
        )
      }
    }

  }

}
