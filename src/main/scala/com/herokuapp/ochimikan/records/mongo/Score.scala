package com.herokuapp.ochimikan
package records
package mongo

import com.mongodb.casbah.commons.MongoDBObject
import java.util.Date

/**
 * The [[records.Score]] backed by '''MongoDB'''.
 *
 * @constructor
 * @param data
 *     The underlying data of this score.
 * @throws ClassCastException
 *     - If `data("value")` is not an `Int`,
 *     - or if `data("level")` is not an `Int`,
 *     - or if `data("player")` is not a `String`,
 *     - or if `data("date")` is not a `Date`
 * @throws IllegalArgumentException
 *     - If `data("value")` is an `Int` but negative,
 *     - or if `data("level")` is an `Int` but < 1
 */
class Score(data: MongoDBObject) extends records.Score {
  override val value  = data.as[Int]("value")
  override val level  = data.as[Int]("level")
  override val player = data.as[String]("player")
  override val date   = data.as[Date]("date")

  // checks validity
  require(value >= 0, s"value must be >= 0 but $value")
  require(level >= 1, s"level must be >= 1 but $level")
}
