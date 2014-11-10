package com.herokuapp.ochimikan
package records
package mongo

import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject

/**
 * The [[records.Database]] backed by '''MongoDB'''.
 *
 * @constructor
 * @param client
 *     The '''MongoDB''' client connecting to the score server.
 */
class Database(client: MongoClient) extends records.Database {
  // the database
  private[mongo] val database = client("mikan")
  // the score collection
  private[mongo] val scoreCollection = database("scores")

  /**
   * ''mongo.Database specific behavior.''
   *
   * @throws DatabaseException
   *     If scores in the database are corrupted.
   */
  override def scores: ScoreList = new ScoreList(this)

  /**
   * ''mongo.Database specific behavior.''
   *
   * @throws DatabaseException
   *     If scores in the database are corrupted.
   */
  override def addScore(score: records.Score) =
    scoreCollection.insert(MongoDBObject(
      "value"  -> score.value,
      "level"  -> score.level,
      "player" -> score.player,
      "date"   -> score.date
    ))
}
