package argonaut.macros

import utest._

import argonaut._
import argonaut.Argonaut._

object instances {
  import argonaut.ArgonautShapeless._
  import argonaut.derive.JsonCodec
  @JsonCodec case class BasicCaseClass(i: Int, s: String)
  @JsonCodec case class GenericCaseClass[T](i: Int, s: String, t: T)
  @JsonCodec sealed trait A
  case class B(b: String) extends A
  case class C(c: Int) extends A
  object A
}

object JsonCodecMacroTests extends TestSuite {
  // ArgonautShapeless._ not imported here
  val tests = TestSuite {
    'basic - {
      import instances.BasicCaseClass
      val encoder = EncodeJson.of[BasicCaseClass]
      val decoder = DecodeJson.of[BasicCaseClass]
    }
    'generic - {
      import instances.GenericCaseClass
      def encoder[T: EncodeJson] = EncodeJson.of[GenericCaseClass[T]]
      def decoder[T: DecodeJson] = DecodeJson.of[GenericCaseClass[T]]
    }
    'ADT - {
      import instances.A
      val encoder = EncodeJson.of[A]
      val decoder = DecodeJson.of[A]
    }
  }
}
