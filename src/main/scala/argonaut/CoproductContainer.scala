package argonaut

trait CoproductContainer {
  def apply(typeName: String, content: Json): Json
  def unapply(json: HCursor): Option[(String, Json)]
}
