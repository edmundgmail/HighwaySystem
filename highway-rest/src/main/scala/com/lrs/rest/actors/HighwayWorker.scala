package com.lrs.rest.routes.actors

import akka.actor.{Actor, ActorLogging, Props, Stash}
import akka.pattern.pipe
import com.lrs.rest.routes.actors.HighwayWorker.GetHighway
import org.mongodb.scala.MongoClient
import org.mongodb.scala.model.Projections._

import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by vagrant on 9/11/17.
  */

object HighwayWorker {

  def props(): Props = Props(new HighwayWorker)

  case class GetHighway()
  case class GetHighwayDetails(name: String, id: String)

  // To directly connect to the default server localhost on port 27017

  // Use a Connection String

}

class HighwayWorker extends Actor with ActorLogging with Stash{
  implicit val system = context.system

  val mongoClient = MongoClient("mongodb://localhost")
  val database = mongoClient.getDatabase("road")
  val roadTable = database.getCollection("Road")

  def receive = {

    case GetHighway =>
      getAllHighways pipeTo sender()

  }


  private def getAllHighways = {
    roadTable.find().projection(fields(include("roadName","roadId"), excludeId())).map(_.toJson).toFuture()
  }

  private def getHighwayDetails() = {

  }
}
