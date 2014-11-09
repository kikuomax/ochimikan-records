package com.herokuapp.ochimikan.records.json

import com.herokuapp.ochimikan.records.Score
import java.util.Date
import spray.json.{
  RootJsonFormat,
  RootJsonReader,
  RootJsonWriter,
  JsNumber,
  JsObject,
  JsString,
  JsValue
}

/**
 * Provides the conversion between a [[Score]] and a JSON object.
 *
 * A JSON object a [[Score]] is similar to the following,
 * {{{
 * {
 *   "value":  300,
 *   "level":  2,
 *   "player": "player",
 *   "date":   1415549555
 * }
 * }}}
 * `value`, `level` and `player` corresponds to those of the [[Score]].
 * `date` represents that of the [[Score]] as the number of seconds since
 * January 1, 1970, 00:00:00 GMT.
 */
trait ScoreJson {
  /** Converts a [[Score]] into a JSON object. */
  trait ScoreJsonWriter extends RootJsonWriter[Score] {
    override def write(score: Score): JsValue =
      JsObject(
        "value"  -> JsNumber(score.value),
        "level"  -> JsNumber(score.level),
        "player" -> JsString(score.player),
        "date"   -> JsNumber(score.date.getTime() / 1000)
      )
  }

  /** Converts a JSON object into a [[Score]]. */
  trait ScoreJsonReader extends RootJsonReader[Score] {
    override def read(json: JsValue): Score =
      json.asJsObject.getFields("value", "level", "player", "date") match {
        case Seq(JsNumber(value), JsNumber(level), JsString(player), JsNumber(dateNum)) =>
          Score(
            value  = value.toInt,
            level  = level.toInt,
            player = player,
            date   = new Date(dateNum.toLong * 1000)
          )
      }
  }

  /** Implicitly converts a [[Score]] into a JSON object. */
  implicit object `Score->JSON` extends RootJsonFormat[Score]
                                with ScoreJsonWriter
                                with ScoreJsonReader
}

/** Companion object of [[ScoreJson]]. */
object ScoreJson extends ScoreJson
