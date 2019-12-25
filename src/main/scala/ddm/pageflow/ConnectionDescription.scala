package ddm.pageflow

import ddm.pageflow.pages.Page
import shapeless.ops.coproduct.Inject

object ConnectionDescription {
  def apply[From, To](implicit cd: ConnectionDescription[From, To]): ConnectionDescription[From, To] =
    cd

  def of[From <: Page : PageID, To <: Page : PageID](_descriptions: String*)
                                                    (implicit inject: Inject[From#Connections, To]): ConnectionDescription[From, To] =
    new ConnectionDescription[From, To] {
      val from: PageID[From] = PageID[From]
      val to: PageID[To] = PageID[To]
      val descriptions: List[String] = _descriptions.toList
    }
}

sealed trait ConnectionDescription[+From, +To] {
  def from: PageID[From]
  def to: PageID[To]
  def descriptions: List[String]

  final override def toString: String =
    s"${from.raw} -> ${to.raw}: ${descriptions.mkString(start = "[[", sep = "], [", end = "]]")}"
}
