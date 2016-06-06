package argonaut

import argonaut.derive._

object ArgonautShapeless
  extends SingletonInstances
  with DerivedInstances {

  object Cached
    extends SingletonInstances
    with CachedDerivedInstances
}

@deprecated("Use argonaut.ArgonautShapeless instead. Will be removed in argonaut-shapeless 1.2.")
object Shapeless
  extends SingletonInstances
  with DerivedInstances {

  object Cached
    extends SingletonInstances
    with CachedDerivedInstances
}
