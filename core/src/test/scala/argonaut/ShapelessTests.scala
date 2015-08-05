package argonaut

import argonaut.Argonaut.ToJsonIdentity
import scalaz.Scalaz.ToEitherOps

import org.scalacheck.{ Arbitrary, Prop }
import shapeless.test.illTyped
import utest._
import Util._


object ShapelessTests extends TestSuite {
  private def toFromJson[T: EncodeJson : DecodeJson](t: T): DecodeResult[T] = t.asJson.as[T]

  private def sameAfterBeforeSerialization[T: Arbitrary : EncodeJson : DecodeJson]: Unit =
    Prop.forAll {
      t: T =>
        toFromJson(t).result == t.right
    }.validate

  import org.scalacheck.Shapeless._
  import JsonCodecs._

  val tests = TestSuite {
    'serializeDeserialize {
      'empty - {
        sameAfterBeforeSerialization[Empty.type]
      }

      'emptyCC - {
        sameAfterBeforeSerialization[EmptyCC]
      }

      'simple - {
        sameAfterBeforeSerialization[Simple]
      }

      'composed - {
        sameAfterBeforeSerialization[Composed]
      }

      'twiceComposed - {
        sameAfterBeforeSerialization[TwiceComposed]
      }

      'composedOptList - {
        sameAfterBeforeSerialization[ComposedOptList]
      }

      'nowThree - {
        sameAfterBeforeSerialization[NowThree]
      }

      'oi - {
        sameAfterBeforeSerialization[OI]
      }

      'oiLoose - {
        val json = Parse.parseOption("{}").get
        assert(json.as[OI].result == OI(None).right)
      }

      'base {
        sameAfterBeforeSerialization[Base]
      }
    }
  }

  {
    import Shapeless._

    illTyped(" implicitly[EncodeJson[NoArbitraryType]] ")
    illTyped(" implicitly[DecodeJson[NoArbitraryType]] ")
    illTyped(" implicitly[EncodeJson[ShouldHaveNoArb]] ")
    illTyped(" implicitly[DecodeJson[ShouldHaveNoArb]] ")
    illTyped(" implicitly[EncodeJson[ShouldHaveNoArbEither]] ")
    illTyped(" implicitly[DecodeJson[ShouldHaveNoArbEither]] ")
    illTyped(" implicitly[EncodeJson[BaseNoArb]] ")
    illTyped(" implicitly[DecodeJson[BaseNoArb]] ")
  }

  // This one raises StackOverflowError, possibly because of automatic Arbitrary[Json] derivation
  // property("SimpleWithJs must not change after serialization/deserialization") {
  //   sameAfterBeforeSerialization[SimpleWithJs]
  // }
}
