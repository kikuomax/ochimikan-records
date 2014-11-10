package com.herokuapp.ochimikan
package records
package mongo

import com.mongodb.casbah.commons.Implicits.wrapDBObj
import records.DatabaseException

/**
 * The [[records.ScoreList]] backed by '''MongoDB'''.
 *
 * @constructor
 * @param database
 *     The database which owns this score list.
 */
class ScoreList(private[mongo] val database: Database)
  extends records.ScoreList
{
  /**
   * ''mongo.ScoreList specific behavior.''
   *
   * @throws DatabaseException
   *     If scores in the database are corrupted.
   */
  override def scores: Seq[Score] =
    try
      database.scoreCollection.find().map { new Score(_) }.toVector
    catch {
      case e @ (_ : ClassCastException | _ : IllegalArgumentException) =>
        throw DatabaseException(e.getMessage(), e)
    }
}
