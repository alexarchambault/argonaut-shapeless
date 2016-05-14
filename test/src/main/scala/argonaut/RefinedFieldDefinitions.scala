package argonaut

import eu.timepit.refined.api.Refined
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
    i: Refined[Int, Positive],
    s: Refined[String, NonEmpty],
    l: Refined[List[Int], Size[Greater[_3]]]
  )
}
