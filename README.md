# argonaut-shapeless

Automatic [argonaut](https://github.com/argonaut-io/argonaut) codec derivation with [shapeless](https://github.com/milessabin/shapeless)

[![Build Status](https://travis-ci.org/alexarchambault/argonaut-shapeless.svg)](https://travis-ci.org/alexarchambault/argonaut-shapeless)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/alexarchambault/argonaut-shapeless?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.alexarchambault/argonaut-shapeless_6.1_2.11.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.alexarchambault/argonaut-shapeless_6.1_2.11)

It is available for scala 2.10 and 2.11, and depends on argonaut 6.1. The master branch depends on shapeless 2.2.5, and the 1.1.x on shapeless 2.3.0.

argonaut-shapeless is part of the shapeless ecosystem of
[Typelevel](http://typelevel.org/), and as such endorses its
[Code of Conduct](http://typelevel.org/conduct.html).

It is one of the very first projects to have used `Lazy` from shapeless 2.1,
which made type class derivation with implicits much more robust.

## Compatibility table

| argonaut-shapeless | argonaut | shapeless | refined |
|--------------------|----------|-----------|---------|
| 1.2.x (to come)    | 6.2      | 2.3.x     | 0.5.x   |
| 1.1.x              | 6.1      | 2.3.x     | 0.4.x   |
| 1.0.x              | 6.1      | 2.2.x     | 0.3.5   |
| 0.3.x              | 6.1      | 2.2.x     | n/a     |

## Usage

Add to your `build.sbt`
```scala
resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies +=
  "com.github.alexarchambault" %% "argonaut-shapeless_6.1" % "1.1.1"
```

If you are using scala 2.10.x, also add the macro paradise plugin to your build,
```scala
libraryDependencies +=
  compilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full)
```

If you are using shapeless 2.2, replace `1.1.1` above by `1.0.0`.

## Features

The examples below assume you imported the content of
`argonaut`, `argonaut.Argonaut`, and `argonaut.ArgonautShapeless`, like
```scala
import argonaut._, Argonaut._, ArgonautShapeless._
```

### Automatic codecs for case classes

```scala
case class CC(i: Int, s: String)

// encoding
val encode = EncodeJson.of[CC]

val json = encode(CC(2, "a"))
json.nospaces == """{"i":2,"s":"a"}"""

// decoding
val decode = DecodeJson.of[CC]

val result = decode.decodeJson(json)
result == DecodeResult.ok(CC(2, "a"))
```




The way case classes are encoded can be customized, see below.

### Automatic codecs for sealed traits

```scala
sealed trait Base
case class First(i: Int) extends Base
case class Second(s: String) extends Base

// encoding
val encode = EncodeJson.of[Base]

val json = encode(First(2))
json.nospaces == """{"First":{"i":2}}"""

// decoding
val decode = DecodeJson.of[Base]

val result = decode.decodeJson(json)
result == DecodeResult.ok(First(2))
```




### Default values

Like [upickle](https://github.com/lihaoyi/upickle-pprint/),
fields equal to their default value are not put in the result JSON object.

```scala
case class CC(i: Int = 4, s: String = "foo")

CC().asJson.nospaces == "{}"
CC(i = 3).asJson.nospaces == """{"i":3}"""
CC(i = 4, s = "baz").asJson.nospaces == """{"s":"baz"}"""

"{}".decodeOption[CC] == Some(CC())
"""{"i":2}""".decodeOption[CC] == Some(CC(i = 2))
"""{"s":"a"}""".decodeOption[CC] == Some(CC(s = "a"))
```




### Custom encoding of case classes

When encoding / decoding a case class `C`, argonaut-shapeless looks
for an implicit `JsonProductCodecFor[C]`, which has a field
`codec: JsonProductCodec`. A `JsonProductCodec` provides a general way of
encoding / decoding case classes.

The default `JsonProductCodecFor[T]` for all types provides
`JsonProductCodec.obj`, which encodes / decodes case classes as shown above.

This default can be changed, e.g. to convert field names to `serpent_case`,




```scala
import argonaut.derive._

implicit def serpentCaseCodecFor[T]: JsonProductCodecFor[T] =
  JsonProductCodecFor(JsonProductCodec.adapt(_.toSerpentCase))

case class Identity(firstName: String, lastName: String)

Identity("Jacques", "Chirac").asJson.nospaces == """{"first_name":"Jacques","last_name":"Chirac"}"""
```







This can be changed for all types at once like just above, or only for specific
types, like
```scala
implicit def serpentCaseCodecForIdentity: JsonProductCodecFor[Identity] =
  JsonProductCodecFor(JsonProductCodec.adapt(_.toSerpentCase))
```




### Custom encoding for sealed traits

When encoding / decoding a sealed trait `S`, argonaut-shapeless looks
for an implicit `JsonSumCodecFor[S]`, which has a field
`codec: JsonSumCodec`. A `JsonSumCodec` provides a general way of
encoding / decoding sealed traits.

The default `JsonSumCodecFor[S]` for all types `S` provides
`JsonSumCodec.obj` as `JsonSumCodec`, which encodes sealed traits
as illustrated above.

`JsonSumCodec.typeField` is provided as an alternative,
which discriminates the various cases of a sealed trait by looking
at a field, `type`, like

```scala
implicit def typeFieldJsonSumCodecFor[S]: JsonSumCodecFor[S] =
  JsonSumCodecFor(JsonSumCodec.typeField)

sealed trait Base
case class First(i: Int) extends Base
case class Second(s: String) extends Base

val f: Base = First(2)

f.asJson.nospaces
f.asJson.nospaces == """{"type":"First","i":2}"""
// instead of the default """{"First":{"i":2}}"""
```




### Proper handling of custom codecs

Of course, if some of your types already have codecs (defined in their
companion object, or manually imported), these will be given the priority
over the ones derived by argonaut-shapeless, like

```scala
import argonaut._, Argonaut._, ArgonautShapeless._

case class Custom(s: String)

object Custom {
  implicit def encode: EncodeJson[Custom] =
    EncodeJson.of[String].contramap[Custom](_.s)
  implicit def decode: DecodeJson[Custom] =
    DecodeJson.of[String].map(Custom(_))
}
```




```scala
Custom("a").asJson.nospaces == """"a""""
""""b"""".decodeOption[Custom] == Some(Custom("b"))
```




### refined module

argonaut-shapeless also has a module to encode/decode types from
[refined](https://github.com/fthomas/refined), allowing for some
kind of validation at the type level.

Add it to your dependencies with
```scala
libraryDependencies += "com.github.alexarchambault" %% "argonaut-refined_6.1" % "1.1.1"
```

Use like
```scala
import argonaut._, Argonaut._, ArgonautShapeless._, ArgonautRefined._
import eu.timepit.refined._
import eu.timepit.refined.api.Refined

case class CC(
  i: Int Refined numeric.Greater[W.`5`.T],
  s: String Refined string.StartsWith[W.`"A"`.T]
)

CC(
  refineMV(6),
  refineMV("Abc")
).asJson.nospaces == """{"i":6,"s":"Abc"}""" // fields are encoded as their underlying type

"""{"i": 7, "s": "Abcd"}""".decodeOption[CC] == Some(CC(refineMV(7), refineMV("Abcd")))
"""{"i": 4, "s": "Abcd"}""".decodeOption[CC] == None // fails as the provided `i` doesn't meet the predicate ``GreaterThan[W.`5`.T]``
```




## See also

- [spray-json-shapeless](https://github.com/fommil/spray-json-shapeless/) features automatic codec derivation with shapeless for spray-json
- [circe](https://github.com/travisbrown/circe) features its own automatic codec derivation with shapeless, via its *generic* module

## Contributors

+ Alexandre Archambault ([@alexarchambault](https://github.com/alexarchambault/))
+ Ben James ([@bmjames](https://github.com/bmjames))
+ Denis Mikhaylov ([@notxcain](https://github.com/notxcain))
+ Frank S. Thomas ([@fthomas](https://github.com/fthomas))
+ Ismael Juma ([@ijuma](https://github.com/ijuma))
+ Your name here :-)

Initially based on an early (non `Lazy`-based) automatic codec derivation in argonaut
by [Maxwell Swadling](https://github.com/maxpow4h),
[Travis Brown](https://github.com/travisbrown), and
[Mark Hibberd](https://github.com/markhibberd).

## License

Released under the BSD license. See LICENSE file for more details.

