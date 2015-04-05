package argonaut

import org.scalatest.prop.PropertyChecks
import org.scalatest.{ Matchers, PropSpec }
import org.scalacheck.Arbitrary
import shapeless.test.illTyped

import argonaut.Argonaut.ToJsonIdentity
import scalaz.Scalaz.ToEitherOps


class ShapelessTests extends PropSpec with Matchers with PropertyChecks {
  private def toFromJson[T: EncodeJson : DecodeJson](t: T): DecodeResult[T] = t.asJson.as[T]

  private def sameAfterBeforeSerialization[T: Arbitrary : EncodeJson : DecodeJson]: Unit =
    forAll { t: T =>
      toFromJson(t).result shouldBe t.right
    }

  import org.scalacheck.Shapeless._
  import JsonCodecs._

  property("Empty must not change after serialization/deserialization") {
    sameAfterBeforeSerialization[Empty.type]
  }

  property("EmptyCC must not change after serialization/deserialization") {
    sameAfterBeforeSerialization[EmptyCC]
  }

  property("Simple must not change after serialization/deserialization") {
    sameAfterBeforeSerialization[Simple]
  }

  property("Composed must not change after serialization/deserialization") {
    sameAfterBeforeSerialization[Composed]
  }

  property("TwiceComposed must not change after serialization/deserialization") {
    sameAfterBeforeSerialization[TwiceComposed]
  }

  property("ComposedOptList must not change after serialization/deserialization") {
    sameAfterBeforeSerialization[ComposedOptList]
  }

  property("NowThree must not change after serialization/deserialization") {
    sameAfterBeforeSerialization[NowThree]
  }

  property("Base must not change after serialization/deserialization") {
    sameAfterBeforeSerialization[Base]
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

  // This one raises StackOverflowError
  // property("SimpleWithJs must not change after serialization/deserialization") {
  //   sameAfterBeforeSerialization[SimpleWithJs]
  // }
}
