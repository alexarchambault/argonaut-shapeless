package argonaut

object AutoCodecJson {
  def derive[A](implicit
    autoEncodeJson: AutoEncodeJson[A],
    autoDecodeJson: AutoDecodeJson[A]
  ): CodecJson[A] =
    CodecJson.derived(
      autoEncodeJson.encodeJson,
      autoDecodeJson.decodeJson
    )
}

