package com.herokuapp.ochimikan.directives

import akka.event.Logging.LogLevel
import scala.collection.LinearSeq
import scala.language.implicitConversions
import spray.http.{
  HttpHeader,
  HttpRequest,
  HttpResponse,
  HttpResponsePart
}
import spray.routing.Directive0
import spray.routing.directives.{
  DebuggingDirectives,
  LogEntry
}
import spray.util.LoggingContext
import spray.util.pimps.PimpedLinearSeq

/** Provides a directive `logAccess` which records access logs. */
trait LoggingDirectives {
  /**
   * Records access logs.
   *
   * Takes arguments equivalent to the following,
   * {{{
   * logAccess(level: LogLevel)(implicit log: LoggingContext): Directive0
   * }}}
   */
  def logAccess(magnet: AccessLoggingMagnet): Directive0 =
    magnet.directive
}

/** Companion object of [[LoggingDirectives]]. */
object LoggingDirectives extends LoggingDirectives

/**
 * A magnet which attracts arguments for `logAccess`.
 *
 * @param directive
 *     The logging directive to run.
 */
case class AccessLoggingMagnet(val directive: Directive0)

/** Companion object of [[AccessLoggingMagnet]]. */
object AccessLoggingMagnet {
  import DebuggingDirectives._

  /**
   * Implicitly converts a log level into an [[AccessLoggingMagnet]].
   *
   * Access will be logged in the format similar to the following Apache log
   * format,
   * {{{
   * "\"%r\" %>s %b \"%{Referer}i\" \"%{User-agent}i\"" combined
   * }}}
   *
   * @param level
   *     The log level.
   * @param log
   *     The logger depending on the context.
   */
  implicit def fromLogLevel(level: LogLevel)(implicit log: LoggingContext): AccessLoggingMagnet =
    AccessLoggingMagnet(logRequestResponse(defaultFormatter(level)))

  /**
   * Returns a default log formatter of a specified log level.
   *
   * @param level
   *     The log level.
   */
  def defaultFormatter(level: LogLevel): HttpRequest => HttpResponsePart => Option[LogEntry] =
    request => {
      case response: HttpResponse =>
        val msg = s"""${request.method.value} ${request.uri.toRelative} ${request.protocol.value} ${response.status.intValue} - "${request.`Referer`}" "${request.`User-agent`}""""
        Some(new LogEntry(msg, level))
      case _ =>
        None
    }

  /** An augmented request headers with facilities to access common headers. */
  class RequestHeaders(headers: LinearSeq[HttpHeader]) extends PimpedLinearSeq(headers) {
    /** "Referer" header value. Empty if not specified. */
    def `Referer`: String = mapFind(valueIfMatch(_, "referer")).getOrElse("")

    /** "User-agent" header value. Empty if not specified. */
    def `User-agent`: String =
      mapFind(valueIfMatch(_, "user-agent")).getOrElse("")

    // returns the value if a specified header has a specified name.
    // NOTE: name must be specified in lowercase
    def valueIfMatch(header: HttpHeader, name: String): Option[String] =
      if (header.is(name)) Some(header.value) else None
  }

  /** Implicitly converts an `HttpRequest` into a [[RequestHeaders]]. */
  implicit def toRequestHeaders(request: HttpRequest): RequestHeaders =
    new RequestHeaders(request.headers)
}
