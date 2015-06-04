package argonaut
package custom

import shapeless._, labelled._

object CustomGenericDecodeJsons {
  implicit def customHConsDecodeJson[K <: Symbol, H, T <: HList](implicit
    key: Witness.Aux[K],
    headDecode: Lazy[DecodeJson[H]],
    tailDecode: Lazy[DecodeJson[T]],
    pc: JsonProductCodec
  ): DecodeJson[FieldType[K, H] :: T] =
    DecodeJson { c =>
      val headCursor = pc.decodeField(key.value.name, c)

      for {
        head <- headDecode.value.tryDecode(headCursor)
        tail <- c.as(tailDecode.value)
      } yield field[K](head) :: tail
    }

  implicit def customCConsAsJsObjectDecodeJson[K <: Symbol, H, T <: Coproduct](implicit
    key: Witness.Aux[K],
    headDecode: Lazy[DecodeJson[H]],
    tailDecode: Lazy[DecodeJson[T]],
    cc: JsonCoproductCodec
  ): DecodeJson[FieldType[K, H] :+: T] =
    DecodeJson { c =>
      val inl: Option[DecodeResult[FieldType[K, H] :+: T]] =
        cc.attemptDecode(key.value.name, headDecode.value, c.focus)
          .map(_.map(field[K](_)).map(Inl(_)))
      inl.getOrElse(c.as(tailDecode.value).map(Inr(_)))
    }
}

trait CustomGenericDecodeJsons {
  implicit val hnilDecodeJson: DecodeJson[HNil] =
    GenericDecodeJsons.hnilDecodeJsonSucceeds

  implicit def hconsDecodeJson[K <: Symbol, H, T <: HList](implicit
    key: Witness.Aux[K],
    headDecode: Lazy[DecodeJson[H]],
    tailDecode: Lazy[DecodeJson[T]],
    pc: JsonProductCodec
  ): DecodeJson[FieldType[K, H] :: T] =
    CustomGenericDecodeJsons.customHConsDecodeJson(key, headDecode, tailDecode, pc)

  implicit val cnilDecodeJson: DecodeJson[CNil] =
    GenericDecodeJsons.cnilDecodeJsonFails

  implicit def cconsDecodeJson[K <: Symbol, H, T <: Coproduct](implicit
    key: Witness.Aux[K],
    headDecode: Lazy[DecodeJson[H]],
    tailDecode: Lazy[DecodeJson[T]],
    cc: JsonCoproductCodec
  ): DecodeJson[FieldType[K, H] :+: T] =
    CustomGenericDecodeJsons.customCConsAsJsObjectDecodeJson(key, headDecode, tailDecode, cc)

  implicit def instanceDecodeJson[F, G](implicit
    gen: LabelledGeneric.Aux[F, G],
    decode: Lazy[DecodeJson[G]]
  ): DecodeJson[F] =
    GenericDecodeJsons.defaultInstanceDecodeJson(gen, decode)
}
