package argonaut
package custom

trait JsonProductCodec {
  def encodeField(fieldName: String, content: Json, to: Json): Json
  def decodeField(fieldName: String, from: HCursor): ACursor
}

object JsonProductCodec {
  implicit val defaultJsonProductCodec: JsonProductCodec =
    new JsonProductCodec {
      def encodeField(fieldName: String, content: Json, to: Json) =
        (fieldName -> content) ->: to
      def decodeField(fieldName: String, from: HCursor) =
        from --\ fieldName
    }
}
