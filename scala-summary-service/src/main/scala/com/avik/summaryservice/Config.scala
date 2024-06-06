package com.avik.summaryservice

import com.typesafe.config.ConfigFactory

object Config {
  private val config = ConfigFactory.load()

  object Database {
    val url: String = config.getString("db.url")
    val user: String = config.getString("db.user")
    val password: String = config.getString("db.password")
  }

  object Api {
    val fastApiUrl: String = config.getString("api.fastApiUrl")
  }
}
