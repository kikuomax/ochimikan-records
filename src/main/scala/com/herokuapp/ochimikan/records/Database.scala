package com.herokuapp.ochimikan.records

/** The interface of the score database. */
trait Database {
  /**
   * Returns the list of scores in this database.
   *
   * @param from
   *     The index from which scores are to be obtained.
   *     If `None`, scores will be obtained from the beginning.
   * @param to
   *     The index until which scores are to be obtained.
   *     If `None`, scores will be obtained until the end.
   * @throws IllegalArgumentException
   *     If both `from` and `to` are specified and `from` > `to`.
   * @throws DatabaseException
   *     If some error has occurred during accessing this database.
   */
  def scores(from: Option[Int], to: Option[Int]): ScoreList

  /**
   * Registers a specified score into this database.
   *
   * @param score
   *     The score to be registered into this database.
   * @throws DatabaseException
   *     If some error has occurred during accessing this database.
   */
  def addScore(score: Score): Unit
}
