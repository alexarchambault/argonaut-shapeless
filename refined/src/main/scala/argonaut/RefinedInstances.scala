package argonaut

import eu.timepit.refined._
import eu.timepit.refined.internal.{ Refine, Wrapper }
import shapeless.tag.@@
import language.higherKinds

trait RefinedInstances {

  implicit def refinedDecodeJson[T, P, F[_, _]]
   (implicit
     underlying: DecodeJson[T],
     predicate: Predicate[P, T],
     wrapper: Wrapper[F]
   ): DecodeJson[F[T, P]] =
    DecodeJson { c =>
      underlying.decode(c).flatMap { t0 =>
        new Refine[P, F].apply[T](t0) match {
          case Left(err) => DecodeResult.fail(err, c.history)
          case Right(t)  => DecodeResult.ok(t)
        }
      }
    }

  implicit def refinedTEncodeJson[T, P]
   (implicit
     underlying: EncodeJson[T],
     predicate: Predicate[P, T]
   ): EncodeJson[T @@ P] =
    underlying.contramap(t => t: T)

  implicit def refinedVEncodeJson[T, P]
   (implicit
     underlying: EncodeJson[T],
     predicate: Predicate[P, T]
   ): EncodeJson[Refined[T, P]] =
    underlying.contramap(_.get)

}

object Refined extends RefinedInstances