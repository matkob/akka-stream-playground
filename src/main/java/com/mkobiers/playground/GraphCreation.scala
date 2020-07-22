package com.mkobiers.playground

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ClosedShape, OverflowStrategy}
import akka.stream.scaladsl.{GraphDSL, Keep, RunnableGraph, Sink, Source}

object GraphCreation {

  implicit val system: ActorSystem = ActorSystem("playground")

  def main(args: Array[String]): Unit = {
    RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
      import GraphDSL.Implicits._

      val source = Source(List(1,2,3,4,5,6,7,8,9))
      val sink = Sink.foreach[Int](n => {
        println(f"received $n")
      })

      source ~> sink
      ClosedShape
    }).run()
  }

}
