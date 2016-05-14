package argonaut
package derive

trait JsonProductCodec {
  def encodeEmpty: Json
  def encodeField(field: (String, Json), obj: Json, default: => Option[Json]): Json
  
  def decodeEmpty(cursor: HCursor): DecodeResult[Unit]
  def decodeField[A](name: String, cursor: HCursor, decode: DecodeJson[A], default: Option[A]): DecodeResult[(A, ACursor)]
}

object JsonProductCodec {
  val obj: JsonProductCodec = new JsonProductObjCodec
}

trait JsonProductCodecFor[P] {
  def codec: JsonProductCodec
}

object JsonProductCodecFor {
  def apply[S](codec0: JsonProductCodec): JsonProductCodecFor[S] =
    new JsonProductCodecFor[S] {
      def codec = codec0
    }

  implicit def default[T]: JsonProductCodecFor[T] =
    JsonProductCodecFor(JsonProductCodec.obj)
}

class JsonProductObjCodec extends JsonProductCodec {

  def toJsonName(name: String): String = name

  val encodeEmpty: Json = Json.obj()
  def encodeField(field: (String, Json), obj: Json, default: => Option[Json]): Json = {
    val (name, content) = field
    if (default.toSeq.contains(content))
      obj
    else
      (toJsonName(name) -> content) ->: obj
  }

  def decodeEmpty(cursor: HCursor): DecodeResult[Unit] = DecodeResult.ok(())
  def decodeField[A](name: String, cursor: HCursor, decode: DecodeJson[A], default: Option[A]): DecodeResult[(A, ACursor)] = {
    val c = cursor.--\(toJsonName(name))
    def result = c.as(decode).map((_, ACursor.ok(cursor)))

    default match {
      case None => result
      case Some(d) =>
        if (c.succeeded)
          result
        else
          DecodeResult.ok((d, ACursor.ok(cursor)))
    }
  }
}
