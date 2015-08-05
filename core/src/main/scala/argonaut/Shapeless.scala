package argonaut

import shapeless.{ Strict, LowPriority, Witness }
import argonaut.derive._

trait SingletonInstances {

  implicit def singletonTypeEncodeJson[S, W]
   (implicit
     w: Witness.Aux[S],
     widen: Widen.Aux[S, W],
     underlying: EncodeJson[W]
   ): EncodeJson[S] =
    underlying.contramap[S](widen.to)

  implicit def singletonTypeDecodeJson[S, W]
   (implicit
     w: Witness.Aux[S],
     widen: Widen.Aux[S, W],
     underlying: DecodeJson[W]
   ): DecodeJson[S] =
    DecodeJson { c =>
      underlying.decode(c).flatMap { w0 =>
        widen.from(w0) match {
          case Some(s) => DecodeResult.ok(s)
          case None => DecodeResult.fail(s"Expected ${w.value}, got $w0", c.history)
        }
      }
    }

}

trait DerivedInstances {

  implicit def defaultJsonProductCodecFor[T]: JsonProductCodecFor[T] =
    new JsonProductCodecFor[T] {
      def codec = JsonProductCodec.obj
    }
  implicit def defaultJsonSumCodecFor[T]: JsonSumCodecFor[T] =
    new JsonSumCodecFor[T] {
      def codec = JsonSumCodec.obj
    }

  implicit def mkEncodeJson[T]
   (implicit
     priority: Strict.Cached[LowPriority[EncodeJson[T], MkEncodeJson[T]]]
   ): EncodeJson[T] =
    priority.value.value.encodeJson

  implicit def mkDecodeJson[T]
   (implicit
     priority: Strict.Cached[LowPriority[DecodeJson[T], MkDecodeJson[T]]]
   ): DecodeJson[T] =
    priority.value.value.decodeJson

}

object Shapeless
  extends SingletonInstances
  with DerivedInstances