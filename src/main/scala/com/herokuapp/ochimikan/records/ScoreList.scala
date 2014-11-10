package com.herokuapp.ochimikan.records

/** The interface of a score list. */
trait ScoreList {
  /** The sequence of scores in this list. */
  def scores: Seq[Score]
}

/** Companion object of [[ScoreList]]. */
object ScoreList {
  /**
   * Constructs a [[ScoreList]] which has specified [[Score]]s in it.
   *
   * @param scores
   *     The sequence of scores.
   */
  def apply(scores: Seq[Score]): ScoreList = RawScoreList(scores)
}

/**
 * A direct implementation of [[ScoreList]].
 *
 * @constructor
 * @param scores
 *     The sequence of scores in this list.
 */
case class RawScoreList(scores: Seq[Score]) extends ScoreList
