package argonaut

// These case classes / ADTs were originally the same as in scalacheck-shapeless

/*
 * We should have codecs for these
 */
case object Empty
case class EmptyCC()
case class Simple(i: Int, s: String, blah: Boolean)
case class Composed(foo: Simple, other: String)
case class TwiceComposed(foo: Simple, bar: Composed, v: Int)
case class ComposedOptList(fooOpt: Option[Simple], other: String, l: List[TwiceComposed])

case class OI(oi: Option[Int])

case class SimpleWithJs(i: Int, s: String, v: Json)

case class NowThree(s: String, i: Int, n: Double)

// See also Base in DefaultFormats.scala

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
