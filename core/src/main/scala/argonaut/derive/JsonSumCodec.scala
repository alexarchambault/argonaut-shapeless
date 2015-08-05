package argonaut
package derive

import scalaz.{\/-, -\/}

trait JsonSumCodec {
  def encodeEmpty: Nothing
  def encodeField(fieldOrObj: Either[Json, (String, Json)]): Json

  def decodeEmpty(cursor: HCursor): DecodeResult[Nothing]
  def decodeField[A](name: String, cursor: HCursor, decode: DecodeJson[A]): DecodeResult[Either[ACursor, A]]
}

trait JsonSumCodecFor[S] {
  def codec: JsonSumCodec
}

object JsonSumCodecFor {
  def apply[S](codec0: JsonSumCodec): JsonSumCodecFor[S] =
    new JsonSumCodecFor[S] {
      def codec = codec0
    }
}

object JsonSumCodec {
  val obj = JsonSumObjCodec()
}

case class JsonSumObjCodec(
  toJsonName: Option[String => String] = None
) extends JsonSumCodec {
  private def toJsonName0(name: String) =
    toJsonName.fold(name)(_(name))


  def encodeEmpty: Nothing =
    throw new IllegalArgumentException("empty")
  def encodeField(fieldOrObj: Either[Json, (String, Json)]): Json =
    fieldOrObj match {
      case Left(other) => other
      case Right((name, content)) =>
        Json.obj(toJsonName0(name) -> content)
    }

  def decodeEmpty(cursor: HCursor): DecodeResult[Nothing] =
    DecodeResult.fail("sum", cursor.history)
  def decodeField[A](name: String, cursor: HCursor, decode: DecodeJson[A]): DecodeResult[Either[ACursor, A]] =
    cursor.--\(toJsonName0(name)).either match {
      case -\/(_) =>
        DecodeResult.ok(Left(ACursor.ok(cursor)))
      case \/-(content) =>
        decode.decode(content).map(Right(_))
    }
}
