package argonaut.derive

import shapeless.Witness

// Put under the argonaut.derive namespace, not to pollute argonaut, for lack of a better namespace around

trait Widen[S] {
  type T
  def to(s: S): T
  def from(t: T): Option[S]
}

// Non subtyping widen: Symbol (the way shapeless does it)

class SubTypingWiden[S, T0 >: S](s: S) extends Widen[S] {
  type T = T0
  def to(s: S): T = s
  def from(t: T): Option[S] = if (t == s) Some(s) else None
}

object Widen {
  def apply[S](implicit w: Widen[S]): Aux[S, w.T] = w

  type Aux[S, T0] = Widen[S] { type T = T0 }


  implicit def stringWiden[S <: String](implicit witness: Witness.Aux[S]): Aux[S, String] =
    new SubTypingWiden[S, String](witness.value)

  // Missing: Symbol (shapeless way of making it a singleton type)

  implicit def longWiden[L <: Long](implicit witness: Witness.Aux[L]): Aux[L, Long] =
    new SubTypingWiden[L, Long](witness.value)

  implicit def intWiden[I <: Int](implicit witness: Witness.Aux[I]): Aux[I, Int] =
    new SubTypingWiden[I, Int](witness.value)

  implicit def shortWiden[S <: Short](implicit witness: Witness.Aux[S]): Aux[S, Short] =
    new SubTypingWiden[S, Short](witness.value)

  implicit def byteWiden[B <: Byte](implicit witness: Witness.Aux[B]): Aux[B, Byte] =
    new SubTypingWiden[B, Byte](witness.value)


  implicit def doubleWiden[D <: Double](implicit witness: Witness.Aux[D]): Aux[D, Double] =
    new SubTypingWiden[D, Double](witness.value)

  implicit def floatWiden[F <: Float](implicit witness: Witness.Aux[F]): Aux[F, Float] =
    new SubTypingWiden[F, Float](witness.value)


  implicit def booleanWiden[B <: Boolean](implicit witness: Witness.Aux[B]): Aux[B, Boolean] =
    new SubTypingWiden[B, Boolean](witness.value)

}
