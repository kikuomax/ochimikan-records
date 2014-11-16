package com.herokuapp.ochimikan.records.service

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.herokuapp.ochimikan.records.Query
import com.herokuapp.ochimikan.records.mongo
import com.mongodb.casbah.MongoClient
import com.typesafe.config.ConfigFactory
import scala.concurrent.{
  ExecutionContext,
  Future
}
import scala.concurrent.duration._
import spray.can.Http

object Boot extends App {
  // loads the configuration
  private val settings = new Settings(ConfigFactory.load())

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("ochimikan-records")

  // the query object for database components
  private val query: Query = new Query {
    val client = MongoClient(settings.mongoUri)

    def resolveDatabase(implicit executionContext:ExecutionContext) =
      Future { new mongo.Database(client) }
  }

  // create and start our service actor
  val service = system.actorOf(Props(new ServiceActor(settings, query)),
    "ochimikan-records")

  implicit val timeout = Timeout(5.seconds)
  // start a new HTTP server on a specified port of a specified host
  IO(Http) ? Http.Bind(service,
    interface = settings.hostName, port = settings.hostPort)
}
