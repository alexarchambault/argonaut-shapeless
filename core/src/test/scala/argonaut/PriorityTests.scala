package argonaut

import utest._

import Argonaut._, Shapeless._

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

object PriorityTests extends TestSuite {
  import PriorityTestsDefn._

  val tests = TestSuite {
    'dontOverride - {
      DecodeJson.of[CC] match {
        case _: Flag =>
        case _ => throw new Exception(s"Default DecodeJson was overridden")
      }
    }

    'doOverride - {
      DecodeJson.of[CC2] match {
        case _: Flag => throw new Exception(s"Can't happen")
        case _ =>
      }
    }
  }

}
