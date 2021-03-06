package com.waltsu.wdht

import akka.actor.ActorSystem
import colossus.WorkingConnection
import colossus.protocols.http.{HttpBody, HttpCodes, HttpRequest}
import colossus.testkit.{CallbackAwait, FakeIOSystem}
import com.waltsu.wdht.DistributedHashTable._
import com.waltsu.wdht.Synchronously._
import com.waltsu.wdht.storage.models.StoredObject
import com.waltsu.wdht.testhelpers.CleanDatabase
import org.junit.runner.RunWith
import org.specs2.mutable
import org.specs2.runner.JUnitRunner
import play.api.libs.json.Json

import scala.concurrent.duration._


@RunWith(classOf[JUnitRunner])
class WDHTServerSpec extends mutable.Specification {
  sequential

  implicit val testAkkaActorSystem = ActorSystem("WDHT-Test")
  implicit val ioSystem = FakeIOSystem
  implicit val testExecutor = ioSystem.testExecutor

  "WDHTHandler" should {
    val server = WorkingConnection.server(new WDHTServer(_)).typedHandler
    "fetch key" in new CleanDatabase {
      put("myKey", "Testing").synchronously

      val response = server.handle(HttpRequest.get("/myKey"))
      val responseString = CallbackAwait.result(response, 2 seconds).body.bytes.utf8String

      val storedObject = Json.parse(responseString).as[StoredObject]
      storedObject.key mustEqual("myKey")
      storedObject.value mustEqual("Testing")
    }

    "return 404 if key wasn't found" in new CleanDatabase {
      val response = server.handle(HttpRequest.get("/foobar"))
      val result = CallbackAwait.result(response, 2.seconds)
      result.code should equalTo(HttpCodes.NOT_FOUND)
    }

    "insert key with value" in new CleanDatabase {
      val putRequest = HttpRequest.put("/newKey").withBody(HttpBody("""{"value": "newValue" }"""))
      val response = server.handle(putRequest)
      val result = CallbackAwait.result(response, 2 seconds)
      result.code should equalTo(HttpCodes.OK)

      val storedValue = get("newKey").synchronously
      storedValue.map(_.value) should beSome("newValue")
    }
  }
}
