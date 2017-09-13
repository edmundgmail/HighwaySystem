import sbt.Keys._
import NativePackagerHelper._
import com.typesafe.sbt.packager.MappingsHelper.{contentOf => _}
import sbtassembly.AssemblyPlugin.autoImport._
import scala.collection.JavaConverters._

import scala.util.Try

val meta = """META.INF(.)*""".r


/**
  * Dependencies
  */
val akkaV = "2.5.4"
val akkaHttpV = "10.0.10"

val jacksonCore           = "com.fasterxml.jackson.core"      %     "jackson-core"                  %  "2.7.5"
val jacksonDatabind       = "com.fasterxml.jackson.core"      %     "jackson-databind"              %  "2.7.5"
val jacksonScala          = "com.fasterxml.jackson.module"    %%    "jackson-module-scala"          %  "2.7.5"
val jodaTime              = "com.github.nscala-time"          %%    "nscala-time"                   %  "2.12.0"
val log4j                 = "log4j"                           %     "log4j"                         %  "1.2.17"
val mockito               = "org.mockito"                     %     "mockito-all"                   %  "1.10.19" % "test"
val scalaTest             = "org.scalatest"                   %%    "scalatest"                     %  "2.2.6"   % "test"
val scalaMock             = "org.scalamock"                   %%    "scalamock-scalatest-support"   %  "3.2.2"   % "test"
val sparkCore             = "org.apache.spark"                %%    "spark-core"                    %  "2.0.2"   //% "provided"
val sparkStreaming        = "org.apache.spark"                %%    "spark-streaming"               %  "2.0.2"   //% "provided"
val typesafeConfig        = "com.typesafe"                    %     "config"                        %  "1.3.0"
val gson                  = "com.google.code.gson"            %     "gson"                          % "2.8.1"
val akkaActor   		  = "com.typesafe.akka" 			  %% "akka-actor" 						% akkaV
val akkaSlf4j   		  = "com.typesafe.akka"		   		  %% "akka-slf4j" 						% akkaV
val akkaStream     		  = "com.typesafe.akka" 			  %% "akka-stream" 						% akkaV
val akkaHttp    		  = "com.typesafe.akka" 			  %% "akka-http" 			% akkaHttpV
val akkaHttpTest    	  = "com.typesafe.akka" 			  %% "akka-http-testkit" 				% akkaHttpV  % "test"
val akkaHttpSprayJson    		  = "com.typesafe.akka" 			  %% "akka-http-spray-json" 			% akkaHttpV
val json4s 				  = "org.json4s" 					  %% "json4s-jackson" 					% "3.3.0"
val logBack  			  = "ch.qos.logback" 				  % "logback-classic" 					% "1.1.7"
val amazonAws 			  = "com.amazonaws" 				  % "aws-java-sdk-sqs" 					% "1.11.9"
val mongoDB 			  = "org.mongodb.scala" 			  % "mongo-scala-driver_2.11" 		    % "2.1.0"

/**
  * Shared settings for all the projects
  */
lazy val commonSettings = Seq(
  version := s"0.0.1-SNAPSHOT${Try("_" + sys.env("BUILD_NUMBER")).getOrElse("")}",
  organization := "com.lrs",
  scalaVersion := "2.11.6",
  coverageMinimum := 80,
  coverageFailOnMinimum := false,
  parallelExecution in Test := false,
  fork in Test := true,
  baseDirectory in Test := file("."),
  javaOptions in Test += "-Xms1g",
  javaOptions in Test += "-Xmx2g",
  javaOptions in Test += "-XX:MaxMetaspaceSize=1g"
)

/**
  * This only holds Universal package
  */
lazy val HighwaySystem = (project in file(".")).aggregate(common, streaming).
  enablePlugins(UniversalPlugin).
  settings(commonSettings: _*).
  settings(
    mappings in Universal ++= contentOf("highway-configs/"),
    mappings in Universal ++= contentOf("highway-scripts/"),
    mappings in Universal ++= contentOf("highway-streaming/target/scala-2.11/"),
    mappings in Universal := (mappings in Universal).value filter {
      case (file, name) => !name.contains("classes")
    }
  )

/**
  * Common project which contains shared code among the other projects
  */
lazy val common = (project in file("highway-common")).
  settings(commonSettings: _*).
  settings(
    unmanagedBase := baseDirectory.value / "lib",
    libraryDependencies ++= Seq(gson, jacksonCore, jacksonDatabind, jacksonScala, jodaTime, typesafeConfig, logBack,
      scalaTest, scalaMock, mockito)
  )

/**
  * Streaming project which is in charge of manageing the spark streaming
  */
lazy val streaming = (project in file("highway-streaming")).
  settings(commonSettings: _*).
  settings(

    libraryDependencies ++= Seq(sparkCore, sparkStreaming, scalaTest, scalaMock),
    mainClass in Compile := Some("com.lrs.streaming.Driver"),
    mainClass in run := Some("com.lrs.streaming.Driver"),
    assemblyMergeStrategy in assembly := {
      case meta(_) => MergeStrategy.discard
      case x => MergeStrategy.first
    }
  ).dependsOn(common % "compile->compile;test->test")


/**
  * Streaming project which is in charge of manageing the spark streaming
  */
lazy val rest = (project in file("highway-rest")).
  settings(commonSettings: _*).
  settings(

    libraryDependencies ++= Seq( json4s,akkaActor, akkaHttp,akkaHttpTest, akkaHttpSprayJson, akkaStream,akkaSlf4j, mongoDB, amazonAws, scalaTest, scalaMock),
    mainClass in Compile := Some("com.lrs.rest.AkkaHttpScalaDockerSeed"),
    mainClass in run := Some("com.lrs.rest.AkkaHttpScalaDockerSeed"),
    assemblyMergeStrategy in assembly := {
      case meta(_) => MergeStrategy.discard
      case x => MergeStrategy.first
    }
  ).dependsOn(common % "compile->compile;test->test")
