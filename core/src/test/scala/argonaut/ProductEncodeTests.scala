package argonaut

import utest._
import Util._

import org.scalacheck.{ Arbitrary, Prop }

import shapeless._
import derive._
import org.scalacheck.Shapeless._
import Argonaut._, Shapeless._


case class WrappedMap(m: Map[String, Json])


object ProductEncodeTests extends TestSuite {
  
  lazy val expectedEmptyEncodeJson =
    MkEncodeJson.productEncodeJson(
      ProductEncodeJson.genericEncodeJson(
        LabelledGeneric[Empty.type],
        Lazy(
          HListProductEncodeJson.hnilEncodeJson
        )
      ),
      defaultJsonProductCodecFor
    ).encodeJson

  lazy val expectedEmptyCCEncodeJson =
    MkEncodeJson.productEncodeJson(
      ProductEncodeJson.genericEncodeJson(
        LabelledGeneric[EmptyCC],
        Lazy(
          HListProductEncodeJson.hnilEncodeJson
        )
      ),
      defaultJsonProductCodecFor
    ).encodeJson

  lazy val expectedSimpleEncodeJson =
    MkEncodeJson.productEncodeJson(
      ProductEncodeJson.genericEncodeJson(
        LabelledGeneric[Simple],
        Lazy(
          HListProductEncodeJson.hconsEncodeJson(
            Witness('i),
            Strict(IntEncodeJson),
            HListProductEncodeJson.hconsEncodeJson(
              Witness('s),
              Strict(StringEncodeJson),
              HListProductEncodeJson.hconsEncodeJson(
                Witness('blah),
                Strict(BooleanEncodeJson),
                HListProductEncodeJson.hnilEncodeJson
              )
            )
          )
        )
      ),
      defaultJsonProductCodecFor
    ).encodeJson

  lazy val expectedComposedEncodeJson =
    MkEncodeJson.productEncodeJson(
      ProductEncodeJson.genericEncodeJson(
        LabelledGeneric[Composed],
        Lazy(
          HListProductEncodeJson.hconsEncodeJson(
            Witness('foo),
            Strict(expectedSimpleEncodeJson),
            HListProductEncodeJson.hconsEncodeJson(
              Witness('other),
              Strict(StringEncodeJson),
              HListProductEncodeJson.hnilEncodeJson
            )
          )
        )
      ),
      defaultJsonProductCodecFor
    ).encodeJson

  lazy val expectedSimpleWithJsEncodeJson =
    MkEncodeJson.productEncodeJson(
      ProductEncodeJson.genericEncodeJson(
        LabelledGeneric[SimpleWithJs],
        Lazy(
          HListProductEncodeJson.hconsEncodeJson(
            Witness('i),
            Strict(IntEncodeJson),
            HListProductEncodeJson.hconsEncodeJson(
              Witness('s),
              Strict(StringEncodeJson),
              HListProductEncodeJson.hconsEncodeJson(
                Witness('v),
                Strict(JsonEncodeJson),
                HListProductEncodeJson.hnilEncodeJson
              )
            )
          )
        )
      ),
      defaultJsonProductCodecFor
    ).encodeJson

  lazy val expectedWrappedMapEncodeJson =
    MkEncodeJson.productEncodeJson(
      ProductEncodeJson.genericEncodeJson(
        LabelledGeneric[WrappedMap],
        Lazy(
          HListProductEncodeJson.hconsEncodeJson(
            Witness('m),
            Strict(MapLikeEncodeJson[Map, Json](JsonEncodeJson)),
            HListProductEncodeJson.hnilEncodeJson
          )
        )
      ),
      defaultJsonProductCodecFor
    ).encodeJson

  lazy val expectedOIEncodeJson =
    MkEncodeJson.productEncodeJson(
      ProductEncodeJson.genericEncodeJson(
        LabelledGeneric[OI],
        Lazy(
          HListProductEncodeJson.hconsEncodeJson(
            Witness('oi),
            Strict(OptionEncodeJson[Int](IntEncodeJson)),
            HListProductEncodeJson.hnilEncodeJson
          )
        )
      ),
      defaultJsonProductCodecFor
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
