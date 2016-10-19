package com.waltsu.wdht.storage

import com.typesafe.config.ConfigFactory
import com.waltsu.wdht.storage.models.StoredObject
import com.waltsu.wdht.storage.tables.StoredObjectTable
import slick.driver.SQLiteDriver.api._

import scala.concurrent.Future

object StorageEngine {
  private val configuration = ConfigFactory.load()
  private val database = Database.forURL(configuration.getString("storageDatabase.url"), driver = "org.sqlite.JDBC")
  private implicit val session = database.createSession()

  def put(key: String, value: String): Future[StoredObject] = {
    val storedObject = StoredObject(0, key, value)
    database.run { StoredObjectTable.returningStoredObjects += storedObject }
  }

  def reset() = {
    database.run { StoredObjectTable.storedObjects.delete }
  }
}
