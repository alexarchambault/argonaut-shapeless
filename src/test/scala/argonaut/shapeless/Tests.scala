package argonaut
package shapeless

import Argonaut._, Shapeless._

import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.prop.PropertyChecks
import org.scalatest.{Matchers, PropSpec}


case class Empty()

case class Foo(i: Int, s: String, blah: Boolean)

case class Bar(foo: Foo, other: String)

case class FooBar(foo: Foo, bar: Bar, /* js: Json, /* weird runtime error with this one */ */ v: Int)


class Tests extends PropSpec with Matchers with PropertyChecks {
  implicit val genEmpty: Gen[Empty] = 
    Gen.const(Empty())
  
  implicit val arbitraryEmpty = Arbitrary(genEmpty)

  implicit val genFoo: Gen[Foo] = 
    for {
      i <- implicitly[Arbitrary[Int]].arbitrary
      s <- Arbitrary.arbString.arbitrary
      b <- Arbitrary.arbBool.arbitrary
    } yield Foo(i, s, b)

  implicit val arbitraryFoo = Arbitrary(genFoo)

  implicit val genBar: Gen[Bar] =
    for {
      foo <- implicitly[Arbitrary[Foo]].arbitrary
      other <- Arbitrary.arbString.arbitrary
    } yield Bar(foo, other)
  
  implicit val arbitraryBar = Arbitrary(genBar)
  
  implicit val genFooBar: Gen[FooBar] =
    for {
      foo <- implicitly[Arbitrary[Foo]].arbitrary
      bar <- implicitly[Arbitrary[Bar]].arbitrary
//      json <- Gen.oneOf( // Not testing this one too exhaustively...
//        implicitly[Arbitrary[Empty]].arbitrary.map(_.asJson),
//        implicitly[Arbitrary[Foo]].arbitrary.map(_.asJson),
//        implicitly[Arbitrary[Bar]].arbitrary.map(_.asJson)
//      )
      v <- implicitly[Arbitrary[Int]].arbitrary
    } yield FooBar(foo, bar, v)

  implicit val arbitraryFooBar = Arbitrary(genFooBar)


  property("Empty must not change after serialization/deserialization") {
    forAll { f: Empty =>            
      assert(f.asJson.as[Empty].result.bimap(_ => false, f.==).merge)
    }
  }
  
  property("Foo must not change after serialization/deserialization") {
    forAll { f: Foo =>
      assert(f.asJson.as[Foo].result.bimap(_ => false, f.==).merge)
    }
  }

  property("Bar must not change after serialization/deserialization") {
    forAll { f: Bar =>
      assert(f.asJson.as[Bar].result.bimap(_ => false, f.==).merge)
    }
  }
  
  property("FooBar must not change after serialization/deserialization") {
    forAll { f: FooBar =>
      assert(f.asJson.as[FooBar].result.bimap(_ => false, f.==).merge)
    }
  }
  
}
