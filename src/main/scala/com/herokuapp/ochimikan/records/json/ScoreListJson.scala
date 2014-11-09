package com.herokuapp.ochimikan.records.json

import com.herokuapp.ochimikan.records.ScoreList
import spray.json.{
  DefaultJsonProtocol,
  JsObject,
  JsValue,
  pimpAny,
  RootJsonWriter
}

/**
 * Provides the conversion from a [[ScoreList]] to a JSON object.
 *
 * A JSON object is similar to the following,
 * {{{
 * {
 *   "scores": [
 *     ...
 *   ]
 * }
 * }}}
 * `score` is an array of [[Score]]s.
 * Each [[Score]] is formatted as described by [[ScoreJson]].
 */
trait ScoreListJson {
  import DefaultJsonProtocol._
  import ScoreJson._

  /** Converts a [[ScoreList]] into a JSON object. */
  trait ScoreListJsonWriter extends RootJsonWriter[ScoreList] {
    override def write(scores: ScoreList): JsValue =
      JsObject("scores" -> scores.scores.toJson)
  }

  /** Implicit converter of [[ScoreList]]. */
  implicit object `ScoreList->JSON` extends ScoreListJsonWriter
}
