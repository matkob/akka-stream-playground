package com.mkobiers.playground

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.Message
import akka.stream.KillSwitches
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, MergeHub, Sink, Source}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

object DynamicStream extends App {

  implicit val system: ActorSystem = ActorSystem("playground")
  implicit val ec: ExecutionContext = system.dispatcher

  val iterator = Iterator.from(0)
  val merge: Source[String, Sink[String, NotUsed]] = MergeHub.source[String]
  val broadcast = BroadcastHub.sink[String]
  val (sink, source) = merge.toMat(broadcast)(Keep.both).run()
  var inner = createInnerStream(s"inner${iterator.next()}")

  val flow: Flow[String, String, NotUsed] = Flow.fromSinkAndSource(sink, source)

  system.scheduler.scheduleAtFixedRate(2000.millis, 10.seconds) { () =>
    inner.shutdown()
    inner = Source.tick(500.millis, 500.millis, s"inner${iterator.next()}").viaMat(KillSwitches.single)(Keep.right).to(sink).run()
  }

  Source.tick(1000.millis, 1000.millis, "msg").via(flow).runForeach(println)

  def createInnerStream(msg: String) = Source.tick(500.millis, 500.millis, msg).viaMat(KillSwitches.single)(Keep.right).to(sink).run()
}
