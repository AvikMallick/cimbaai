package com.avik.summaryservice

// Importing necessary libraries and modules
import org.slf4j.LoggerFactory
import slick.jdbc.PostgresProfile.api.{Database as SlickDatabase, *}
import slick.jdbc.meta.MTable

import java.sql.Timestamp
import java.time.Instant
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

// Case class representing a summary
case class Summary(url: String, username: String, content: String, timestamp: Timestamp = Timestamp
  .from(Instant.now()), id: String = UUID.randomUUID().toString)

// Slick table definition for 'summaries'
class Summaries(tag: Tag) extends Table[Summary](tag, "summaries") {
  def id = column[String]("id", O.PrimaryKey)
  def url = column[String]("url")
  def username = column[String]("username")
  def content = column[String]("content")
  def timestamp = column[Timestamp]("timestamp", O.Default(Timestamp.from(Instant.now())))

  // Mapping between case class and table
  def * = (url, username, content, timestamp, id) <> ((Summary.apply _).tupled, Summary.unapply)
}

// Database object containing all database related operations
object Database {
  // Logger for logging
  private val logger = LoggerFactory.getLogger(this.getClass)
  // Database configuration
  val db = SlickDatabase.forConfig("database")

  // Query interface for 'summaries' table
  val summaries = TableQuery[Summaries]

  // Function to initialize the schema
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

  // Function to save a summary, returns the number of rows affected
  def saveSummary(summary: Summary)(implicit ec: ExecutionContext): Future[Int] = {
    db.run(summaries += summary).recoverWith {
      case ex: Exception =>
        logger.error("Error saving summary", ex)
        Future.failed(ex)
    }
  }

  // Function to fetch summaries by username
  def fetchSummariesByUsername(username: String)(implicit ec: ExecutionContext): Future[Seq[Summary]] = {
    db.run(summaries.filter(_.username === username).sortBy(_.timestamp.desc).result).recoverWith {
      case ex: Exception =>
        logger.error("Error fetching all summaries", ex)
        Future.failed(ex)
    }
  }
}
