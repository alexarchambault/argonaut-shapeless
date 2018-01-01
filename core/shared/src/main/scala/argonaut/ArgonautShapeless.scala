package argonaut

import argonaut.derive._

object ArgonautShapeless extends ArgonautShapeless

trait ArgonautShapeless
  extends SingletonInstances
  with DerivedInstances {

  object Cached
    extends SingletonInstances
    with CachedDerivedInstances
}
