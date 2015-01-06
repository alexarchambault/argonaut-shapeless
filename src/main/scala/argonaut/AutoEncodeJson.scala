package argonaut

import Json._
import shapeless._, labelled.FieldType

final class AutoEncodeJson[A](val encodeJson: EncodeJson[A]) extends AnyVal

object AutoEncodeJson {
  def derive[A](implicit autoEncodeJson: AutoEncodeJson[A]): EncodeJson[A] =
    autoEncodeJson.encodeJson

  private final def wrap[A](encodeJson: EncodeJson[A]): AutoEncodeJson[A] =
    new AutoEncodeJson[A](encodeJson)

  implicit def hnilEncodeJson[L <: HNil]: AutoEncodeJson[L] = wrap[L] {
    EncodeJson(_ => jEmptyObject)
  }

  implicit def hconsEncodeJson[K <: Symbol, H, T <: HList](implicit
    key: Witness.Aux[K],
    headEncode: EncodeJson[H],
    tailEncode: AutoEncodeJson[T]
  ): AutoEncodeJson[FieldType[K, H] :: T] = wrap[FieldType[K, H] :: T] {
    EncodeJson { case (h :: t) =>
      (key.value.name -> headEncode.encode(h)) ->: tailEncode.encodeJson.encode(t) 
    }
  }
  
  implicit val cnilEncodeJson: AutoEncodeJson[CNil] = wrap[CNil] {
    EncodeJson(_ => jEmptyObject)
  }

  implicit def cconsEncodeJson[K <: Symbol, H, T <: Coproduct](implicit
    key: Witness.Aux[K],
    headEncode: AutoEncodeJson[H],
    tailEncode: AutoEncodeJson[T]
  ): AutoEncodeJson[FieldType[K, H] :+: T] = wrap[FieldType[K, H] :+: T] {
    EncodeJson {
      case Inl(h) => Json(key.value.name -> headEncode.encodeJson.encode(h))
      case Inr(t) => tailEncode.encodeJson.encode(t)
    }
  }

  implicit def projectEncodeJson[F, G](implicit
    gen: LabelledGeneric.Aux[F, G],
    encode: AutoEncodeJson[G]
  ): AutoEncodeJson[F] = wrap[F] {
    encode.encodeJson.contramap(gen.to)
  }
}
