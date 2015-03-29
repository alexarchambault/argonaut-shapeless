package argonaut

trait CoproductContainer {
  def apply(typeName: String, content: Json): Json
  def unapply(json: HCursor): Option[(String, Json)]
}

object CoproductContainer {
  def default = new CoproductContainer {
    override def apply(typeName: String, content: Json): Json = Json(typeName -> content)
    override def unapply(c: HCursor): Option[(String, Json)] = {
      c.focus.objectFields.flatMap(_.headOption).flatMap { field =>
        c.focus.field(field).map { value =>
          (field, value)
        }
      }
    }
  }
}
