package argonaut

import shapeless.cachedImplicit

// Defined here and not in Definitions.scala because of https://issues.scala-lang.org/browse/SI-7755
sealed trait Base
case class BaseIS(i: Int, s: String) extends Base
case class BaseDB(d: Double, b: Boolean) extends Base
case class BaseLast(c: Simple) extends Base


// Proxied implicits
object JsonCodecs {
  import Argonaut._, Shapeless._

  implicit val emptyEncodeJson: EncodeJson[Empty.type] = cachedImplicit
  implicit val emptyDecodeJson: DecodeJson[Empty.type] = cachedImplicit

  implicit val emptyCCEncodeJson: EncodeJson[EmptyCC] = cachedImplicit
  implicit val emptyCCDecodeJson: DecodeJson[EmptyCC] = cachedImplicit

  implicit val simpleEncodeJson: EncodeJson[Simple] = cachedImplicit
  implicit val simpleDecodeJson: DecodeJson[Simple] = cachedImplicit

  implicit val composedEncodeJson: EncodeJson[Composed] = cachedImplicit
  implicit val composedDecodeJson: DecodeJson[Composed] = cachedImplicit

  implicit val twiceComposedEncodeJson: EncodeJson[TwiceComposed] = cachedImplicit
  implicit val twiceComposedDecodeJson: DecodeJson[TwiceComposed] = cachedImplicit

  implicit val composedOptListEncodeJson: EncodeJson[ComposedOptList] = cachedImplicit
  implicit val composedOptListDecodeJson: DecodeJson[ComposedOptList] = cachedImplicit

  implicit val nowThreeEncodeJson: EncodeJson[NowThree] = cachedImplicit
  implicit val nowThreeDecodeJson: DecodeJson[NowThree] = cachedImplicit

  implicit val baseEncodeJson: EncodeJson[Base] = cachedImplicit
  implicit val baseDecodeJson: DecodeJson[Base] = cachedImplicit
}
