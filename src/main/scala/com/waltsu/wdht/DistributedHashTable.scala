package com.waltsu.wdht

import com.typesafe.config.ConfigFactory
import com.waltsu.wdht.storage.StorageEngine
import com.waltsu.wdht.storage.models.StoredObject

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.concurrent.{Await, Future}

object DistributedHashTable {
  def get(key: String): Future[Option[StoredObject]] = {
    StorageEngine.get(key)
  }

  def put(key: String, value: String): Future[StoredObject] = {
    StorageEngine.put(key, value)
  }
}

object Synchronously {
  private val OperationTimeout = ConfigFactory.load().getInt("wdht.operationTimeout") seconds

  implicit class OperationWithSynchronously(operation: Future[StoredObject]) {
    def synchronously(implicit timeout: FiniteDuration = OperationTimeout): StoredObject = {
      Await.result(operation, timeout)
    }
  }
}
