package argonaut

import utest._
import Util._

import org.scalacheck.{ Arbitrary, Prop }

import shapeless.{ Lazy => _, _ }
import shapeless.compat._

import derive._

import org.scalacheck.Shapeless._

import Argonaut._, Shapeless._


case class WrappedMap(m: Map[String, Json])


object ProductEncodeTests extends TestSuite {
  
  lazy val expectedEmptyEncodeJson =
    MkEncodeJson.product(
      ProductEncodeJson.generic(
        LabelledGeneric[Empty.type],
        Default.AsOptions[Empty.type],
        Lazy(
          HListProductEncodeJson.hnil
        )
      ),
      JsonProductCodecFor.default
    ).encodeJson

  lazy val expectedEmptyCCEncodeJson =
    MkEncodeJson.product(
      ProductEncodeJson.generic(
        LabelledGeneric[EmptyCC],
        Default.AsOptions[EmptyCC],
        Lazy(
          HListProductEncodeJson.hnil
        )
      ),
      JsonProductCodecFor.default
    ).encodeJson

  lazy val expectedSimpleEncodeJson =
    MkEncodeJson.product(
      ProductEncodeJson.generic(
        LabelledGeneric[Simple],
        Default.AsOptions[Simple],
        Lazy(
          HListProductEncodeJson.hcons(
            Witness('i),
            Strict(IntEncodeJson),
            HListProductEncodeJson.hcons(
              Witness('s),
              Strict(StringEncodeJson),
              HListProductEncodeJson.hcons(
                Witness('blah),
                Strict(BooleanEncodeJson),
                HListProductEncodeJson.hnil
              )
            )
          )
        )
      ),
      JsonProductCodecFor.default
    ).encodeJson

  lazy val expectedComposedEncodeJson =
    MkEncodeJson.product(
      ProductEncodeJson.generic(
        LabelledGeneric[Composed],
        Default.AsOptions[Composed],
        Lazy(
          HListProductEncodeJson.hcons(
            Witness('foo),
            Strict(expectedSimpleEncodeJson),
            HListProductEncodeJson.hcons(
              Witness('other),
              Strict(StringEncodeJson),
              HListProductEncodeJson.hnil
            )
          )
        )
      ),
      JsonProductCodecFor.default
    ).encodeJson

  lazy val expectedSimpleWithJsEncodeJson =
    MkEncodeJson.product(
      ProductEncodeJson.generic(
        LabelledGeneric[SimpleWithJs],
        Default.AsOptions[SimpleWithJs],
        Lazy(
          HListProductEncodeJson.hcons(
            Witness('i),
            Strict(IntEncodeJson),
            HListProductEncodeJson.hcons(
              Witness('s),
              Strict(StringEncodeJson),
              HListProductEncodeJson.hcons(
                Witness('v),
                Strict(JsonEncodeJson),
                HListProductEncodeJson.hnil
              )
            )
          )
        )
      ),
      JsonProductCodecFor.default
    ).encodeJson

  lazy val expectedWrappedMapEncodeJson =
    MkEncodeJson.product(
      ProductEncodeJson.generic(
        LabelledGeneric[WrappedMap],
        Default.AsOptions[WrappedMap],
        Lazy(
          HListProductEncodeJson.hcons(
            Witness('m),
            Strict(MapLikeEncodeJson[Map, Json](JsonEncodeJson)),
            HListProductEncodeJson.hnil
          )
        )
      ),
      JsonProductCodecFor.default
    ).encodeJson

  lazy val expectedOIEncodeJson =
    MkEncodeJson.product(
      ProductEncodeJson.generic(
        LabelledGeneric[OI],
        Default.AsOptions[OI],
        Lazy(
          HListProductEncodeJson.hcons(
            Witness('oi),
            Strict(OptionEncodeJson[Int](IntEncodeJson)),
            HListProductEncodeJson.hnil
          )
        )
      ),
      JsonProductCodecFor.default
    ).encodeJson


  def compareEncodeJsons[T: Arbitrary](first: EncodeJson[T], second: EncodeJson[T]): Unit =
    Prop.forAll{
      t: T =>
        first.encode(t) == second.encode(t)
    }.validate

  def jsonIs[T: EncodeJson](t: T, json: Json): Unit = {
    assert(t.asJson == json)
  }


  val tests = TestSuite {

    'codec {
      'empty - {
        compareEncodeJsons(EncodeJson.of[Empty.type], expectedEmptyEncodeJson)
      }

      'emptyCC - {
        compareEncodeJsons(EncodeJson.of[EmptyCC], expectedEmptyCCEncodeJson)
      }

      'simple - {
        compareEncodeJsons(EncodeJson.of[Simple], expectedSimpleEncodeJson)
      }

      'composed - {
        compareEncodeJsons(EncodeJson.of[Composed], expectedComposedEncodeJson)
      }

      // Disabled, Arbitrary Json generation seems to take forever
      // 'simpleWithJs - {
      //   compareEncodeJsons(EncodeJson.of[SimpleWithJs], expectedSimpleWithJsEncodeJson)
      // }

      // Looks like not enough WrappedMap can be generated
      // 'wrappedMap - {
      //   val arb = Gen.resize(1000, Arbitrary.arbitrary[WrappedMap])
      //   compareEncodeJsons(EncodeJson.of[WrappedMap], expectedWrappedMapEncodeJson)(Arbitrary(arb))
      // }

      'withOption - {
        compareEncodeJsons(EncodeJson.of[OI], expectedOIEncodeJson)
      }
    }

    'output {
      'empty - {
        jsonIs(Empty, Json.obj())
      }

      'emptyCC - {
        jsonIs(EmptyCC(), Json.obj())
      }

      'simple - {
        jsonIs(
          Simple(41, "aa", blah = false),
          Json.obj(
            "i" -> Json.jNumber(41),
            "s" -> Json.jString("aa"),
            "blah" -> Json.jBool(false)
          )
        )
      }

      'composed - {
        jsonIs(
          Composed(Simple(41, "aa", blah = false), "bbb"),
          Json.obj(
            "foo" -> Json.obj(
              "i" -> Json.jNumber(41),
              "s" -> Json.jString("aa"),
              "blah" -> Json.jBool(false)
            ),
            "other" -> Json.jString("bbb")
          )
        )
      }

      'simpleWithJs - {
        jsonIs(
          SimpleWithJs(41, "aa", Json.jArray(List(Json.jNumber(10), Json.obj("a" -> Json.jBool(true))))),
          Json.obj(
            "i" -> Json.jNumber(41),
            "s" -> Json.jString("aa"),
            "v" -> Json.jArray(List(Json.jNumber(10), Json.obj("a" -> Json.jBool(true))))
          )
        )
      }

      'wrappedMap - {
        jsonIs(
          WrappedMap(Map(
            "aa" -> Json.jArray(List(Json.jNumber(10), Json.obj("a" -> Json.jBool(true)))),
            "bb" -> Json.obj("c" -> Json.jBool(false))
          )),
          Json.obj(
            "m" -> Json.obj(
              "aa" -> Json.jArray(List(Json.jNumber(10), Json.obj("a" -> Json.jBool(true)))),
              "bb" -> Json.obj("c" -> Json.jBool(false))
            )
          )
        )
      }
    }

  }

}
