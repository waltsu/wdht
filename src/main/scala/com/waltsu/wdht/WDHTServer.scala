package com.waltsu.wdht

import java.util.concurrent.Executors

import colossus._
import core._
import service._
import protocols.http._
import UrlParsing._
import HttpMethod._
import com.typesafe.config.ConfigFactory
import com.waltsu.wdht.storage.models.StoredObject
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.concurrent.{ExecutionContext, Future}

object WDHTServer {
  private implicit val executionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))
}
class WDHTServer(context: ServerContext) extends HttpService(context) {
  import WDHTServer._

  private val RequestTimeout = ConfigFactory.load().getInt("wdht.requestTimeout") seconds

  def handle = {
    case request @ Get on Root / key => Callback.fromFuture(getObject(request, key))
    case request @ Put on Root / key => Callback.fromFuture(putObject(request, key))
    case request => Callback.successful(request.notFound("not found"))
  }

  private def getObject(request: HttpRequest, key: String): Future[HttpResponse] = {
    DistributedHashTable.get(key).map {
      case Some(storedObject) =>
        request.ok(Json.toJson(storedObject).toString)
      case None => request.notFound("")
    }
  }

  private def putObject(request: HttpRequest, key: String): Future[HttpResponse] = {
    (Json.parse(request.body.toString) \ "value").validate[String] match {
      case value: JsSuccess[String] =>
        DistributedHashTable.put(key, value.value).map((storedObject) => {
          request.ok(Json.toJson(storedObject).toString())
        })
      case error: JsError =>
        Future { request.badRequest("Errors: " + JsError.toJson(error).toString()) }
    }
  }
}

class WDHTInitializer(worker: WorkerRef) extends Initializer(worker) {
  def onConnect = context => new WDHTServer(context)
}
