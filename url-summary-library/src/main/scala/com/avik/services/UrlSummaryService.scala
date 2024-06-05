package com.avik.services

import com.avik.HttpClient
import com.avik.Database
import scala.concurrent.{ExecutionContext, Future}
import com.typesafe.config.ConfigFactory

object UrlSummaryService {
  private val config = ConfigFactory.load()
  private val fastApiUrl = config.getString("fastapi.url")

  def getUrlSummary(url: String)(implicit ec: ExecutionContext): Future[String] = {
    for {
      summaryResponse <- HttpClient.getSummary(url, fastApiUrl)
      _ <- Database.saveResponseHistory(url, summaryResponse.summary)
    } yield summaryResponse.summary
  }
}
