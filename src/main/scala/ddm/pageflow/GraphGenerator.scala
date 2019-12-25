package ddm.pageflow

import java.io.StringWriter

import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.io.{Attribute, AttributeType, DOTExporter, DefaultAttribute}

import scala.jdk.CollectionConverters._

object GraphGenerator {
  def export(connections: List[ConnectionDescription[_, _]]): String = {
    val graph = build(newGraph())(
      g => connections.map(_.from).distinct.foreach(g.addVertex),
      g => connections.foreach(conn => g.addEdge(conn.from, conn.to, conn)),
    )

    val stringWriter = new StringWriter()
    build(newExporter())(
      _.putGraphAttribute("splines", "ortho"),
      _.exportGraph(graph, stringWriter)
    )
    stringWriter.toString
  }

  private def newGraph(): DefaultDirectedGraph[PageID[_], ConnectionDescription[_, _]] =
    new DefaultDirectedGraph[PageID[_], ConnectionDescription[_, _]](classOf[ConnectionDescription[_, _]])

  private def newExporter(): DOTExporter[PageID[_], ConnectionDescription[_, _]] =
    new DOTExporter[PageID[_], ConnectionDescription[_, _]](
      (component: PageID[_]) => component.raw,
      null,
      null,
      (component: PageID[_]) =>
        Map[String, Attribute](
          "shape" -> new DefaultAttribute("box", AttributeType.STRING)
        ).asJava,
      (component: ConnectionDescription[_, _]) =>
        Map[String, Attribute](
          "arrowsize" -> new DefaultAttribute("0.5", AttributeType.DOUBLE)
        ).asJava,
    )

  private def build[T](t: T)(fs: (T => Unit)*): T = {
    fs.foreach(_.apply(t))
    t
  }
}
