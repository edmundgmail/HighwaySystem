package com.lrs.streaming

import java.io.File
import java.nio.file.{Files, Paths}
import java.util.concurrent.Executors

import com.google.gson.GsonBuilder
import com.lrs.common.ConfigFields
import com.lrs.common.logging.Logging
import com.lrs.common.models.{DataRecord, DataRecordDeserializer, Road}
import com.lrs.common.utils.MongoUtils
import com.lrs.streaming.processor.RoadProcessor
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext
import scala.util.Try

/**
  * Created by vagrant on 6/21/17.
  */
object Driver extends Logging{

  private def createSparkContext(sparkConfig: SparkConf): SparkContext = new SparkContext(sparkConfig)

  private def readConfiguration(args: Array[String]): Config = Try(args(0)).toOption match {
    case Some(x) => ConfigFactory.parseFile(new File(x)).resolve()
    case None => ConfigFactory.load()
  }

  private def getConfigAsMap(config: Config, root: String) = config.getConfig(root).entrySet().asScala.map(x =>
    (x.getKey, x.getValue.unwrapped().toString))


  def main(args: Array[String]) : Unit = {
    val config = readConfiguration(args)
    val appConfig = getConfigAsMap(config, ConfigFields.APP_CONFIG)

    val sparkConfig = new SparkConf().setAll(appConfig)
    val sc = createSparkContext(sparkConfig)

    // Get the application configuration
    // read JSON file data as String
    val filePath = config.getString(ConfigFields.STAGING_DIR)
    val fileData = new String(Files.readAllBytes(Paths.get(filePath)))

    val gsonBuilder = new GsonBuilder
    gsonBuilder.registerTypeAdapter(classOf[DataRecord], DataRecordDeserializer.getInstance)

    val gson = gsonBuilder.create()
    // parse json string to object
    val records = gson.fromJson(fileData, classOf[Array[DataRecord]])

    implicit val ec = ExecutionContext.fromExecutor(null)

    val records1 = MongoUtils.getHighwayRecords(1)

    records1 onSuccess {
      case rs => {
        var road : Road = null
        val rss = rs.map(r=>gson.fromJson(r.toString, classOf[DataRecord]))
        for(record<-rss){
          road = RoadProcessor.process(sc, road, record)
        }
        logger.info(road.toString)
      }
    }

    records1 onFailure {
      case t=>
        logger.info("" + t.getMessage)
    }
  }
}
