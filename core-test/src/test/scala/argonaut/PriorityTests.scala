package argonaut

import utest._

import argonaut.Argonaut._
import argonaut.ArgonautShapeless._

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
