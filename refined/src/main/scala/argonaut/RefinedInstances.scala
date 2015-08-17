package argonaut

import eu.timepit.refined.Predicate
import eu.timepit.refined.RefType
import language.higherKinds

trait RefinedInstances {

  implicit def refinedDecodeJson[T, P, F[_, _]]
   (implicit
     underlying: DecodeJson[T],
     predicate: Predicate[P, T],
     refType: RefType[F]
   ): DecodeJson[F[T, P]] =
    DecodeJson { c =>
      underlying.decode(c).flatMap { t0 =>
        refType.refine(t0) match {
          case Left(err) => DecodeResult.fail(err, c.history)
          case Right(t)  => DecodeResult.ok(t)
        }
      }
    }

  implicit def refinedEncodeJson[T, P, F[_, _]]
   (implicit
     underlying: EncodeJson[T],
     refType: RefType[F]
   ): EncodeJson[F[T, P]] =
    underlying.contramap(refType.unwrap)

}

object Refined extends RefinedInstances
