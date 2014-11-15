package com.herokuapp.ochimikan.records.service

import akka.actor.Actor
import akka.event.Logging
import com.herokuapp.ochimikan.directives.{
  LoggingDirectives,
  JwtClaimBuilder,
  JwtClaimVerifier,
  JwtDirectives,
  JwtSignature
}
import com.herokuapp.ochimikan.json.JwtJson
import com.herokuapp.ochimikan.records.{
  Query,
  Score,
  ScoreList
}
import com.herokuapp.ochimikan.records.json.{
  ScoreJson,
  ScoreListJson
}
import com.nimbusds.jose.{
  JWSAlgorithm,
  JWSObject
}
import net.minidev.json.JSONObject
import scala.concurrent.{
  ExecutionContext,
  Future
}
import scala.concurrent.duration.DurationInt
import scala.language.implicitConversions
import spray.http._
import MediaTypes._
import spray.httpx.SprayJsonSupport
import spray.json.CollectionFormats
import spray.routing._
import spray.routing.authentication.{
  BasicAuth,
  UserPass
}
import spray.util.LoggingContext

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
/**
 * An `Actor` which provides [[Service]].
 *
 * @constructor
 * @param settings
 *     The settings of the service.
 * @param query
 *     The query object for database components.
 */
class ServiceActor(override val settings: Settings, override val query: Query)
  extends Actor with Service
{
  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // supplies the implicit logger of this actor
  override lazy val log = implicitly[LoggingContext]

  // supplies the default dispatcher as the implicit execution context
  override implicit lazy val executionContext = context.dispatcher

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(route)
}

/**
 * Provides the RESTful API to retrieve and store OchiMikan score records.
 *
 * ==Use Cases==
 *
 * ===Listing high scores===
 *
 *  1. A user GETs a list of high scores from an `OchiMikan Records`.
 *  1. The `OchiMikan Records` queries a `Score DB` for a list of high scores.
 *  1. The `OchiMikan Records` returns the list of high scores to the user.
 *
 * ===Registering a score===
 *
 *  1. A user POSTs his/her score to an `OchiMikan Records`.
 *  1. The `OchiMikan Records` verifies the user's privilege for posting a
 *     score.
 *  1. The `OchiMikan Records` inserts the score into a `Score DB`.
 *
 * ==RESTful API==
 *
 * ===GET /record===
 *
 * Returns a list of scores.
 *
 * ====Response====
 *
 * A JSON object which contains an array of scores.
 * It will be similar to the following,
 * {{{
 * {
 *   "scores": [
 *     {
 *       "value":  300,
 *       "level":  2,
 *       "player": "player",
 *       "date":   1415549555
 *     },
 *     ...
 *   ]
 * }
 * }}}
 * `scores` is an array of `score object`s.
 * They are sorted in descending order.
 *
 * A `score object` has properties `value`, `level`, `player` and `date`.
 *
 * `value` is the value of the score. An integer >= 0.
 *
 * `level` is the level at which the score was achieved. An integer >= 1.
 *
 * `player` is the name of the player who achieved the score. A string.
 *
 * `date` is the date when the score was achieved.
 * The number of seconds since January 1, 1970, 00:00:00 GMT. 
 *
 * ===POST /record===
 *
 * Records a specified score.
 *
 * ====Entity====
 *
 * A JSON object which represents the score to be recorded.
 * It will be similar to the following,
 * {{{
 * {
 *   "value":  300,
 *   "level":  2,
 *   "player": "player",
 *   "date":   1415549555
 * }
 * }}}
 * `value`, `level`, `player` and `date` corresponds to those of `GET /record`.
 *
 * ===GET /authenticate===
 *
 * Does Basic authentication and returns an authorized token.
 *
 * ====Response====
 *
 * A JSON object which contains a signed JSON Web Token (JWT).
 * It will be similar to the following,
 * {{{
 * {
 *   "token": "1234ABC..."
 * }
 * }}}
 * `token` is a signed JWT. A string.
 *
 */
trait Service extends HttpService with SprayJsonSupport with CollectionFormats with JwtDirectives with LoggingDirectives with JwtJson with ScoreJson with ScoreListJson {
  // imports functions and conversions for JwtClaimBuilder and JwtClaimVerifier
  import JwtClaimBuilder._
  import JwtClaimVerifier._

  /** The implicit logger. */
  val log: LoggingContext

  /** The implicit execution context of this service. */
  implicit val executionContext: ExecutionContext

  /** The settings of the service. */
  val settings: Settings

  /** The query object for the database contents. */
  val query: Query

  // imports implicit signer and verifier in the scope
  private val jwtSignature =
    JwtSignature(JWSAlgorithm.HS256, settings.secretKey)
  import jwtSignature._

  // valid duration of a JWT
  private val tokenDuration = 5.minutes

  // the implicit JWT claim builder
  private implicit val claimBuilder =
    claimSubject[UserPass](_.user) ~>
    claimIssuer("OchiMikan Records") ~>
    claimExpiration(tokenDuration)

  val route =
    logAccess(Logging.InfoLevel) {
      handleRejections(RejectionHandler.Default) {
        path("") {
          get {
            respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
              complete {
<html>
  <body>
    <h1>Say hello to <i>spray-routing</i> on <i>spray-can</i>!</h1>
  </body>
</html>
              }
            }
          }
        } ~
        path("authenticate") {
          // an experimental authenticator
          def authenticator(userpass: Option[UserPass]) =
            Future {
              userpass flatMap {
                case up if up.user == "guest" && up.pass == "mikan" =>
                  log.info(s"event=login attempt\tuser=${up.user}\tresult=success")
                  Some(up)
                case up =>
                  log.info(s"event=login attempt\tuser=${up.user}\tresult=failure")
                  None
              }
            }

          authenticate(BasicAuth(jwtAuthenticator(authenticator _), "OchiMikan Records")) {
            complete(_)
          }
        } ~
        path("record") {
          val dbQuery = query.Database()
          get {
            onSuccess(dbQuery.scores.?) {
              complete(_)
            }
          } ~
          post {
            def canRegisterScore(claim: JSONObject) =
              Option(claim.get("sub")) flatMap {
                case user: String => Some(user)
                case _            => None
              }

            authorizeToken(verifyNotExpired && canRegisterScore) { user =>
              entity(as[Score]) { score =>
                onSuccess(dbQuery.?) { db =>
                  db.addScore(score)
                  complete(db.scores)
                }
              }
            }
          }
        }
      }
    }
}
