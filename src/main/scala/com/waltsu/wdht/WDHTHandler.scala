package com.waltsu.wdht

import colossus._
import core._
import service._
import protocols.http._
import UrlParsing._
import HttpMethod._

class WDHTHandler(context: ServerContext) extends HttpService(context) {
  def handle = {
    case request @ Get on Root / "get" => {
      Callback.successful(request.ok("Getting stuff"))
    }
  }
}

class WDHTInitializer(worker: WorkerRef) extends Initializer(worker) {
  def onConnect = context => new WDHTHandler(context)
}
