package argonaut

import Json._
import shapeless._, labelled.FieldType

trait AutoEncodeJsons1 {

  def cconsEncodeJson[K <: Symbol, H, T <: Coproduct](implicit
                                                      key: Witness.Aux[K],
                                                      headEncode: Lazy[EncodeJson[H]],
                                                      tailEncode: Lazy[EncodeJson[T]],
                                                      coproductContainer: CoproductContainer
                                                       ): EncodeJson[FieldType[K, H] :+: T]

  implicit def defaultCConsEncodeJson[K <: Symbol, H, T <: Coproduct](implicit
                                                                      key: Witness.Aux[K],
                                                                      headEncode: Lazy[EncodeJson[H]],
                                                                      tailEncode: Lazy[EncodeJson[T]]
                                                                       ): EncodeJson[FieldType[K, H] :+: T] =
    cconsEncodeJson(key, headEncode, tailEncode, CoproductContainer.default)
}

trait AutoEncodeJsons extends AutoEncodeJsons1 {
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
    tailEncode: Lazy[EncodeJson[T]],
    coproductContainer: CoproductContainer
  ): EncodeJson[FieldType[K, H] :+: T] =
    EncodeJson {
      case Inl(h) => coproductContainer(key.value.name, headEncode.value.encode(h))
      case Inr(t) => tailEncode.value.encode(t)
    }

  implicit def projectEncodeJson[F, G](implicit
    gen: LabelledGeneric.Aux[F, G],
    encode: Lazy[EncodeJson[G]]
  ): EncodeJson[F] =
    encode.value.contramap(gen.to)
}
