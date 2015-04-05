package argonaut

import Json._
import shapeless._, labelled.FieldType

object GenericEncodeJsons {
  implicit def hnilJsObjectEncodeJson[L <: HNil]: EncodeJson[L] =
    EncodeJson(_ => jEmptyObject)

  implicit def hconsJsObjectEncodeJson[K <: Symbol, H, T <: HList](implicit
    key: Witness.Aux[K],
    headEncode: Lazy[EncodeJson[H]],
    tailEncode: Lazy[EncodeJson[T]]
  ): EncodeJson[FieldType[K, H] :: T] =
    EncodeJson { case (h :: t) =>
      (key.value.name -> headEncode.value.encode(h)) ->: tailEncode.value.encode(t)
    }

  implicit def cnilEncodeJsonFails: EncodeJson[CNil] =
    EncodeJson(_ => sys.error("JSON representation of CNil"))

  implicit def cconsJsObjectEncodeJson[K <: Symbol, H, T <: Coproduct](implicit
    key: Witness.Aux[K],
    headEncode: Lazy[EncodeJson[H]],
    tailEncode: Lazy[EncodeJson[T]]
  ): EncodeJson[FieldType[K, H] :+: T] =
    EncodeJson {
      case Inl(h) => Json.obj(key.value.name -> headEncode.value.encode(h))
      case Inr(t) => tailEncode.value.encode(t)
    }

  implicit def defaultInstanceEncodeJson[F, G](implicit
    gen: LabelledGeneric.Aux[F, G],
    encode: Lazy[EncodeJson[G]]
  ): EncodeJson[F] =
    encode.value.contramap(gen.to)
}

trait DefaultGenericEncodeJsons {
  implicit def hnilEncodeJson[L <: HNil]: EncodeJson[L] =
    GenericEncodeJsons.hnilJsObjectEncodeJson

  implicit def hconsEncodeJson[K <: Symbol, H, T <: HList](implicit
    key: Witness.Aux[K],
    headEncode: Lazy[EncodeJson[H]],
    tailEncode: Lazy[EncodeJson[T]]
  ): EncodeJson[FieldType[K, H] :: T] =
    GenericEncodeJsons.hconsJsObjectEncodeJson(key, headEncode, tailEncode)
  
  implicit val cnilEncodeJson: EncodeJson[CNil] =
    GenericEncodeJsons.cnilEncodeJsonFails

  implicit def cconsEncodeJson[K <: Symbol, H, T <: Coproduct](implicit
    key: Witness.Aux[K],
    headEncode: Lazy[EncodeJson[H]],
    tailEncode: Lazy[EncodeJson[T]]
  ): EncodeJson[FieldType[K, H] :+: T] =
    GenericEncodeJsons.cconsJsObjectEncodeJson(key, headEncode, tailEncode)

  implicit def instanceEncodeJson[F, G](implicit
    gen: LabelledGeneric.Aux[F, G],
    encode: Lazy[EncodeJson[G]]
  ): EncodeJson[F] =
    GenericEncodeJsons.defaultInstanceEncodeJson(gen, encode)
}
