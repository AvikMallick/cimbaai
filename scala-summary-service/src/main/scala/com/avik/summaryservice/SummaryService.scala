package com.avik.summaryservice

import sttp.client3._
import sttp.client3.asynchttpclient.future.AsyncHttpClientFutureBackend
import io.circe.parser._
import scala.concurrent.{Future, ExecutionContext}
import scala.util.{Failure, Success}
import org.slf4j.LoggerFactory
import scala.compat.java8.FutureConverters._
import java.util.concurrent.CompletableFuture

object SummaryService {
  private val logger = LoggerFactory.getLogger(this.getClass)
  implicit val backend: SttpBackend[Future, Any] = AsyncHttpClientFutureBackend()

  def getSummary(url: String)(implicit ec: ExecutionContext): Future[String] = {
    val request = basicRequest.get(uri"${Config.Api.fastApiUrl}/summary?url=$url")

    request.send().flatMap { response =>
      response.body match {
        case Right(body) =>
          decode[Map[String, String]](body) match {
            case Right(json) => Future.successful(json("summary"))
            case Left(error) =>
              logger.error("Error decoding JSON response", error)
              Future.failed(new Exception(error))
          }
        case Left(error) =>
          logger.error("Error in HTTP response", new Exception(error))
          Future.failed(new Exception(error))
      }
    }.recoverWith {
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
