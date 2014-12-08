package com.herokuapp.ochimikan.records

import java.util.Date

/** A score. */
trait Score {
  /** The value of this score. */
  def value: Int

  /** The level at which this score was achieved. */
  def level: Int

  /** The name of the player who achieved this score. */
  def player: String

  /** The date when this score was achieved. */
  def date: Date
}

/** Companion object of [[Score]]. */
object Score {
  /**
   * Creates an instance of [[Score]] of specified properties.
   *
   * @param value
   *     The value of the score.
   * @param level
   *     The level at which the score was achieved.
   * @param player
   *     The name of the player who achieved the score.
   * @param date
   *     The date when the score was achieved.
   * @throws IllegalArgumentException
   *     - If `value` < 0,
   *     - or if `level` < 0.
   */
  def apply(value: Int, level: Int, player: String, date: Date): Score =
    RawScore(value, level, player, date)

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

/**
 * The direct implementation of [[Score]].
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
 * @throws IllegalArgumentException
 *     - If `value` < 0,
 *     - or if `level` < 0.
 */
case class RawScore(value: Int, level: Int, player: String, date: Date)
  extends Score
{
  require(value >= 0, s"value must be >= 0 but $value")
  require(level >= 0, s"level must be >= 0 but $level")
}
