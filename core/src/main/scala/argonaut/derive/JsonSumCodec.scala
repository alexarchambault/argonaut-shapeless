package argonaut
package derive

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

  implicit def default[T]: JsonSumCodecFor[T] =
    JsonSumCodecFor(JsonSumCodec.obj)
}

object JsonSumCodec {
  val obj: JsonSumCodec = new JsonSumObjCodec
  val typeField: JsonSumCodec = new JsonSumTypeFieldCodec
}

class JsonSumObjCodec extends JsonSumCodec {

  def toJsonName(name: String): String = name

  def encodeEmpty: Nothing =
    throw new IllegalArgumentException("empty")
  def encodeField(fieldOrObj: Either[Json, (String, Json)]): Json =
    fieldOrObj match {
      case Left(other) => other
      case Right((name, content)) =>
        Json.obj(toJsonName(name) -> content)
    }

  def decodeEmpty(cursor: HCursor): DecodeResult[Nothing] =
    DecodeResult.fail(s"unrecognized type(s): ${cursor.fields.getOrElse(Nil).mkString(", ")}", cursor.history)
  def decodeField[A](name: String, cursor: HCursor, decode: DecodeJson[A]): DecodeResult[Either[ACursor, A]] =
    cursor.--\(toJsonName(name)).either match {
      case Left(_) =>
        DecodeResult.ok(Left(ACursor.ok(cursor)))
      case Right(content) =>
        decode.decode(content).map(Right(_))
    }
}

class JsonSumTypeFieldCodec extends JsonSumCodec {

  def typeField: String = "type"

  def toTypeValue(name: String) = name

  def encodeEmpty: Nothing =
    throw new IllegalArgumentException("empty")
  def encodeField(fieldOrObj: Either[Json, (String, Json)]): Json =
    fieldOrObj match {
      case Left(other) => other
      case Right((name, content)) =>
        (typeField -> Json.jString(toTypeValue(name))) ->: content
    }

  def decodeEmpty(cursor: HCursor): DecodeResult[Nothing] =
    DecodeResult.fail(
      cursor.--\(typeField).focus match {
        case None => "no type found"
        case Some(type0) => s"unrecognized type: $type0"
      },
      cursor.history
    )
  def decodeField[A](name: String, cursor: HCursor, decode: DecodeJson[A]): DecodeResult[Either[ACursor, A]] =
    cursor.--\(typeField).as[String].result match {
      case Right(name0) if toTypeValue(name) == name0 =>
        decode.decode(cursor).map(Right(_))
      case _ =>
        DecodeResult.ok(Left(ACursor.ok(cursor)))
    }
}
