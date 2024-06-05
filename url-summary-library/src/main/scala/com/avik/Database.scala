package com.avik

import com.typesafe.config.ConfigFactory
import slick.jdbc.JdbcBackend.Database as SlickDatabase
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

case class ResponseHistory(id: Long, url: String, summary: String, timestamp: java.sql.Timestamp)

object ResponseHistory {
  def tupled: ((Long, String, String, java.sql.Timestamp)) => ResponseHistory = (ResponseHistory.apply _).tupled
}

class ResponseHistoryTable(tag: Tag) extends Table[ResponseHistory](tag, "response_history") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def url = column[String]("url")
  def summary = column[String]("summary")
  def timestamp = column[java.sql.Timestamp]("timestamp")

  def * = (id, url, summary, timestamp) <> (ResponseHistory.tupled, ResponseHistory.unapply)
}

object Database {
  val config = ConfigFactory.load()
  val db = SlickDatabase.forConfig("db", config)

  val responseHistory = TableQuery[ResponseHistoryTable]

  def createSchema()(implicit ec: ExecutionContext): Future[Unit] = {
    val schema = responseHistory.schema
    val action = schema.createIfNotExists
    db.run(action).map(_ => ())
  }

  def saveResponseHistory(url: String, summary: String)(implicit ec: ExecutionContext): Future[Int] = {
    val timestamp = new java.sql.Timestamp(System.currentTimeMillis())
    val action = responseHistory += ResponseHistory(0, url, summary, timestamp)

    val result = db.run(action)

    result.onComplete {
      case Success(value) => println(s"Successfully inserted $value rows.")
      case Failure(exception) => println(s"Failed to insert data: $exception")
    }

    result
  }
}
