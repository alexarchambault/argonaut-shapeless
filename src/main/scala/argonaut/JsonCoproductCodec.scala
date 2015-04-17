package argonaut

trait JsonCoproductCodec {
  def encode(typeName: String, json: Json): Json
  def attemptDecode[T](typeName: String, decodeJson: DecodeJson[T], json: Json): Option[DecodeResult[T]]
}
