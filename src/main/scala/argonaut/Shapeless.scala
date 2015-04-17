package argonaut

object Shapeless extends AutoDecodeJsons with AutoEncodeJsons with DefaultJsonCoproductCodec {
  object Custom extends CustomGenericDecodeJsons with CustomGenericEncodeJsons
}