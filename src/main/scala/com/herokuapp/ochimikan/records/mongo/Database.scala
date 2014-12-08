package com.herokuapp.ochimikan
package records
package mongo

import com.mongodb.MongoException
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject

/**
 * The [[records.Database]] backed by '''MongoDB'''.
 *
 * @constructor
 * @param client
 *     The '''MongoDB''' client connecting to the score server.
 * @param dbName
 *     The name of the database to connect to.
 */
class Database(client: MongoClient, dbName: String) extends records.Database {
  // the database
  private[mongo] val database = client(dbName)
  // the score collection
  private[mongo] val scoreCollection = database("scores")

  /**
   * ''mongo.Database specific behavior.''
   *
   * @throws DatabaseException
   *     If scores in the database are corrupted.
   */
  override def scores(from: Option[Int], to: Option[Int]): ScoreList =
    new ScoreList(this, from, to)

  /**
   * ''mongo.Database specific behavior.''
   *
   * @throws DatabaseException
   *     If scores in the database are corrupted.
   */
  override def addScore(score: records.Score) =
    try {
      scoreCollection.insert(MongoDBObject(
        "value"  -> score.value,
        "level"  -> score.level,
        "player" -> score.player,
        "date"   -> score.date
      ))
    } catch {
      case e: MongoException => throw DatabaseException(e)
    }
}
