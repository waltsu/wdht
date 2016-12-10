package com.waltsu.wdht.storage

import com.waltsu.wdht.testhelpers.CleanDatabase
import org.junit.runner.RunWith
import org.specs2.mutable
import org.specs2.runner.JUnitRunner

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}

@RunWith(classOf[JUnitRunner])
class StoreEngineSpec(implicit ec: ExecutionContext) extends mutable.Specification {
  sequential
  "StorageEngine" should {
    "store new key and value" in new CleanDatabase {
      val tests = for {
        storedObject <- StorageEngine.put("foo", "bar")
      } yield {
        storedObject.id should beGreaterThan(0)
        storedObject.key must equalTo("foo")
        storedObject.value must equalTo("bar")
      }
      Await.result(tests, 2 seconds)
    }

    "update old value" in new CleanDatabase {
      val tests = for {
        insertion <- StorageEngine.put("foo", "bar")
        update <- StorageEngine.put("foo", "bazz")
      } yield {
        insertion.id should beGreaterThan(0)
        insertion.id should equalTo(update.id)
        update.key must equalTo("foo")
        update.value must equalTo("bazz")
      }
      Await.result(tests, 2 seconds)
    }
  }
}
