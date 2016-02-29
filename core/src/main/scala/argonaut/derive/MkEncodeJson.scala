package argonaut
package derive

import shapeless._
import shapeless.labelled.FieldType

trait MkEncodeJson[T] {
  def encodeJson: EncodeJson[T]
}

trait ProductEncodeJson[T] {
  def apply(productCodec: JsonProductCodec): EncodeJson[T]
}

trait HListProductEncodeJson[L <: HList, D <: HList] {
  def apply(productCodec: JsonProductCodec, defaults: D): EncodeJson[L]
}

object HListProductEncodeJson {
  def apply[L <: HList, D <: HList](implicit encodeJson: HListProductEncodeJson[L, D]): HListProductEncodeJson[L, D] =
    encodeJson

  implicit def hnilEncodeJson: HListProductEncodeJson[HNil, HNil] =
    new HListProductEncodeJson[HNil, HNil] {
      def apply(productCodec: JsonProductCodec, defaults: HNil) =
        new EncodeJson[HNil] {
          def encode(l: HNil) = productCodec.encodeEmpty
        }
    }

  implicit def hconsEncodeJson[K <: Symbol, H, T <: HList, TD <: HList]
   (implicit
     key: Witness.Aux[K],
     headEncode: Strict[EncodeJson[H]],
     tailEncode: HListProductEncodeJson[T, TD]
   ): HListProductEncodeJson[FieldType[K, H] :: T, Option[H] :: TD] =
    new HListProductEncodeJson[FieldType[K, H] :: T, Option[H] :: TD] {
      def apply(productCodec: JsonProductCodec, defaults: Option[H] :: TD) =
        new EncodeJson[FieldType[K, H] :: T] {
          lazy val defaultOpt = defaults.head.map(headEncode.value.encode)
          lazy val tailEncode0 = tailEncode(productCodec, defaults.tail)

          def encode(l: FieldType[K, H] :: T) =
            productCodec.encodeField(
              key.value.name -> headEncode.value.encode(l.head),
              tailEncode0.encode(l.tail),
              defaultOpt
            )
        }
    }
}

object ProductEncodeJson {
  def apply[P](implicit encodeJson: ProductEncodeJson[P]): ProductEncodeJson[P] = encodeJson

  // TODO Generate an HList made of Option[...] as to use as default
  // implicit def recordEncodeJson[R <: HList]
  //  (implicit
  //    underlying: HListProductEncodeJson[R]
  //  ): ProductEncodeJson[R] =
  //   new ProductEncodeJson[R] {
  //     def apply(productCodec: JsonProductCodec) =
  //       underlying(productCodec)
  //   }

  implicit def genericEncodeJson[P, L <: HList, D <: HList]
   (implicit
     gen: LabelledGeneric.Aux[P, L],
     defaults: Default.AsOptions.Aux[P, D],
     underlying: Lazy[HListProductEncodeJson[L, D]]
   ): ProductEncodeJson[P] =
    new ProductEncodeJson[P] {
      def apply(productCodec: JsonProductCodec) =
        underlying.value(productCodec, defaults())
          .contramap(gen.to)
    }
}


trait SumEncodeJson[S] {
  def apply(sumCodec: JsonSumCodec): EncodeJson[S]
}

trait CoproductSumEncodeJson[C <: Coproduct] {
  def apply(sumCodec: JsonSumCodec): EncodeJson[C]
}

object CoproductSumEncodeJson {
  def apply[C <: Coproduct](implicit encodeJson: CoproductSumEncodeJson[C]): CoproductSumEncodeJson[C] =
    encodeJson

  implicit def cnilEncodeJson: CoproductSumEncodeJson[CNil] =
    new CoproductSumEncodeJson[CNil] {
      def apply(sumCodec: JsonSumCodec) =
        new EncodeJson[CNil] {
          def encode(c: CNil) = 
            sumCodec.encodeEmpty
        }
    }

  implicit def cconsEncodeJson[K <: Symbol, H, T <: Coproduct]
   (implicit
     key: Witness.Aux[K],
     headEncode: Lazy[EncodeJson[H]],
     tailEncode: CoproductSumEncodeJson[T]
   ): CoproductSumEncodeJson[FieldType[K, H] :+: T] =
    new CoproductSumEncodeJson[FieldType[K, H] :+: T] {
      def apply(sumCodec: JsonSumCodec) =
        new EncodeJson[FieldType[K, H] :+: T] {
          lazy val tailEncode0 = tailEncode(sumCodec)

          def encode(c: FieldType[K, H] :+: T) =
            c match {
              case Inl(h) =>
                sumCodec.encodeField(
                  Right(key.value.name -> headEncode.value.encode(h))
                )
              case Inr(r) =>
                tailEncode0(r)
            }
        }
    }
}

object SumEncodeJson {
  def apply[S](implicit encodeJson: SumEncodeJson[S]): SumEncodeJson[S] = encodeJson

  implicit def unionEncodeJson[U <: Coproduct]
   (implicit
     underlying: CoproductSumEncodeJson[U]
   ): SumEncodeJson[U] =
    new SumEncodeJson[U] {
      def apply(sumCodec: JsonSumCodec) =
        underlying(sumCodec)
    }

  implicit def genericEncodeJson[S, C <: Coproduct]
   (implicit
     gen: LabelledGeneric.Aux[S, C],
     underlying: Strict[CoproductSumEncodeJson[C]]
   ): SumEncodeJson[S] =
    new SumEncodeJson[S] {
      def apply(sumCodec: JsonSumCodec) =
        underlying.value(sumCodec)
          .contramap(gen.to)
    }
}


object MkEncodeJson {
  def apply[T](implicit encodeJson: MkEncodeJson[T]): MkEncodeJson[T] = encodeJson

  implicit def productEncodeJson[P]
   (implicit
     underlying: Strict[ProductEncodeJson[P]],
     codecFor: JsonProductCodecFor[P]
   ): MkEncodeJson[P] =
    new MkEncodeJson[P] {
      def encodeJson = underlying.value(codecFor.codec)
    }

  implicit def sumEncodeJson[S]
   (implicit
     underlying: Strict[SumEncodeJson[S]],
     codecFor: JsonSumCodecFor[S]
   ): MkEncodeJson[S] =
    new MkEncodeJson[S] {
      def encodeJson = underlying.value(codecFor.codec)
    }
}
