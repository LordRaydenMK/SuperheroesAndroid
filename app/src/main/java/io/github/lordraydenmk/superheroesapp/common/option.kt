package io.github.lordraydenmk.superheroesapp.common

import arrow.core.Either

/**
 * arrow.core.Option is getting deprecated in favor or Nullable types.
 *
 * This is the next best thing for when you can't use nullable types
 */
typealias Option<A> = Either<Unit, A>