package argonaut

object Shapeless extends DefaultGenericDecodeJsons with DefaultGenericEncodeJsons {

  object Custom extends custom.CustomGenericDecodeJsons with custom.CustomGenericEncodeJsons

}