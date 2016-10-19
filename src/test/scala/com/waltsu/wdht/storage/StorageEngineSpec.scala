package com.waltsu.wdht.storage

import com.waltsu.wdht.testhelpers.CleanDatabase
import org.junit.runner.RunWith
import org.specs2.mutable
import org.specs2.runner.JUnitRunner

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}

@RunWith(classOf[JUnitRunner])
class StorageEngineSpec(implicit ec: ExecutionContext) extends mutable.Specification {
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
  }
}
