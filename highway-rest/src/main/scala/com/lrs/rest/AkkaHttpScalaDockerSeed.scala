package com.lrs.rest

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.lrs.rest.actors.HighwayWorker
import com.lrs.rest.models.errors.{ExternalResourceException, ExternalResourceNotFoundException}
import com.lrs.rest.routes.{HighwayRoutes, MonitoringRoutes}
import com.typesafe.config.ConfigFactory

object AkkaHttpScalaDockerSeed extends App {

  implicit val system = ActorSystem("main-actor-system")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val conf = ConfigFactory.load()
  // actors
  //val queueConnector = system.actorOf(QueueConnector.props(queueName), "queue-connector")
  //val stockPriceConnector = system.actorOf(StockPriceConnector.props(apiKey), "stock-price-connector")
  //val stockPriceWorker = system.actorOf(MessageWorker.props(queueConnector, messageToSend), "message-worker")

  // route definitions
  //val queueRoutes = new QueueRoutes(queueConnector, stockPriceConnector)
  val roadWorker = system.actorOf(HighwayWorker.props)

  val highwayRoutes = new HighwayRoutes(roadWorker)
  val monitoringRoutes = new MonitoringRoutes()
  // implicit exception handler - this will be picked up by the route definitions
  implicit def myExceptionHandler: ExceptionHandler = customGlobalErrorHandler

  // merge all routes here
  def allRoutes = {
    highwayRoutes.routes ~
    monitoringRoutes.routes
  }

  Http().bindAndHandle(allRoutes, "0.0.0.0", 5000)


  // When handling and completing errors as results like this
  // you have the option (see bellow) of not logging errors or log with a lower level when
  // failures are caused by the user (not the system itself)
  def customGlobalErrorHandler = ExceptionHandler {
    case ex: ExternalResourceNotFoundException =>
      extractUri { uri =>
        // no errors will be logged here
        complete(HttpResponse(NotFound, entity = ex.message))
      }
    case ex: ExternalResourceException => {
      // a WARN will be logged instead of an error
      system.log.warning(ex.message)
      complete(HttpResponse(BadGateway, entity = ex.message))
    }

    // This behaves as a pipeline; errors that are not handled above will be passed on (bubble up)
    // up to the route.
    // The default behaviour is to log the error and return a InternalServerError and no body
  }


}
