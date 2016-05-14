package argonaut

import argonaut.derive._

object Shapeless
  extends SingletonInstances
  with DerivedInstances {

  object Cached
    extends SingletonInstances
    with CachedDerivedInstances
}
