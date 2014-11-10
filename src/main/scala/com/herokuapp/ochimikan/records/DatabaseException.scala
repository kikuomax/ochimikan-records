package com.herokuapp.ochimikan.records

/**
 * The exception which represents a database related error.
 *
 * @constructor
 * @param message
 *     The brief explanation of the exception.
 * @param cause
 *     The root cause of the exception. May be `null`.
 */
class DatabaseException(message: String, cause: Throwable)
  extends Exception(message, cause)

/** Companion object of [[DatabaseException]]. */
object DatabaseException {
  /**
   * Constructs a [[DatabaseException]].
   *
   * @param message
   *     The brief explanation of the exception.
   */
  def apply(message: String): DatabaseException = this(message, null)

  /**
   * Constructs a [[DatabaseException]].
   *
   * @param message
   *     The brief explanation of the exception.
   * @param cause
   *     The root cause of the exception. May be `null`.
   */
  def apply(message: String, cause: Throwable): DatabaseException =
    new DatabaseException(message, cause)
}
