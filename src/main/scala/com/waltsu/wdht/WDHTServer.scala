package com.waltsu.wdht

import java.util.concurrent.Executors

import colossus._
import core._
import service._
import protocols.http._
import UrlParsing._
import HttpMethod._
import com.typesafe.config.ConfigFactory
import play.api.libs.json.{JsError, JsSuccess, Json}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import scala.language.postfixOps

class WDHTServer(context: ServerContext) extends HttpService(context) {
  private implicit val executionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(5))
  private val RequestTimeout = ConfigFactory.load().getInt("wdht.requestTimeout") seconds


  def handle = {
    case request @ Get on Root / key =>
      val response = DistributedHashTable.get(key).map {
        case Some(storedObject) =>
          request.ok(Json.toJson(storedObject).toString)
        case None => request.notFound("")
      }

      Callback.successful(Await.result(response, RequestTimeout))

    case request @ Put on Root / key =>
      val response = (Json.parse(request.body.toString) \ "value").validate[String] match {
        case value: JsSuccess[String] =>
          DistributedHashTable.put(key, value.value).map((storedObject) => {
            request.ok(Json.toJson(storedObject).toString())
          })
        case error: JsError =>
          Future { request.badRequest("Errors: " + JsError.toJson(error).toString()) }

      }
      Callback.successful(Await.result(response, RequestTimeout))

    case request =>
      Callback.successful(request.notFound("not found"))
  }
}

class WDHTInitializer(worker: WorkerRef) extends Initializer(worker) {
  def onConnect = context => new WDHTServer(context)
}
