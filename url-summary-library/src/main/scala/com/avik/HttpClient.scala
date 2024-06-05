package com.avik

import sttp.client3.*
import sttp.client3.circe.*
import com.avik.model.SummaryResponse

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import io.circe.generic.auto.*
import sttp.client3.asynchttpclient.future.AsyncHttpClientFutureBackend

object HttpClient {
  private val backend = AsyncHttpClientFutureBackend()

  def getSummary(url: String, fastApiUrl: String): Future[SummaryResponse] = {
    val request = basicRequest
      .get(uri"$fastApiUrl?url=$url")
      .response(asJson[SummaryResponse])

    request.send(backend).map(_.body).flatMap {
      case Right(response) => Future.successful(response)
      case Left(error) => Future.failed(new Exception(error.toString))
    }
  }
}
