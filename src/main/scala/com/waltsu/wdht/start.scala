package com.waltsu.wdht

import akka.actor.ActorSystem
import colossus.IOSystem
import colossus.core.Server

object Start {
  def main(args: Array[String]): Unit = {
    implicit val actorSystem = ActorSystem("WDHT")
    implicit val io = IOSystem()
    Server.start("hello-world", 9000) { worker => new WDHTInitializer(worker) }
  }
}

