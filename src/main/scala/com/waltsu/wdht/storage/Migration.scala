package com.waltsu.wdht.storage

import com.typesafe.config.ConfigFactory
import org.flywaydb.core.Flyway

object Migration {
  def apply() = {
    new Migration()
  }
}
class Migration {
  private val configuration = ConfigFactory.load()

  private val flyWay = new Flyway()
  flyWay.setDataSource(configuration.getString("storageDatabase.url"), "", "", "select 1")
  flyWay.setLocations(s"filesystem:${configuration.getString("storageDatabase.migrationFolder")}")
  flyWay.setSqlMigrationPrefix("")

  def migrate() = {
    flyWay.migrate()
  }
}
