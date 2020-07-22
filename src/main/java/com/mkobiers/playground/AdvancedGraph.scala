package com.mkobiers.playground

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ClosedShape, FlowShape, SinkShape}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge, RunnableGraph, Sink, Source}

object AdvancedGraph {

  implicit val system: ActorSystem = ActorSystem("playground")

  def main(args: Array[String]): Unit = {
    val sourceOdd = Source((0 to 10).filter(_ % 2 == 1))
    val sourceEven = Source((0 to 10).filter(_ % 2 == 0))
    val bigFlow = Flow.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
      import GraphDSL.Implicits._

      val subFlow = Flow.fromFunction[Int, Int](n => {
        println(f"$n passing by")
        n
      })

      val broadcast = builder.add(Broadcast[Int](4))
      val merge = builder.add(Merge[Int](4))

      broadcast ~> subFlow ~> merge
      broadcast ~> subFlow ~> merge
      broadcast ~> subFlow ~> merge
      broadcast ~> subFlow ~> merge
      FlowShape(broadcast.in, merge.out)
    })
    Source.combine(sourceOdd, sourceEven)(Merge(_)).via(bigFlow).to(Sink.ignore).run()
  }

}
