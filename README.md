# argonaut-shapeless

Automatic argonaut codec derivation with shapeless

## Usage

As argonaut 6.1-SNAPSHOT is not published as is on sonatype, you should
clone their repo and publish it locally first (`git clone https://github.com/argonaut-io/argonaut.git && cd argonaut && sbt publish-local` - use `sbt +publish-local` for scala 2.10).

Then add to your `build.sbt`
```scala
resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

libraryDependencies += "com.github.alexarchambault" %% "argonaut-shapeless" % "6.1-SNAPSHOT"
```

Import the content of `argonaut.Shapeless` along with the one of `argonaut.Argonaut` close to where you want codecs to be automatically available for case classes / sealed hierarchies:
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
