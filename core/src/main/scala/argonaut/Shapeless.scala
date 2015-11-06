package argonaut

import shapeless.{ Cached, Strict, Widen, Witness }
import _root_.derive.LowPriority
import argonaut.derive._

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

trait DefaultProductCodec {
  implicit def defaultJsonProductCodecFor[T]: JsonProductCodecFor[T] =
    JsonProductCodecFor(JsonProductCodec.obj)
}

trait DefaultSumCodec {
  implicit def defaultJsonSumCodecFor[T]: JsonSumCodecFor[T] =
    JsonSumCodecFor(JsonSumCodec.obj)
}

trait DerivedInstances extends DefaultProductCodec with DefaultSumCodec {

  implicit def mkEncodeJson[T]
   (implicit
     priority: Strict[LowPriority[EncodeJson[T], MkEncodeJson[T]]]
   ): EncodeJson[T] =
    priority.value.value.encodeJson

  implicit def mkDecodeJson[T]
   (implicit
     priority: Strict[LowPriority[DecodeJson[T], MkDecodeJson[T]]]
   ): DecodeJson[T] =
    priority.value.value.decodeJson

}

trait CachedDerivedInstances extends DefaultProductCodec with DefaultSumCodec {

  implicit def mkEncodeJson[T]
   (implicit
     priority: Cached[Strict[LowPriority[EncodeJson[T], MkEncodeJson[T]]]]
   ): EncodeJson[T] =
    priority.value.value.value.encodeJson

  implicit def mkDecodeJson[T]
   (implicit
     priority: Cached[Strict[LowPriority[DecodeJson[T], MkDecodeJson[T]]]]
   ): DecodeJson[T] =
    priority.value.value.value.decodeJson

}

object Shapeless
  extends SingletonInstances
  with DerivedInstances {

  object Cached
    extends SingletonInstances
    with CachedDerivedInstances

}
