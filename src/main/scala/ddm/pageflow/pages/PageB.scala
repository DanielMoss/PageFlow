package ddm.pageflow.pages

import ddm.pageflow.utils.CoproductSubtypeUnifier
import ddm.pageflow.{ConnectionDescription, Example, PageID}
import shapeless.{:+:, CNil}

object PageB {
  implicit val pageID: PageID[PageB] =
    PageID.of("B")

  implicit val cdStart: ConnectionDescription[PageB, Start.type] =
    ConnectionDescription.of[PageB, Start.type]("Always")

  implicit val example: Example[PageB] =
    Example.of(new PageB(3))
}

final class PageB(count: Int) extends Page {
  type Connections = Start.type :+: CNil
  protected val typeEvidence: CoproductSubtypeUnifier[Connections, Page] = implicitly

  protected def handleImpl(input: String): Connections = Start
}
