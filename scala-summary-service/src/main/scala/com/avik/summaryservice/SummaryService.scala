package com.avik.summaryservice

import sttp.client3._
import sttp.client3.asynchttpclient.future.AsyncHttpClientFutureBackend
import sttp.model.StatusCode
import io.circe.parser._
import scala.concurrent.{Future, ExecutionContext}
import scala.util.{Failure, Success}
import org.slf4j.LoggerFactory
import scala.compat.java8.FutureConverters._
import java.util.concurrent.CompletableFuture

object SummaryService {
  private val logger = LoggerFactory.getLogger(this.getClass)
  implicit val backend: SttpBackend[Future, Any] = AsyncHttpClientFutureBackend()

//  def getSummary(url: String)(implicit ec: ExecutionContext): Future[String] = {
//    val request = basicRequest.get(uri"${Config.Api.fastApiUrl}/summary?url=$url")
//
//    request.send(backend).flatMap { response =>
//      response.body match {
//        case Right(body) =>
//          decode[Map[String, String]](body) match {
//            case Right(json) => Future.successful(json("summary"))
//            case Left(error) =>
//              logger.error("Error decoding JSON response", error)
//              Future.failed(new Exception(error))
//          }
//        case Left(error) =>
//          logger.error("Error in HTTP response", new Exception(error))
//          Future.failed(new Exception(error))
//      }
//    }.recoverWith {
//      case ex: Exception =>
//        logger.error("Error fetching summary", ex)
//        Future.failed(ex)
//    }
//  }

  def getSummary(url: String)(implicit ec: ExecutionContext): Future[String] = {
    // Create a GET request to the FastAPI service
    val request = basicRequest.get(uri"${Config.Api.fastApiUrl}/summary?url=$url")

    // Send the request and handle the response
    request.send(backend).flatMap { response =>

      // Check the HTTP response status
      if (response.code.isSuccess) {
        // If the status is a success, handle the body of the response
        response.body match {
          case Right(body) =>
            // Try to decode the body from JSON to a Map
            decode[Map[String, String]](body) match {
              case Right(json) =>
                // If the decoding is successful, return the summary
                Future.successful(json("summary"))
              case Left(error) =>
                // If there's an error decoding the JSON, log the error and return a failed Future
                logger.error("Error decoding JSON response", error)
                Future.failed(new Exception(error))
            }
          case Left(error) =>
            // If there's an error in the HTTP response, log the error and return a failed Future
            println(s"*****error in HTTP response:  $error")
            logger.error("Error in HTTP response", new Exception(error))
            Future.failed(new Exception(error))
        }
      } else {
        // If the HTTP status is an error, check if the status code is 404
        if (response.code == StatusCode.NotFound) {
          // If the status code is 404, return a personalized message
          Future.successful("The requested resource could not be found.")
        } else {
          // If the status code is not 404, log the status and return a failed Future
          logger.error("HTTP error status", new Exception(response.statusText))
          Future.failed(new Exception(response.statusText))
        }
      }
    }.recoverWith {
      // Handle any exceptions that occur during the Future computation
      case ex: Exception =>
        logger.error("Error fetching summary", ex)
        Future.failed(ex)
    }
  }


  def fetchAndSaveSummary(url: String)(implicit ec: ExecutionContext): CompletableFuture[String] = {
    val scalaFuture = (for {
      _ <- Database.initSchema
      summary <- getSummary(url)
      _ <- Database.saveSummary(Summary(url, summary))
    } yield summary).recoverWith {
      case ex: Exception =>
        logger.error("Error in fetchAndSaveSummary process", ex)
        Future.failed(ex)
    }
    scalaFuture.toJava.toCompletableFuture
  }

  def shutdown(): Unit = {
    backend.close()
    Database.close()
    logger.info("HTTP client backend closed.")
  }
}
