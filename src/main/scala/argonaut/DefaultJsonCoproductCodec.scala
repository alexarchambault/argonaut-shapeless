package argonaut

trait DefaultJsonCoproductCodec {
  implicit val coproductCodec = new JsonCoproductCodec {
    override def attemptDecode[T](typeName: String, decodeJson: DecodeJson[T], json: Json): Option[DecodeResult[T]] =
      json.field(typeName).map(decodeJson.decodeJson)

    override def encode(typeName: String, json: Json): Json =
      Json.obj(typeName -> json)
  }
}
