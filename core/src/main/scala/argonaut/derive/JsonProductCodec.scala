package argonaut
package derive

trait JsonProductCodec {
  def encodeEmpty: Json
  def encodeField(field: (String, Json), obj: Json, default: => Option[Json]): Json
  
  def decodeEmpty(cursor: HCursor): DecodeResult[Unit]
  def decodeField[A](name: String, cursor: HCursor, decode: DecodeJson[A], default: Option[A]): DecodeResult[(A, ACursor)]
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
  def encodeField(field: (String, Json), obj: Json, default: => Option[Json]): Json = {
    val (name, content) = field
    if (default.toSeq.contains(content))
      obj
    else
      (toJsonName0(name) -> content) ->: obj
  }

  def decodeEmpty(cursor: HCursor): DecodeResult[Unit] = DecodeResult.ok(())
  def decodeField[A](name: String, cursor: HCursor, decode: DecodeJson[A], default: Option[A]): DecodeResult[(A, ACursor)] = {
    val c = cursor.--\(toJsonName0(name))
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
