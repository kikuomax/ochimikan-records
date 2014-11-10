package com.herokuapp.ochimikan
package records

import scala.concurrent.{
  ExecutionContext,
  Future
}

/** The interface of a queryable object. */
trait Queryable[T] {
    /**
     * Requests the object associated with this queryable object.
     *
     * A resulting `Future` fails with [[DatabaseException]] if some error has
     * occurred during accessing the database.
     *
     * @param executionContext
     *     The `ExecutionContext` in which the request is to be performed.
     * @return
     *     A `Future` to access a resolved object.
     */
    def request(implicit executionContext: ExecutionContext): Future[T]

    /** An alias to [[#request]]. */
    def ?(implicit executionContext: ExecutionContext) = request
}

/** The interface to query for database components. */
trait Query {
  /**
   * Resolves a database.
   *
   * A resulting `Future` fails with [[DatabaseException]] if some error has
   * occurred during resolving the database.
   *
   * @param executionContext
   *     The `ExecutionContext` in which the request is to be performed.
   * @return
   *     A `Future` to access a resolved database.
   */
  def resolveDatabase(implicit executionContext: ExecutionContext):
    Future[records.Database]

  /** A query for a database. */
  case class Database() extends Queryable[records.Database] {
    /** Resolves this database. */
    override def request(implicit executionContext: ExecutionContext):
      Future[records.Database] = resolveDatabase

    /** Returns a query for the [[ScoreList]] in this database. */
    def scores: ScoreList = ScoreList(this)
  }

  /** A query for a [[ScoreList]]. */
  case class ScoreList(database: Database)
    extends Queryable[records.ScoreList]
  {
    /** Resolves this score list. */
    override def request(implicit executionContext: ExecutionContext) =
      database.? map { _.scores }
  }
}
