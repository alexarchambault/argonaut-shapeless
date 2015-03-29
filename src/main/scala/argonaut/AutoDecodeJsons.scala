package argonaut

import scalaz.Scalaz.{^ => apply2, _}
import shapeless._, labelled.{ FieldType, field }

trait AutoDecodeJsons1 {
  def cconsDecodeJson[K <: Symbol, H, T <: Coproduct](implicit
                                                      key: Witness.Aux[K],
                                                      headDecode: Lazy[DecodeJson[H]],
                                                      tailDecode: Lazy[DecodeJson[T]],
                                                      coproductContainer: CoproductContainer
                                                       ): DecodeJson[FieldType[K, H] :+: T]

  implicit def defaultCConsDecodeJson[K <: Symbol, H, T <: Coproduct](implicit
    key: Witness.Aux[K],
    headDecode: Lazy[DecodeJson[H]],
    tailDecode: Lazy[DecodeJson[T]]
  ): DecodeJson[FieldType[K, H] :+: T] =
    cconsDecodeJson(key, headDecode, tailDecode, CoproductContainer.default)
}

trait AutoDecodeJsons extends AutoDecodeJsons1 {
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
    coproductContainer: CoproductContainer
  ): DecodeJson[FieldType[K, H] :+: T] =
    DecodeJson {
      case coproductContainer(key.value.name, head) => head.as(headDecode.value).map(h => Inl(field(h)))
      case other => other.as(tailDecode.value).map(Inr(_))
    }

  implicit def projectDecodeJson[F, G](implicit
    gen: LabelledGeneric.Aux[F, G],
    decode: Lazy[DecodeJson[G]]
  ): DecodeJson[F] =
    decode.value.map(gen.from)
}
