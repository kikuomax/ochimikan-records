package com.herokuapp.ochimikan.records

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
import spray.routing._
import spray.routing.authentication.{
  BasicAuth,
  UserPass
}
import spray.http._
import MediaTypes._
import spray.httpx.SprayJsonSupport
import spray.util.LoggingContext

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class ServiceActor extends Actor with Service {

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
 *  2. The `OchiMikan Records` queries a `Score DB` for a list of high scores.
 *  3. The `OchiMikan Records` returns the list of high scores to the user.
 *
 * ===Registering a score===
 *
 *  1. A user POSTs his/her score to an `OchiMikan Records`.
 *  2. The `OchiMikan Records` verifies the user's privilege for posting a
 *     score.
 *  3. The `OchiMikan Records` inserts the score into a `Score DB`.
 *
 * ==RESTful API==
 *
 * The following paths are available,
 * {{{
 *     /record
 *       GET   Returns a list of records.
 *       POST  Records a specified score.
 *
 *     /authenticate
 *       GET   Does authentication and returns an authorized token.
 *             Authentication is done by Basic authentication.
 * }}}
 */
trait Service extends HttpService with SprayJsonSupport with JwtDirectives with LoggingDirectives with JwtJson {

  // imports functions and conversions for JwtClaimBuilder and JwtClaimVerifier
  import JwtClaimBuilder._
  import JwtClaimVerifier._

  /** The implicit logger. */
  val log: LoggingContext

  /** The implicit execution context of this service. */
  implicit val executionContext: ExecutionContext

  // imports implicit signer and verifier in the scope
  private val jwtSignature = JwtSignature(JWSAlgorithm.HS256, "60f6244c747525041003566630a49fe5d3ae5a90c7c59194c5016164d32346504017a7f58e5ff9abc37ba561b3c3bfd1b0f6b4323b5b4e9bef8285b105decff7")
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
          get {
            complete {
<html>
  <body>
    Records
  </body>
</html>
            }
          } ~
          post {
            def canRegisterScore(claim: JSONObject) =
              Option(claim.get("sub")) flatMap {
                case user: String => Some(user)
                case _            => None
              }

            authorizeToken(verifyNotExpired && canRegisterScore) { user =>
              complete {
<html>
  <body>
    {user}'s record updated.
  </body>
</html>
              }
            }
          }
        }
      }
    }
}
