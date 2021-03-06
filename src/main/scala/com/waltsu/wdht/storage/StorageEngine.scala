package com.waltsu.wdht.storage

import com.typesafe.config.ConfigFactory
import com.waltsu.wdht.storage.models.StoredObject
import com.waltsu.wdht.storage.tables.StoredObjectTable
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class StorageException(message: String) extends Exception(message)

object StorageEngine {
  private val configuration = ConfigFactory.load()
  private val database = Database.forConfig("storageDatabase", configuration)

  def put(key: String, value: String): Future[StoredObject] = {
    database.run {
      getKeyQuery(key).flatMap[StoredObject, NoStream, Effect.All]({
        case Some(existingObject) =>
          val updatedObject = existingObject.copy(value = value)
          StoredObjectTable.storedObjects.filter(_.id === existingObject.id).update(updatedObject)
            .flatMap[StoredObject, NoStream, Effect.All]((updateCount) => {
              if (updateCount > 0) DBIO.successful(updatedObject)
              else DBIO.failed(new StorageException(s"Failed to update key $key"))
            })
        case None =>
          val newObject = StoredObject(0, key, value)
          StoredObjectTable.returningStoredObjects += newObject
      }).transactionally
    }
  }

  def get(key: String): Future[Option[StoredObject]] = {
    database.run { getKeyQuery(key) }
  }

  def reset() = {
    database.run { StoredObjectTable.storedObjects.delete }
  }

  private def getKeyQuery(key: String) = {
    StoredObjectTable.storedObjects.filter(_.key === key).take(1).result.headOption
  }
}
