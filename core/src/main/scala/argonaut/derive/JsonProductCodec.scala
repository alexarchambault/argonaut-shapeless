package argonaut
package derive

trait JsonProductCodec {
  def encodeEmpty: Json
  def encodeField(field: (String, Json), obj: Json): Json
  
  def decodeEmpty(cursor: HCursor): DecodeResult[Unit]
  def decodeField[A](name: String, cursor: HCursor, decode: DecodeJson[A]): DecodeResult[(A, ACursor)]
}

trait JsonProductCodecFor[P] {
  def codec: JsonProductCodec
}

object JsonProductCodecFor {
  def apply[S](codec0: JsonProductCodec): JsonProductCodecFor[S] =
    new JsonProductCodecFor[S] {
      def codec = codec0
    }
}

object JsonProductCodec {
  val obj = JsonProductObjCodec()
}

case class JsonProductObjCodec(
  toJsonName: Option[String => String] = None
) extends JsonProductCodec {
  def toJsonName0(name: String) = toJsonName.fold(name)(_(name))

  val encodeEmpty: Json = Json.obj()
  def encodeField(field: (String, Json), obj: Json): Json = {
    val (name, content) = field
    (toJsonName0(name) -> content) ->: obj
  }

  def decodeEmpty(cursor: HCursor): DecodeResult[Unit] = DecodeResult.ok(())
  def decodeField[A](name: String, cursor: HCursor, decode: DecodeJson[A]): DecodeResult[(A, ACursor)] =
    cursor
      .--\(toJsonName0(name))
      .as(decode)
      .map((_, ACursor.ok(cursor)))
}
