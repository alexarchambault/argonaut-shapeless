package argonaut

trait AutoDecodeJsons {
  implicit def autoDecodeJson[A: AutoDecodeJson]: DecodeJson[A] =
    AutoDecodeJson.derive
}

trait AutoEncodeJsons {  
  implicit def autoEncodeJson[A: AutoEncodeJson]: EncodeJson[A] =
    AutoEncodeJson.derive
}

trait AutoCodecJsons extends AutoEncodeJsons with AutoDecodeJsons {
  implicit def autoCodecJson[A: AutoEncodeJson: AutoDecodeJson]: CodecJson[A] =
    AutoCodecJson.derive
}

object Shapeless extends AutoCodecJsons
