package com.avik

import com.avik.services.UrlSummaryService

import scala.concurrent.{ExecutionContext, Future}

object UrlSummaryLib {
  def getSummary(url: String)(implicit ec: ExecutionContext): Future[String] = {
    Database.createSchema().flatMap { _ =>
      UrlSummaryService.getUrlSummary(url)
    }
  }
}
