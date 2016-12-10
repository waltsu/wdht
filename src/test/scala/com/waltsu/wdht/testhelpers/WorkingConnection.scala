package colossus

import akka.actor.ActorSystem
import akka.testkit.TestProbe
import colossus.core.{ServerConnection, ServerConnectionHandler, ServerContext}
import colossus.testkit.{FakeIOSystem, TypedMockConnection}

object WorkingConnection {
  def server[T <: ServerConnectionHandler](handlerF: ServerContext => T, _maxWriteSize: Int = 1024)
                                          (implicit sys: ActorSystem): ServerConnection with TypedMockConnection[T] = {
    val (_serverProbe, server) = FakeIOSystem.fakeServerRef
    val worker = FakeIOSystem.fakeExecutorWorkerRef
    val ctx = ServerContext(server, worker.generateContext())
    val _handler = handlerF(ctx)
    _handler.setBind()
    new ServerConnection(_handler.context.id, _handler, server, worker) with TypedMockConnection[T] {
      def maxWriteSize = _maxWriteSize
      def workerProbe = TestProbe()
      def serverProbe = Some(_serverProbe)
      def typedHandler = _handler
    }
  }

}
