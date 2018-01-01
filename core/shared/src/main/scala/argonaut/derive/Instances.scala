package argonaut.derive

import argonaut.{ DecodeJson, DecodeResult, EncodeJson }

import shapeless.{ Cached, LowPriority, Strict, Widen, Witness }

trait SingletonInstances {

  implicit def singletonTypeEncodeJson[S, W >: S]
   (implicit
     w: Witness.Aux[S],
     widen: Widen.Aux[S, W],
     underlying: EncodeJson[W]
   ): EncodeJson[S] =
    underlying.contramap[S](widen.apply)

  implicit def singletonTypeDecodeJson[S, W >: S]
   (implicit
     w: Witness.Aux[S],
     widen: Widen.Aux[S, W],
     underlying: DecodeJson[W]
   ): DecodeJson[S] =
    DecodeJson { c =>
      underlying.decode(c).flatMap { w0 =>
        if (w0 == w.value)
          DecodeResult.ok(w.value)
        else
          DecodeResult.fail(s"Expected ${w.value}, got $w0", c.history)
      }
    }
}

trait DerivedInstances {

  implicit def derivedEncodeJson[T]
   (implicit
     ev: LowPriority,
     underlying: Strict[MkEncodeJson[T]]
   ): EncodeJson[T] =
    underlying.value.encodeJson

  implicit def derivedDecodeJson[T]
   (implicit
     ev: LowPriority,
     underlying: Strict[MkDecodeJson[T]]
   ): DecodeJson[T] =
    underlying.value.decodeJson
}

trait CachedDerivedInstances {

  implicit def cachedDerivedEncodeJson[T]
   (implicit
     ev: LowPriority,
     underlying: Cached[Strict[MkEncodeJson[T]]]
   ): EncodeJson[T] =
    underlying.value.value.encodeJson

  implicit def cachedDerivedDecodeJson[T]
   (implicit
     ev: LowPriority,
     underlying: Cached[Strict[MkDecodeJson[T]]]
   ): DecodeJson[T] =
    underlying.value.value.decodeJson
}
