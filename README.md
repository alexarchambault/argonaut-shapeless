# argonaut-shapeless

Automatic argonaut codec derivation with shapeless

## Usage

Add to your `build.sbt`
```scala
libraryDependencies += "com.github.alexarchambault" %% "argonaut-shapeless" % "6.1-SNAPSHOT"
```

Then import the content of `argonaut.Shapeless` along with the one of `argonaut.Argonaut` close to where you want codecs to be automatically available for case classes / sealed hierarchies:
```scala
import argonaut._, Argonaut._, Shapeless._

//  If you defined:

// case class Foo(i: Int, s: String, blah: Boolean)
// case class Bar(foo: Foo, other: String)

//  then you can now do

implicitly[EncodeJson[Foo]]
implicitly[EncodeJson[Bar]]
implicitly[DecodeJson[Foo]]
implicitly[DecodeJson[Bar]]

```

Available for scala 2.10 and 2.11. Uses argonaut 6.1-SNAPSHOT and shapeless 2.1-SNAPSHOT.
