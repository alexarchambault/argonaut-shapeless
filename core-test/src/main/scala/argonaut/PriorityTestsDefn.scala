package argonaut

object PriorityTestsDefn {
  trait Flag

  case class CC(s: String, i: Int)

  object CC {
    implicit val decode: DecodeJson[CC] = new DecodeJson[CC] with Flag {
      def decode(c: HCursor) =
        DecodeJson.of[(String, Int)].decode(c).map { case (s, i) =>
          CC(s, i)
        }
    }
  }

  case class CC2(i: Int, s: String)
}
