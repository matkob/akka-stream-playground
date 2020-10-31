package com.mkobiers.playground

import akka.Done
import akka.actor.ActorSystem
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source}

import scala.concurrent.Future

object Backpressure {

  implicit val system: ActorSystem = ActorSystem("playground")

  def main(args: Array[String]): Unit = {
    val source = Source(List(1,2,3,4,5,6,7,8,9)).buffer(2, OverflowStrategy.dropTail)
    val flow = Flow.fromFunction[Int, Int](n => {
      println(s"$n passing by")
      n
    })
    val sink = Sink.foreach[Int](n => {
      println(f"received $n")
      Thread.sleep(1000)
    })
    source.async.via(flow).async.to(sink).run()
  }

}
