# argonaut-shapeless

Automatic [argonaut](https://github.com/argonaut-io/argonaut) codec derivation with [shapeless](https://github.com/milessabin/shapeless)

## Usage

Add to your `build.sbt`
```scala
resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

libraryDependencies +=
  "com.github.alexarchambault" %% "argonaut-shapeless" % "6.1-SNAPSHOT"
```

Then import the content of `argonaut.Shapeless` along with the one of `argonaut.Argonaut` close to where you want codecs to be automatically available for case classes / sealed hierarchies:
```scala
import argonaut._, Argonaut._, Shapeless._

//  If you defined:

// case class Foo(i: Int, s: String, blah: Boolean)
// case class Bar(foo: Foo, other: String)

// sealed trait Base
// case class BaseIntString(i: Int, s: String) extends Base
// case class BaseDoubleBoolean(d: Double, b: Boolean) extends Base

//  then you can now do

implicitly[EncodeJson[Foo]]
implicitly[EncodeJson[Bar]]
implicitly[EncodeJson[Base]]

implicitly[DecodeJson[Foo]]
implicitly[DecodeJson[Bar]]
implicitly[DecodeJson[Base]]

```

Available for scala 2.10 and 2.11. Uses argonaut 6.1-M5 and shapeless 2.1.0-RC1.

Released under the BSD license. See LICENSE file for more details.

Based on an early (non `Lazy`-based) automatic codec derivation in argonaut
by [Maxwell Swadling](https://github.com/maxpow4h),
[Travis Brown](https://github.com/travisbrown), and
[Mark Hibberd](https://github.com/markhibberd).
