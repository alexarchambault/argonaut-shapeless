package argonaut
package derive

import shapeless.{ Lazy => _, _ }
import shapeless.labelled.FieldType

import shapeless.compat.{ Strict, Lazy, Default }

trait MkEncodeJson[T] {
  def encodeJson: EncodeJson[T]
}

object MkEncodeJson {
  def apply[T](implicit encodeJson: MkEncodeJson[T]): MkEncodeJson[T] = encodeJson

  implicit def product[P]
   (implicit
     underlying: Strict[ProductEncodeJson[P]],
     codecFor: JsonProductCodecFor[P]
   ): MkEncodeJson[P] =
    new MkEncodeJson[P] {
      def encodeJson = underlying.value(codecFor.codec)
    }

  implicit def sum[S]
   (implicit
     underlying: Strict[SumEncodeJson[S]],
     codecFor: JsonSumCodecFor[S]
   ): MkEncodeJson[S] =
    new MkEncodeJson[S] {
      def encodeJson = underlying.value(codecFor.codec)
    }
}

trait ProductEncodeJson[P] {
  def apply(productCodec: JsonProductCodec): EncodeJson[P]
}

object ProductEncodeJson {
  def apply[P](implicit encodeJson: ProductEncodeJson[P]): ProductEncodeJson[P] = encodeJson

  def instance[P](f: JsonProductCodec => EncodeJson[P]): ProductEncodeJson[P] =
    new ProductEncodeJson[P] {
      def apply(productCodec: JsonProductCodec) =
        f(productCodec)
    }

  // TODO Generate an HList made of Option[...] as to use as default
  // implicit def record[R <: HList]
  //  (implicit
  //    underlying: HListProductEncodeJson[R]
  //  ): ProductEncodeJson[R] =
  //   instance { productCodec =>
  //     underlying(productCodec)
  //   }

  implicit def generic[P, L <: HList, D <: HList]
   (implicit
     gen: LabelledGeneric.Aux[P, L],
     defaults: Default.AsOptions.Aux[P, D],
     underlying: Lazy[HListProductEncodeJson[L, D]]
   ): ProductEncodeJson[P] =
    instance { productCodec =>
      underlying.value(productCodec, defaults())
        .contramap(gen.to)
    }
}

trait HListProductEncodeJson[L <: HList, D <: HList] {
  def apply(productCodec: JsonProductCodec, defaults: D): EncodeJson[L]
}

object HListProductEncodeJson {
  def apply[L <: HList, D <: HList](implicit encodeJson: HListProductEncodeJson[L, D]): HListProductEncodeJson[L, D] =
    encodeJson

  def instance[L <: HList, D <: HList](f: (JsonProductCodec, D) => EncodeJson[L]): HListProductEncodeJson[L, D] =
    new HListProductEncodeJson[L, D] {
      def apply(productCodec: JsonProductCodec, defaults: D) =
        f(productCodec, defaults)
    }

  implicit val hnil: HListProductEncodeJson[HNil, HNil] =
    instance { (productCodec, _) =>
      EncodeJson { _ =>
        productCodec.encodeEmpty
      }
    }

  implicit def hcons[K <: Symbol, H, T <: HList, TD <: HList]
   (implicit
     key: Witness.Aux[K],
     headEncode: Strict[EncodeJson[H]],
     tailEncode: HListProductEncodeJson[T, TD]
   ): HListProductEncodeJson[FieldType[K, H] :: T, Option[H] :: TD] =
    instance { (productCodec, defaults) =>
      lazy val defaultOpt = defaults.head.map(headEncode.value.encode)
      lazy val tailEncode0 = tailEncode(productCodec, defaults.tail)

      EncodeJson { l =>
        productCodec.encodeField(
          key.value.name -> headEncode.value.encode(l.head),
          tailEncode0.encode(l.tail),
          defaultOpt
        )
      }
    }
}


trait SumEncodeJson[S] {
  def apply(sumCodec: JsonSumCodec): EncodeJson[S]
}

object SumEncodeJson {
  def apply[S](implicit encodeJson: SumEncodeJson[S]): SumEncodeJson[S] = encodeJson

  def instance[S](f: JsonSumCodec => EncodeJson[S]): SumEncodeJson[S] =
    new SumEncodeJson[S] {
      def apply(sumCodec: JsonSumCodec) =
        f(sumCodec)
    }

  implicit def union[U <: Coproduct]
   (implicit
     underlying: CoproductSumEncodeJson[U]
   ): SumEncodeJson[U] =
    instance { sumCodec =>
      underlying(sumCodec)
    }

  implicit def generic[S, C <: Coproduct]
   (implicit
     gen: LabelledGeneric.Aux[S, C],
     underlying: Strict[CoproductSumEncodeJson[C]]
   ): SumEncodeJson[S] =
    instance { sumCodec =>
      underlying.value(sumCodec)
        .contramap(gen.to)
    }
}

trait CoproductSumEncodeJson[C <: Coproduct] {
  def apply(sumCodec: JsonSumCodec): EncodeJson[C]
}

object CoproductSumEncodeJson {
  def apply[C <: Coproduct](implicit encodeJson: CoproductSumEncodeJson[C]): CoproductSumEncodeJson[C] =
    encodeJson

  def instance[C <: Coproduct](f: JsonSumCodec => EncodeJson[C]): CoproductSumEncodeJson[C] =
    new CoproductSumEncodeJson[C] {
      def apply(sumCodec: JsonSumCodec) =
        f(sumCodec)
    }

  implicit val cnil: CoproductSumEncodeJson[CNil] =
    instance { sumCodec =>
      EncodeJson { c =>
        sumCodec.encodeEmpty
      }
    }

  implicit def ccons[K <: Symbol, H, T <: Coproduct]
   (implicit
     key: Witness.Aux[K],
     headEncode: Lazy[EncodeJson[H]],
     tailEncode: CoproductSumEncodeJson[T]
   ): CoproductSumEncodeJson[FieldType[K, H] :+: T] =
    instance { sumCodec =>
      lazy val tailEncode0 = tailEncode(sumCodec)

      EncodeJson {
        case Inl(h) =>
          sumCodec.encodeField(
            Right(key.value.name -> headEncode.value.encode(h))
          )
        case Inr(r) =>
          tailEncode0(r)
      }
    }
}