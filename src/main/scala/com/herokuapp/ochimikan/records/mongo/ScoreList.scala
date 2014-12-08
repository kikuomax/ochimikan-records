package com.herokuapp.ochimikan
package records
package mongo

import com.mongodb.casbah.MongoCursor
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.commons.Implicits.wrapDBObj
import com.mongodb.casbah.commons.Imports.DBObject
import records.DatabaseException

/**
 * The [[records.ScoreList]] backed by '''MongoDB'''.
 *
 * @constructor
 * @param database
 *     The database which owns this score list.
 * @param from
 *     The index from which scores are to be queried.
 *     If `None`, scores will be queried from the beginning.
 * @param to
 *     The index until which scores are to be queried.
 *     If `None`, scores will be queried until the end.
 * @throws IllegalArgumentException
 *     If both `from` and `to` are specified but `from` > `to`.
 */
class ScoreList(private[mongo] val database: Database,
  from: Option[Int], to: Option[Int]) extends records.ScoreList
{
  require((for {
    f <- from
    t <- to
  } yield (f <= t)).getOrElse(true), s"from must be <= to but $from > $to")

  /**
   * The sort rule.
   *
   * Scores are sorted by the following order,
   *  - `value`: descending
   *  - `level`: ascending
   *  - `date`:  ascending
   *
   */
  private val sorter = MongoDBObject("value" -> -1, "level" -> 1, "date" -> 1)

  /**
   * ''mongo.ScoreList specific behavior.''
   *
   * @throws DatabaseException
   *     If scores in the database are corrupted.
   */
  override def scores: Seq[Score] =
    try {
      val cursor = database.scoreCollection.find().sort(sorter)
      applyFrom(applyTo(cursor)).map(new Score(_)).toVector
    } catch {
      case e @ (_ : ClassCastException | _ : IllegalArgumentException) =>
        throw DatabaseException(e.getMessage(), e)
    }

  /** Applies `from` limitation to a specified cursor. */
  private def applyFrom(cursor: Iterator[DBObject]): Iterator[DBObject] =
    from.fold(cursor)(cursor.drop(_))

  /** Applies `to` limitation to a specified cursor. */
  private def applyTo(cursor: MongoCursor): MongoCursor =
    to.fold(cursor)(cursor.limit(_))
}
