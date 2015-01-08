package argonaut
package shapeless


import Argonaut._, Shapeless._
import scalaz._, Scalaz._
import org.scalatest.prop.PropertyChecks
import org.scalatest.{Matchers, PropSpec}
import org.scalacheck.Arbitrary
import org.scalacheck.Shapeless._
import _root_.shapeless.test.illTyped

// These case classes / ADTs are the same as in scalacheck-shapeless

/*
 * We should have codecs for these
 */
case object Empty
case class EmptyCC()
case class Simple(i: Int, s: String, blah: Boolean)
case class Composed(foo: Simple, other: String)
case class TwiceComposed(foo: Simple, bar: Composed, v: Int)
case class ComposedOptList(fooOpt: Option[Simple], other: String, l: List[TwiceComposed])


case class SimpleWithJs(i: Int, s: String, v: Json)

case class NowThree(s: String, i: Int, n: Double)

sealed trait Base
case class BaseIS(i: Int, s: String) extends Base
case class BaseDB(d: Double, b: Boolean) extends Base
case class BaseLast(c: Simple) extends Base


/*
 * We should *not* have codecs for these
 */
trait NoArbitraryType
case class ShouldHaveNoArb(n: NoArbitraryType, i: Int)
case class ShouldHaveNoArbEither(s: String, i: Int, n: NoArbitraryType)

sealed trait BaseNoArb
case class BaseNoArbIS(i: Int, s: String) extends BaseNoArb
case class BaseNoArbDB(d: Double, b: Boolean) extends BaseNoArb
case class BaseNoArbN(n: NoArbitraryType) extends BaseNoArb


class Tests extends PropSpec with Matchers with PropertyChecks {
  private def toFromJson[T: EncodeJson : DecodeJson](t: T): DecodeResult[T] = t.asJson.as[T]

  private def sameAfterBeforeSerialization[T: Arbitrary : EncodeJson : DecodeJson]: Unit =
    forAll { t: T =>
      toFromJson(t).result shouldBe t.right
    }

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

  illTyped(" implicitly[EncodeJson[NoArbitraryType]] ")
  illTyped(" implicitly[DecodeJson[NoArbitraryType]] ")
  illTyped(" implicitly[EncodeJson[ShouldHaveNoArb]] ")
  illTyped(" implicitly[DecodeJson[ShouldHaveNoArb]] ")
  illTyped(" implicitly[EncodeJson[ShouldHaveNoArbEither]] ")
  illTyped(" implicitly[DecodeJson[ShouldHaveNoArbEither]] ")
  illTyped(" implicitly[EncodeJson[BaseNoArb]] ")
  illTyped(" implicitly[DecodeJson[BaseNoArb]] ")

  // This one raises StackOverflowError
  // property("SimpleWithJs must not change after serialization/deserialization") {
  //   sameAfterBeforeSerialization[SimpleWithJs]
  // }
}
