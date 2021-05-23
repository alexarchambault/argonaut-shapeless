package argonaut

import argonaut.derive.RefinedInstances

import language.higherKinds

object ArgonautRefined extends RefinedInstances

@deprecated("Use argonaut.ArgonautRefined instead. Will be removed in argonaut-shapeless 1.2.")
object Refined extends RefinedInstances
