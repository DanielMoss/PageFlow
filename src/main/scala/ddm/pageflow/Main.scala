package ddm.pageflow

import ddm.pageflow.pages.Start

object Main extends App {
  type StartingPage = Start.type

  println(
    GraphGenerator.export(
      FlowAnalyser.findAllAccessibleConnections(
        Example[StartingPage].get
      )
    )
  )
}
