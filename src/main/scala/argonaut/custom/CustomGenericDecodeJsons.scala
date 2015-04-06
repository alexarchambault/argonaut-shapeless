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
    tailDecode: Lazy[DecodeJson[T]]
  ): DecodeJson[FieldType[K, H] :+: T] =
    GenericDecodeJsons.cconsAsJsObjectDecodeJson(key, headDecode, tailDecode)

  implicit def instanceDecodeJson[F, G](implicit
    gen: LabelledGeneric.Aux[F, G],
    decode: Lazy[DecodeJson[G]]
  ): DecodeJson[F] =
    GenericDecodeJsons.defaultInstanceDecodeJson(gen, decode)
}
