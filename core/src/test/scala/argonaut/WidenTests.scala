package argonaut

import argonaut.derive.Widen
import shapeless.Witness
import shapeless.test.illTyped
import utest._

object WidenTests extends TestSuite {

  val tests = TestSuite {
    'string {
      val w = Widen[Witness.`"aa"`.T]
      val s: String = "other"
      val s0: w.T = s
      illTyped("""
        val s1: w.T = true
      """)
      illTyped("""
        val s2: w.T = 41
      """)
    }

    'int {
      val w = Widen[Witness.`23`.T]
      val n: Int = 43
      val n0: w.T = n
      illTyped("""
        val n1: w.T = true
      """)
      illTyped("""
        val n2: w.T = "aa"
      """)
    }

    'double {
      val w = Widen[Witness.`1.2`.T]
      val d: Double = 1.3
      val d0: w.T = d
      illTyped("""
        val d1: w.T = true
      """)
      illTyped("""
        val d2: w.T = "aa"
      """)
      // val d3: w.T = 2 // Passing, sigh
    }
  }

}

