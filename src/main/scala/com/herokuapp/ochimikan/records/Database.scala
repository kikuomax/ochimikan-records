package com.herokuapp.ochimikan.records

/** The interface of the score database. */
trait Database {
  /**
   * The list of scores in this database.
   *
   * @throws DatabaseException
   *     If some error has occurred during accessing this database.
   */
  def scores: ScoreList

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
