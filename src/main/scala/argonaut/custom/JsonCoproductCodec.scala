package argonaut.custom

import argonaut.{DecodeJson, Json, DecodeResult}

trait JsonCoproductCodec {
  def encode(typeName: String, json: Json): Json
  def attemptDecode[T](typeName: String, decodeJson: DecodeJson[T], json: Json): Option[DecodeResult[T]]
}

object JsonCoproductCodec {
  implicit val defaultJsonCoproductCodec =
    new JsonCoproductCodec {
      def attemptDecode[T](typeName: String, decodeJson: DecodeJson[T], json: Json): Option[DecodeResult[T]] =
        json.field(typeName).map(decodeJson.decodeJson)
      def encode(typeName: String, json: Json): Json =
        Json.obj(typeName -> json)
  }
}