package argonaut

import shapeless.{ Strict, LowPriority }
import argonaut.derive._

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

object Shapeless extends DerivedInstances