package com.avik.summaryservice

import io.circe.parser.*
import org.slf4j.LoggerFactory
import sttp.client3.*
import sttp.client3.asynchttpclient.future.AsyncHttpClientFutureBackend
import sttp.model.StatusCode

import java.util.List as JList
import java.util.concurrent.CompletableFuture
import scala.compat.java8.FutureConverters.*
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.*

object SummaryService {
  implicit val backend: SttpBackend[Future, Any] = AsyncHttpClientFutureBackend()
  private val logger = LoggerFactory.getLogger(this.getClass)

  def getSummary(url: String)(implicit ec: ExecutionContext): Future[String] = {
    logger.info(s"Getting summary for URL: $url")
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
                logger.info(s"Successfully decoded JSON for URL: $url")
                // If the decoding is successful, return the summary
                Future.successful(json("summary"))
              case Left(error) =>
                logger.error(s"Error decoding JSON for URL: $url", error)
                // If there's an error decoding the JSON, return a failed Future
                Future.failed(new Exception(error))
            }
          case Left(error) =>
            logger.error(s"Error in HTTP response for URL: $url", error)
            // If there's an error in the HTTP response, return a failed Future
            Future.failed(new Exception(error))
        }
      } else {
        // If the HTTP status is an error, check if the status code is 404
        if (response.code == StatusCode.NotFound) {
          logger.warn(s"URL not found: $url")
          // If the status code is 404, return a personalized message
          Future.failed(new Exception("No URL found, 404"))
        } else {
          logger.error(s"HTTP error for URL: $url", response.statusText)
          // If the status code is not 404, return a failed Future
          Future.failed(new Exception(response.statusText))
        }
      }
    }.recoverWith {
      // Handle any exceptions that occur during the Future computation
      case ex: Exception =>
        logger.error(s"Exception occurred while getting summary for URL: $url", ex)
        Future.failed(ex)
    }
  }

  def fetchAndSaveSummary(url: String, username: String)(implicit ec: ExecutionContext)
  : CompletableFuture[Summary] = {
    logger.info(s"Fetching summaries by username: $username")
    val scalaFuture = (for {
      _ <- Database.initSchema
      summary <- getSummary(url)
      summaryObj = Summary(url, username, summary)
      _ <- Database.saveSummary(summaryObj)
    } yield summaryObj).recoverWith {
      case ex: Exception =>
        logger.error(s"Exception occurred while fetching and saving summary for URL: $url and username: $username", ex)
        Future.failed(ex)
    }
    scalaFuture.toJava.toCompletableFuture
  }

  def fetchSummariesByUsername(username: String)(implicit ec: ExecutionContext):
  CompletableFuture[JList[Summary]] = {
    logger.info(s"Fetching summaries by username: $username")
    // _.toList.asJava is equivalent to (summaries: Seq[Summary]) => summaries.toList.asJava
    val scalaFuture =
      Database.fetchSummariesByUsername(username).map(_.toList.asJava).recoverWith {
      case ex: Exception =>
        logger.error(s"Exception occurred while fetching summaries by username: $username", ex)
        Future.failed(ex)
    }
    scalaFuture.toJava.toCompletableFuture
  }

}
