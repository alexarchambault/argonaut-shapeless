package argonaut

import scalaz.Scalaz._
import shapeless._, labelled.{ FieldType, field }

trait AutoDecodeJsons {
  implicit val hnilDecodeJson: DecodeJson[HNil] =
    DecodeJson { c =>
      // if (c.focus.obj.exists(_.isEmpty))
      (HNil: HNil).point[DecodeResult]
      // else
      //   DecodeResult.fail("HNil", c.history)
    }

  implicit def hconsDecodeJson[K <: Symbol, H, T <: HList](implicit
    key: Witness.Aux[K],
    headDecode: Lazy[DecodeJson[H]],
    tailDecode: Lazy[DecodeJson[T]]
  ): DecodeJson[FieldType[K, H] :: T] =
    DecodeJson { c =>
      val headJson = c --\ key.value.name
      (headJson.as(headDecode.value).map(field[K](_)) |@| headJson.delete.as(tailDecode.value))(_ :: _)
    }

  implicit val cnilDecodeJson: DecodeJson[CNil] = 
    DecodeJson(c => DecodeResult.fail("CNil", c.history))

  implicit def cconsDecodeJson[K <: Symbol, H, T <: Coproduct](implicit
    key: Witness.Aux[K],
    headDecode: Lazy[DecodeJson[H]],
    tailDecode: Lazy[DecodeJson[T]]
  ): DecodeJson[FieldType[K, H] :+: T] =
    DecodeJson { c =>
      if (c.focus.string.exists(_ == key.value.name))
        Json.obj().as(headDecode.value).map(h => Inl(field(h)))
      else
        (c --\ key.value.name).focus match {
          case Some(headJson) => headJson.as(headDecode.value).map(h => Inl(field(h)))
          case None           => tailDecode.value.decode(c).map(Inr(_))
        }
    }

  implicit def projectDecodeJson[F, G](implicit
    gen: LabelledGeneric.Aux[F, G],
    decode: Lazy[DecodeJson[G]]
  ): DecodeJson[F] =
    decode.value.map(gen.from)
}
