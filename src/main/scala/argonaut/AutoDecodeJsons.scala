package argonaut

import scalaz.Scalaz.{^ => apply2, _}
import shapeless._, labelled.{ FieldType, field }

trait AutoDecodeJsons {
  implicit val hnilDecodeJson: DecodeJson[HNil] =
    DecodeJson { c =>
      (HNil: HNil).point[DecodeResult]
    }

  implicit def hconsDecodeJson[K <: Symbol, H, T <: HList](implicit
    key: Witness.Aux[K],
    headDecode: Lazy[DecodeJson[H]],
    tailDecode: Lazy[DecodeJson[T]]
  ): DecodeJson[FieldType[K, H] :: T] =
    DecodeJson { c =>
      val headJson = c --\ key.value.name
      val head = headJson.as(headDecode.value).map(field[K](_))
      lazy val tail = headJson.delete.as(tailDecode.value)
      apply2(head, tail)(_ :: _)
    }

  implicit val cnilDecodeJson: DecodeJson[CNil] = 
    DecodeJson(c => DecodeResult.fail("CNil", c.history))


  implicit def cconsDecodeJson[K <: Symbol, H, T <: Coproduct](implicit
    key: Witness.Aux[K],
    headDecode: Lazy[DecodeJson[H]],
    tailDecode: Lazy[DecodeJson[T]],
    coproductCodec: JsonCoproductCodec
  ): DecodeJson[FieldType[K, H] :+: T] =
    DecodeJson[FieldType[K, H] :+: T] { c =>
      val inl: Option[DecodeResult[FieldType[K, H] :+: T]] =
        coproductCodec.attemptDecode(key.value.name, headDecode.value, c.focus)
          .map(_.map(field[K](_)).map(Inl(_)))
      inl.getOrElse(c.as(tailDecode.value).map(Inr(_)))
    }

  implicit def projectDecodeJson[F, G](implicit
    gen: LabelledGeneric.Aux[F, G],
    decode: Lazy[DecodeJson[G]]
  ): DecodeJson[F] =
    decode.value.map(gen.from)
}
