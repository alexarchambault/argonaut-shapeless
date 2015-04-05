package argonaut

// Defined here and not in Definitions.scala because of https://issues.scala-lang.org/browse/SI-7755
sealed trait Base
case class BaseIS(i: Int, s: String) extends Base
case class BaseDB(d: Double, b: Boolean) extends Base
case class BaseLast(c: Simple) extends Base


// Proxied implicits
object JsonCodecs {
  import Argonaut._

  // Equivalent to
  //   import Shapeless._

  import GenericEncodeJsons.{
    hnilJsObjectEncodeJson, hconsJsObjectEncodeJson,
    cnilEncodeJsonFails,    cconsJsObjectEncodeJson,
    defaultInstanceEncodeJson
  }

  import GenericDecodeJsons.{
    hnilLooseJsObjectDecodeJson, stopAtFirstErrorHConsJsObjectDecodeJson,
    cnilDecodeJsonFails,         cconsAsJsObjectDecodeJson,
    defaultInstanceDecodeJson
  }

  implicit val emptyEncodeJson = EncodeJson.of[Empty.type]
  implicit val emptyDecodeJson = DecodeJson.of[Empty.type]

  implicit val emptyCCEncodeJson = EncodeJson.of[EmptyCC]
  implicit val emptyCCDecodeJson = DecodeJson.of[EmptyCC]

  implicit val simpleEncodeJson = EncodeJson.of[Simple]
  implicit val simpleDecodeJson = DecodeJson.of[Simple]

  implicit val composedEncodeJson = EncodeJson.of[Composed]
  implicit val composedDecodeJson = DecodeJson.of[Composed]

  implicit val twiceComposedEncodeJson = EncodeJson.of[TwiceComposed]
  implicit val twiceComposedDecodeJson = DecodeJson.of[TwiceComposed]

  implicit val composedOptListEncodeJson = EncodeJson.of[ComposedOptList]
  implicit val composedOptListDecodeJson = DecodeJson.of[ComposedOptList]

  implicit val nowThreeEncodeJson = EncodeJson.of[NowThree]
  implicit val nowThreeDecodeJson = DecodeJson.of[NowThree]

  implicit val baseEncodeJson = EncodeJson.of[Base]
  implicit val baseDecodeJson = DecodeJson.of[Base]
}
