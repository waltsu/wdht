package com.waltsu.wdht.storage

import com.typesafe.config.ConfigFactory
import com.waltsu.wdht.storage.models.StoredObject
import com.waltsu.wdht.storage.tables.StoredObjectTable
import slick.driver.SQLiteDriver.api._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class StorageException(message: String) extends Exception(message)

object StorageEngine {
  private val configuration = ConfigFactory.load()
  private val database = Database.forURL(configuration.getString("storageDatabase.url"), driver = "org.sqlite.JDBC")

  def put(key: String, value: String): Future[StoredObject] = {
    get(key).flatMap {
      case Some(existingObject) =>
        val updatedObject = existingObject.copy(value = value)
        //val updateResult = database.run { StoredObjectTable.storedObjects.filter(_.id === existingObject.id).update(updatedObject) }
        val updateResult = Future.successful(1)
        updateResult.flatMap((updateCount) => {
          if (updateCount > 0) Future.successful(updatedObject)
          else Future.failed(new StorageException(s"Failed to update key ${key}"))
        })
      case None =>
        val newObject = StoredObject(key = key, value = value)
        database.run { StoredObjectTable.returningStoredObjects += newObject }
    }
  }

  def get(key: String): Future[Option[StoredObject]] = {
    database.run { StoredObjectTable.storedObjects.filter(_.key === key).take(1).result.headOption }
  }

  def reset() = {
    database.run { StoredObjectTable.storedObjects.delete }
  }
}
