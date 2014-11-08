package com.herokuapp.ochimikan.json

import com.nimbusds.jose.JWSObject
import spray.json.{
  JsObject,
  JsString,
  JsValue,
  RootJsonWriter
}

/**
 * Converts a JWSObject into a JSON object.
 *
 * A JSON object has the following structure,
 * {{{
 * {
 *   "token": "The Serialized JWS"
 * }
 * }}}
 */
trait JwtJson {
  /** Implicit converter from a JWSObject to a JSON object. */
  implicit object `JWSObject->JSON` extends RootJsonWriter[JWSObject] {
    def write(jws: JWSObject): JsValue =
      JsObject("token" -> JsString(jws.serialize()))
  }
}

/** Companion object of [[JwtJson]]. */
object JwtJson extends JwtJson
