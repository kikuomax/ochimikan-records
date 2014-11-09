package com.herokuapp.ochimikan.records

import java.util.Date

/**
 * A score.
 *
 * @constructor
 * @param value
 *     The value of this score.
 * @param level
 *     The level at which this score was achieved.
 * @param player
 *     The name of the player who achieved this score.
 * @param date
 *     The date when this score was achieved.
 */
case class Score(value: Int, level: Int, player: String, date: Date)

/** Companion object of [[Score]]. */
object Score {
  /** Orders [[Score]]s by values. */
  implicit object ScoreOrdering extends Ordering[Score] {
    /**
     * Compares specified two [[Score]]s for order.
     *
     * Ordering of [[Score]]s is determined so that a better [[Score]] comes
     * later.
     *
     * If two [[Score]]s `lhs` and `rhs` are given,
     *  1. `lhs` is better than `rhs` if `lhs.value` > `rhs.value`
     *  2. If values are the same,
     *     `lhs` is better than `rhs` if `lhs.level` < `rhs.level`
     *  3. If both values and levels are the same,
     *     `lhs` is better than `rhs` if `lhs.date` < `rhs.date`
     *  4. If all of values, levels and dates are the same,
     *     `lhs` and `rhs` are tie.
     *
     * @param lhs
     *     The left hand side of the comparison.
     * @param rhs
     *     The right hand side of the comparison.
     * @return
     *     - Negative integer if `lhs` is worse than `rhs`
     *     - 0 if `lhs` and `rhs` are tie
     *     - Positive integer if `lhs` is better than `rhs`
     */
    override def compare(lhs: Score, rhs: Score) = {
      var r = lhs.value.compareTo(rhs.value)
      if (r == 0) {
        r = rhs.level.compareTo(lhs.level)
        if (r == 0)
          r = rhs.date.compareTo(lhs.date)
      }
      r
    }
  }
}
