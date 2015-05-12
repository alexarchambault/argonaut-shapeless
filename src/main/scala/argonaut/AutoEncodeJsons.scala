package argonaut

import Json._
import shapeless._, labelled.FieldType

trait AutoEncodeJsons {

  object EncodeJsonDeriver {

  implicit def hnilEncodeJson[L <: HNil]: EncodeJson[L] =
    EncodeJson(_ => jEmptyObject)

  implicit def hconsEncodeJson[K <: Symbol, H, T <: HList](implicit
    key: Witness.Aux[K],
    headEncode: Lazy[EncodeJson[H]],
    tailEncode: Lazy[EncodeJson[T]]
  ): EncodeJson[FieldType[K, H] :: T] =
    EncodeJson { case (h :: t) =>
      (key.value.name -> headEncode.value.encode(h)) ->: tailEncode.value.encode(t)
    }

  implicit val cnilEncodeJson: EncodeJson[CNil] =
    EncodeJson(_ => jEmptyObject)

  implicit def cconsEncodeJson[K <: Symbol, H, T <: Coproduct](implicit
    key: Witness.Aux[K],
    headEncode: Lazy[EncodeJson[H]],
    tailEncode: Lazy[EncodeJson[T]]
  ): EncodeJson[FieldType[K, H] :+: T] =
    EncodeJson {
      case Inl(h) => Json(key.value.name -> headEncode.value.encode(h))
      case Inr(t) => tailEncode.value.encode(t)
    }

  implicit def projectEncodeJson[F, G](implicit
    gen: LabelledGeneric.Aux[F, G],
    encode: Lazy[EncodeJson[G]]
  ): EncodeJson[F] =
    encode.value.contramap(gen.to)

  } // EncodeJsonDeriver


  implicit def encodeJsonDeriver[T](implicit orphan: Orphan[EncodeJson, EncodeJsonDeriver.type, T]): EncodeJson[T] =
    orphan.instance

}
