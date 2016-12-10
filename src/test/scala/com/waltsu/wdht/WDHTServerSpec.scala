package com.waltsu.wdht

import akka.actor.ActorSystem
import colossus.protocols.http.{HttpCodes, HttpRequest}
import colossus.testkit.{CallbackAwait, FakeIOSystem, MockConnection}
import org.junit.runner.RunWith
import org.specs2.mutable
import org.specs2.runner.JUnitRunner

import scala.concurrent.duration._
import DistributedHashTable._
import Synchronously._
import com.waltsu.wdht.storage.models.StoredObject
import com.waltsu.wdht.testhelpers.CleanDatabase
import play.api.libs.json.Json

@RunWith(classOf[JUnitRunner])
class WDHTServerSpec extends mutable.Specification {
  sequential

  implicit val testAkkaActorSystem = ActorSystem("WDHT-Test")
  implicit val testExecutor = FakeIOSystem.testExecutor

  "WDHTHandler" should {
    val server = MockConnection.server(new WDHTServer(_)).typedHandler
    "fetch given key" in new CleanDatabase {
      put("myKey", "Testing").synchronously

      val response = server.handle(HttpRequest.get("/myKey"))
      val responseString = CallbackAwait.result(response, 2 seconds).body.bytes.utf8String

      val storedObject = Json.parse(responseString).as[StoredObject]
      storedObject.key mustEqual("myKey")
      storedObject.value mustEqual("Testing")
    }

    "response 404 if key wasn't found" in new CleanDatabase {
      val response = server.handle(HttpRequest.get("/foobar"))
      val result = CallbackAwait.result(response, 2 seconds)
      result.code should equalTo(HttpCodes.NOT_FOUND)
    }
  }
}
