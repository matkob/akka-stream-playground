package com.mkobiers.playground

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ClosedShape
import akka.stream.scaladsl.{Broadcast, GraphDSL, RunnableGraph, Sink, Source}

object Broadcasting {

  implicit val system: ActorSystem = ActorSystem("playground")

  def main(args: Array[String]): Unit = {
    RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
      import GraphDSL.Implicits._

      val source = Source(List(1,2,3,4,5,6,7,8,9))
      val sink = Sink.foreach[Int](n => {
        println(f"received $n")
      })
      val broadcast = builder.add(Broadcast[Int](4))

      source ~> broadcast
      broadcast ~> sink
      broadcast ~> sink
      broadcast ~> sink
      broadcast ~> sink
      ClosedShape
    }).run()
  }

}
