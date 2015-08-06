package argonaut

import eu.timepit.refined._
import eu.timepit.refined.internal.{ RefineAux, Wrapper }
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
        new RefineAux[P, F].apply[T](t0) match {
          case Left(err) => DecodeResult.fail(err, c.history)
          case Right(t)  => DecodeResult.ok(t)
        }
      }
    }

  implicit def refinedEncodeJson[T, P, F[_, _]]
   (implicit
     underlying: EncodeJson[T],
     predicate: Predicate[P, T],
     wrapper: Wrapper[F]
   ): EncodeJson[F[T, P]] =
    underlying.contramap(wrapper.unwrap)

}

object Refined extends RefinedInstances