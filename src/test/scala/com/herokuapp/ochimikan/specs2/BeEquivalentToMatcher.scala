package com.herokuapp.ochimikan.specs2

import org.specs2.matcher.{
  Expectable,
  Matcher
}

/**
 * Provides the `beEquivalentTo` matcher.
 *
 * `beEquivalentTo` matcher expects equivalence in ordering.
 * Values must be viewable as `Ordered`.
 */
trait BeEquivalentToMatcher {
  /** `beEquivalentTo` matcher. */
  def beEquivalentTo[S <% Ordered[S]](n: S) = new BeEquivalentTo(n)

  /** A `Matcher` which expects equivalence in ordering. */
  class BeEquivalentTo[T <% Ordered[T]](n: T) extends Matcher[T] {
    override def apply[S <: T](a: Expectable[S]) = {
      val r = a.value.compareTo(n) == 0
      result(r,
             a.description + " is equivalent to " + n.toString,
             a.description + " is not equivalent to " + n.toString,
             a)
    }
  }
}

/** Companion object of [[BeEquivalentToMatcher]]. */
object BeEquivalentToMatcher extends BeEquivalentToMatcher
