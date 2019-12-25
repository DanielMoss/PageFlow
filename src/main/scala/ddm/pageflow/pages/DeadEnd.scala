package ddm.pageflow.pages

import ddm.pageflow.{ConnectionDescription, PageID}
import ddm.pageflow.utils.CoproductSubtypeUnifier
import shapeless.{:+:, CNil}

object DeadEnd extends Page {
  type Connections = DeadEnd.type :+: CNil
  protected val typeEvidence: CoproductSubtypeUnifier[Connections, Page] = implicitly

  protected def handleImpl(input: String): Connections = DeadEnd

  implicit val pageID: PageID[DeadEnd.type] =
    PageID.of("DeadEnd")

  implicit val cdStart: ConnectionDescription[DeadEnd.type, DeadEnd.type] =
    ConnectionDescription.of[DeadEnd.type, DeadEnd.type]("Always")
}
