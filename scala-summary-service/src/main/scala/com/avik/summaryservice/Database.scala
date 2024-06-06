package com.avik.summaryservice

import slick.jdbc.PostgresProfile.api.{Database => SlickDatabase}
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{Future, ExecutionContext}
import slick.jdbc.meta.MTable
import org.slf4j.LoggerFactory

case class Summary(url: String, content: String)

class Summaries(tag: Tag) extends Table[Summary](tag, "summaries") {
  def url = column[String]("url", O.PrimaryKey)
  def content = column[String]("content")

  def * = (url, content) <> ((Summary.apply _).tupled, Summary.unapply)
}

object Database {
  private val logger = LoggerFactory.getLogger(this.getClass)
  val db = SlickDatabase.forConfig("database")

  val summaries = TableQuery[Summaries]

  def initSchema(implicit ec: ExecutionContext): Future[Unit] = {
    db.run(MTable.getTables("summaries")).flatMap { tables =>
      if (tables.isEmpty) {
        logger.info("Creating summaries table...")
        db.run(summaries.schema.create).map(_ => ())
      } else {
        logger.info("Summaries table already exists.")
        Future.successful(())
      }
    }.recoverWith {
      case ex: Exception =>
        logger.error("Error initializing schema", ex)
        Future.failed(ex)
    }
  }

  def saveSummary(summary: Summary)(implicit ec: ExecutionContext): Future[Int] = {
    db.run(summaries += summary).recoverWith {
      case ex: Exception =>
        logger.error("Error saving summary", ex)
        Future.failed(ex)
    }
  }

  def close(): Unit = {
    db.close()
    logger.info("Database connection closed.")
  }
}
