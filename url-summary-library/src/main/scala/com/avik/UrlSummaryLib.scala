package com.avik

import com.avik.services.UrlSummaryService
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import scala.concurrent.duration._
import scala.concurrent.Await

object UrlSummaryLib {
  def getSummary(url: String)(implicit ec: ExecutionContext): Future[String] = {
    UrlSummaryService.getUrlSummary(url)
  }

//  def main(args: Array[String]): Unit = {
//    val url = "http://example.com" // replace with the URL you want to get the summary of
//    val futureSummary = getSummary(url)
//    futureSummary.onComplete {
//      case Success(summary) => println(s"Summary: $summary")
//      case Failure(e) => e.printStackTrace()
//    }
//
//    // Wait for the Future to complete before exiting
//    Await.result(futureSummary, 10.seconds)
//  }

}
