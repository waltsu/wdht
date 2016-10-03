package com.waltsu.wdht

import akka.actor.ActorSystem
import colossus.protocols.http.HttpRequest
import colossus.testkit.{CallbackAwait, FakeIOSystem, MockConnection}
import org.junit.runner.RunWith
import org.specs2.mutable
import org.specs2.runner.JUnitRunner

import scala.concurrent.duration._

@RunWith(classOf[JUnitRunner])
class WDHTServerSpec extends mutable.Specification {
  implicit val testAkkaActorSystem = ActorSystem("WDHT-Test")
  implicit val testExecutor = FakeIOSystem.testExecutor

  "WDHTHandler" should {
    val connection = MockConnection.server(new WDHTHandler(_))
    "generate response" in {
      val response = connection.typedHandler.handle(HttpRequest.get("/get"))
      val responseString = CallbackAwait.result(response, 2 seconds).body.bytes.utf8String
      responseString mustEqual("Getting stuff")
    }
  }
}
