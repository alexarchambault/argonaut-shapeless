package argonaut
package derive

import shapeless._
import shapeless.labelled.{ field, FieldType }

trait MkDecodeJson[T] {
  def decodeJson: DecodeJson[T]
}

object MkDecodeJson {
  def apply[T](implicit decodeJson: MkDecodeJson[T]): MkDecodeJson[T] = decodeJson

  implicit def product[P]
   (implicit
     underlying: ProductDecodeJson[P],
     codecFor: JsonProductCodecFor[P]
   ): MkDecodeJson[P] =
    new MkDecodeJson[P] {
      def decodeJson = underlying(codecFor.codec)
    }

  implicit def sum[S]
   (implicit
     underlying: SumDecodeJson[S],
     codecFor: JsonSumCodecFor[S]
   ): MkDecodeJson[S] =
    new MkDecodeJson[S] {
      def decodeJson = underlying(codecFor.codec)
    }
}

trait ProductDecodeJson[P] {
  def apply(productCodec: JsonProductCodec): DecodeJson[P]
}

object ProductDecodeJson {
  def apply[P](implicit decodeJson: ProductDecodeJson[P]): ProductDecodeJson[P] = decodeJson

  def instance[P](f: JsonProductCodec => DecodeJson[P]): ProductDecodeJson[P] =
    new ProductDecodeJson[P] {
      def apply(productCodec: JsonProductCodec) =
        f(productCodec)
    }

  // Re-enable by making a dummy HList of defaults made of Option[_]
  // implicit def record[R <: HList]
  //  (implicit
  //    underlying: HListProductDecodeJson[R]
  //  ): ProductDecodeJson[R] =
  //   instance { productCodec =>
  //     underlying(productCodec)
  //   }

  implicit def generic[P, L <: HList, D <: HList]
   (implicit
     gen: LabelledGeneric.Aux[P, L],
     defaults: Default.AsOptions.Aux[P, D],
     underlying: Lazy[HListProductDecodeJson[L, D]]
   ): ProductDecodeJson[P] =
    instance { productCodec =>
      underlying.value(productCodec, defaults())
        .map(gen.from)
    }
}

trait HListProductDecodeJson[L <: HList, D <: HList] {
  def apply(productCodec: JsonProductCodec, defaults: D): DecodeJson[L]
}

object HListProductDecodeJson {
  def apply[L <: HList, D <: HList](implicit decodeJson: HListProductDecodeJson[L, D]): HListProductDecodeJson[L, D] =
    decodeJson

  def instance[L <: HList, D <: HList](f: (JsonProductCodec, D) => DecodeJson[L]): HListProductDecodeJson[L, D] =
    new HListProductDecodeJson[L, D] {
      def apply(productCodec: JsonProductCodec, defaults: D) =
        f(productCodec, defaults)
    }

  implicit val hnil: HListProductDecodeJson[HNil, HNil] =
    instance { (productCodec, defaults) =>
      DecodeJson { c =>
        productCodec
          .decodeEmpty(c)
          .map(_ => HNil)
      }
    }

  implicit def hcons[K <: Symbol, H, T <: HList, TD <: HList]
   (implicit
     key: Witness.Aux[K],
     headDecode: Strict[DecodeJson[H]],
     tailDecode: HListProductDecodeJson[T, TD]
   ): HListProductDecodeJson[FieldType[K, H] :: T, Option[H] :: TD] =
    instance { (productCodec, defaults) =>
      lazy val tailDecode0 = tailDecode(productCodec, defaults.tail)

      DecodeJson { c =>
        for {
          x <- productCodec.decodeField(key.value.name, c, headDecode.value, defaults.head)
          (h, remaining) = x
          t <- remaining.as(tailDecode0)
        } yield field[K](h) :: t
      }
    }
}

trait CoproductSumDecodeJson[C <: Coproduct] {
  def apply(sumCodec: JsonSumCodec): DecodeJson[C]
}

object CoproductSumDecodeJson {
  def apply[C <: Coproduct](implicit decodeJson: CoproductSumDecodeJson[C]): CoproductSumDecodeJson[C] =
    decodeJson

  def instance[C <: Coproduct](f: JsonSumCodec => DecodeJson[C]): CoproductSumDecodeJson[C] =
    new CoproductSumDecodeJson[C] {
      def apply(sumCodec: JsonSumCodec) =
        f(sumCodec)
    }

  implicit val cnil: CoproductSumDecodeJson[CNil] =
    instance { sumCodec =>
      DecodeJson { c =>
        sumCodec
          .decodeEmpty(c)
          .map(t => t: CNil)
      }
    }

  implicit def ccons[K <: Symbol, H, T <: Coproduct]
   (implicit
     key: Witness.Aux[K],
     headDecode: Lazy[DecodeJson[H]],
     tailDecode: CoproductSumDecodeJson[T]
   ): CoproductSumDecodeJson[FieldType[K, H] :+: T] =
    instance { sumCodec =>
      lazy val tailDecode0 = tailDecode(sumCodec)

      DecodeJson { c =>
        sumCodec.decodeField(key.value.name, c, headDecode.value).flatMap {
          case Left(tailCursor) => tailCursor.as(tailDecode0).map(Inr(_))
          case Right(h) => DecodeResult.ok(Inl(field[K](h)))
        }
      }
    }
}

trait SumDecodeJson[S] {
  def apply(sumCodec: JsonSumCodec): DecodeJson[S]
}

object SumDecodeJson {
  def apply[S](implicit decodeJson: SumDecodeJson[S]): SumDecodeJson[S] = decodeJson

  def instance[S](f: JsonSumCodec => DecodeJson[S]): SumDecodeJson[S] =
    new SumDecodeJson[S] {
      def apply(sumCodec: JsonSumCodec) =
        f(sumCodec)
    }

  implicit def union[U <: Coproduct]
   (implicit
     underlying: CoproductSumDecodeJson[U]
   ): SumDecodeJson[U] =
    instance { sumCodec =>
      underlying(sumCodec)
    }

  implicit def generic[S, C <: Coproduct]
   (implicit
     gen: LabelledGeneric.Aux[S, C],
     underlying: Strict[CoproductSumDecodeJson[C]]
   ): SumDecodeJson[S] =
    instance { sumCodec =>
      underlying.value(sumCodec)
        .map(gen.from)
    }
}
