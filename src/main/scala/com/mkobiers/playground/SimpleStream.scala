package com.mkobiers.playground

import akka.Done
import akka.actor.ActorSystem
import akka.stream.{Materializer, scaladsl}
import akka.stream.scaladsl.{Keep, RunnableGraph, Sink, Source}

import scala.concurrent.Future

object SimpleStream {

  implicit val system: ActorSystem = ActorSystem("playground")

  def main(args: Array[String]): Unit = {
    val source = Source(List(1,2,3))
    val sink = Sink.foreach[Int](n => println(n))
    val graph: RunnableGraph[Future[Done]] = source.toMat(sink)(Keep.right)
    graph.run()
  }

}
