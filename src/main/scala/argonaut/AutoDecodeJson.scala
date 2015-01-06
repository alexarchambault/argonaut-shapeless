package argonaut

import scalaz.Scalaz._
import shapeless._, labelled.{ FieldType, field }

final class AutoDecodeJson[A](val decodeJson: DecodeJson[A]) extends AnyVal

object AutoDecodeJson {
  def derive[A](implicit autoDecodeJson: AutoDecodeJson[A]): DecodeJson[A] =
    autoDecodeJson.decodeJson

  private final def wrap[A](decodeJson: DecodeJson[A]): AutoDecodeJson[A] =
    new AutoDecodeJson[A](decodeJson)

  implicit val hnilDecodeJson: AutoDecodeJson[HNil] = wrap {
    DecodeJson { c =>
      // if (c.focus.obj.exists(_.isEmpty))
      (HNil: HNil).point[DecodeResult]
      // else
      //   DecodeResult.fail("HNil", c.history)
    }
  }

  implicit def hconsDecodeJson[K <: Symbol, H, T <: HList](implicit
    key: Witness.Aux[K],
    headDecode: DecodeJson[H],
    tailDecode: AutoDecodeJson[T]
  ): AutoDecodeJson[FieldType[K, H] :: T] = wrap {
    DecodeJson { c =>
      val headJson = c --\ key.value.name
      (headJson.as(headDecode).map(field[K](_)) |@| headJson.delete.as(tailDecode.decodeJson))(_ :: _)
    }
  }

  implicit val cnilDecodeJson: AutoDecodeJson[CNil] = wrap {
    DecodeJson(c => DecodeResult.fail("CNil", c.history))
  }

  implicit def cconsDecodeJson[K <: Symbol, H, T <: Coproduct](implicit
    key: Witness.Aux[K],
    headDecode: AutoDecodeJson[H],
    tailDecode: AutoDecodeJson[T]
  ): AutoDecodeJson[FieldType[K, H] :+: T] = wrap {
    DecodeJson { c =>
      if (c.focus.string.exists(_ == key.value.name))
        Json.obj().as(headDecode.decodeJson).map(h => Inl(field(h)))
      else
        (c --\ key.value.name).focus match {
          case Some(headJson) => headJson.as(headDecode.decodeJson).map(h => Inl(field(h)))
          case None           => tailDecode.decodeJson.decode(c).map(Inr(_))
        }
    }
  }

  implicit def projectDecodeJson[F, G](implicit
    gen: LabelledGeneric.Aux[F, G],
    decode: AutoDecodeJson[G]
  ): AutoDecodeJson[F] = wrap {
    decode.decodeJson.map(gen.from)
  }
}
