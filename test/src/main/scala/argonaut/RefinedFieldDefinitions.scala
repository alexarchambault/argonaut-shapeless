package argonaut

import eu.timepit.refined.api.{ Refined => RefinedType }
import eu.timepit.refined.collection._
import eu.timepit.refined.numeric._

import shapeless.nat._3

object RefinedFieldDefinitions {

  case class Simple0(
    i: Int,
    s: String,
    l: List[Int]
  )

  case class SimpleV(
    i: RefinedType[Int, Positive],
    s: RefinedType[String, NonEmpty],
    l: RefinedType[List[Int], Size[Greater[_3]]]
  )
}
