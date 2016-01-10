package argonaut
package derive

import shapeless.{ Lazy => _, _ }
import shapeless.labelled.{ field, FieldType }

import shapeless.compat.{ Strict, Lazy, Default }

trait MkDecodeJson[T] {
  def decodeJson: DecodeJson[T]
}

trait ProductDecodeJson[T] {
  def apply(productCodec: JsonProductCodec): DecodeJson[T]
}

trait HListProductDecodeJson[L <: HList, D <: HList] {
  def apply(productCodec: JsonProductCodec, defaults: D): DecodeJson[L]
}

object HListProductDecodeJson {
  def apply[L <: HList, D <: HList](implicit decodeJson: HListProductDecodeJson[L, D]): HListProductDecodeJson[L, D] =
    decodeJson

  implicit def hnilDecodeJson: HListProductDecodeJson[HNil, HNil] =
    new HListProductDecodeJson[HNil, HNil] {
      def apply(productCodec: JsonProductCodec, defaults: HNil) =
        new DecodeJson[HNil] {
          def decode(c: HCursor) =
            productCodec
              .decodeEmpty(c)
              .map(_ => HNil)
        }
    }

  implicit def hconsDecodeJson[K <: Symbol, H, T <: HList, TD <: HList]
   (implicit
     key: Witness.Aux[K],
     headDecode: Strict[DecodeJson[H]],
     tailDecode: HListProductDecodeJson[T, TD]
   ): HListProductDecodeJson[FieldType[K, H] :: T, Option[H] :: TD] =
    new HListProductDecodeJson[FieldType[K, H] :: T, Option[H] :: TD] {
      def apply(productCodec: JsonProductCodec, defaults: Option[H] :: TD) =
        new DecodeJson[FieldType[K, H] :: T] {
          lazy val tailDecode0 = tailDecode(productCodec, defaults.tail)

          def decode(c: HCursor) = {
            for {
              x <- productCodec.decodeField(key.value.name, c, headDecode.value, defaults.head)
              (h, remaining) = x
              t <- remaining.as(tailDecode0)
            } yield field[K](h) :: t
          }
        }
    }
}

object ProductDecodeJson {
  def apply[P](implicit decodeJson: ProductDecodeJson[P]): ProductDecodeJson[P] = decodeJson

  // Re-enable by making a dummy HList of defaults made of Option[_]
  // implicit def recordDecodeJson[R <: HList]
  //  (implicit
  //    underlying: HListProductDecodeJson[R]
  //  ): ProductDecodeJson[R] =
  //   new ProductDecodeJson[R] {
  //     def apply(productCodec: JsonProductCodec) =
  //       underlying(productCodec)
  //   }

  implicit def genericDecodeJson[P, L <: HList, D <: HList]
   (implicit
     gen: LabelledGeneric.Aux[P, L],
     defaults: Default.AsOptions.Aux[P, D],
     underlying: Lazy[HListProductDecodeJson[L, D]]
   ): ProductDecodeJson[P] =
    new ProductDecodeJson[P] {
      def apply(productCodec: JsonProductCodec) =
        underlying.value(productCodec, defaults())
          .map(gen.from)
    }
}

trait SumDecodeJson[S] {
  def apply(sumCodec: JsonSumCodec): DecodeJson[S]
}

trait CoproductSumDecodeJson[C <: Coproduct] {
  def apply(sumCodec: JsonSumCodec): DecodeJson[C]
}

object CoproductSumDecodeJson {
  def apply[C <: Coproduct](implicit decodeJson: CoproductSumDecodeJson[C]): CoproductSumDecodeJson[C] =
    decodeJson

  implicit def cnilDecodeJson: CoproductSumDecodeJson[CNil] =
    new CoproductSumDecodeJson[CNil] {
      def apply(sumCodec: JsonSumCodec) =
        new DecodeJson[CNil] {
          def decode(c: HCursor) =
            sumCodec
              .decodeEmpty(c)
              .map(t => t: CNil)
        }
    }

  implicit def cconsDecodeJson[K <: Symbol, H, T <: Coproduct]
   (implicit
     key: Witness.Aux[K],
     headDecode: Lazy[DecodeJson[H]],
     tailDecode: CoproductSumDecodeJson[T]
   ): CoproductSumDecodeJson[FieldType[K, H] :+: T] =
    new CoproductSumDecodeJson[FieldType[K, H] :+: T] {
      def apply(sumCodec: JsonSumCodec) =
        new DecodeJson[FieldType[K, H] :+: T] {
          lazy val tailDecode0 = tailDecode(sumCodec)

          def decode(c: HCursor) =
            sumCodec.decodeField(key.value.name, c, headDecode.value).flatMap {
              case Left(tailCursor) => tailCursor.as(tailDecode0).map(Inr(_))
              case Right(h) => DecodeResult.ok(Inl(field[K](h)))
            }
        }
    }
}

object SumDecodeJson {
  def apply[S](implicit decodeJson: SumDecodeJson[S]): SumDecodeJson[S] = decodeJson

  implicit def unionDecodeJson[U <: Coproduct]
   (implicit
     underlying: CoproductSumDecodeJson[U]
   ): SumDecodeJson[U] =
    new SumDecodeJson[U] {
      def apply(sumCodec: JsonSumCodec) =
        underlying(sumCodec)
    }

  implicit def genericDecodeJson[S, C <: Coproduct]
   (implicit
     gen: LabelledGeneric.Aux[S, C],
     underlying: Strict[CoproductSumDecodeJson[C]]
   ): SumDecodeJson[S] =
    new SumDecodeJson[S] {
      def apply(sumCodec: JsonSumCodec) =
        underlying.value(sumCodec)
          .map(gen.from)
    }
}


object MkDecodeJson {
  def apply[T](implicit decodeJson: MkDecodeJson[T]): MkDecodeJson[T] = decodeJson

  implicit def productDecodeJson[P]
   (implicit
     underlying: ProductDecodeJson[P],
     codecFor: JsonProductCodecFor[P]
   ): MkDecodeJson[P] =
    new MkDecodeJson[P] {
      def decodeJson = underlying(codecFor.codec)
    }

  implicit def sumDecodeJson[S]
   (implicit
     underlying: SumDecodeJson[S],
     codecFor: JsonSumCodecFor[S]
   ): MkDecodeJson[S] =
    new MkDecodeJson[S] {
      def decodeJson = underlying(codecFor.codec)
    }
}
