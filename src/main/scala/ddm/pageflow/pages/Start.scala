package ddm.pageflow.pages

import ddm.pageflow.utils.CoproductSubtypeUnifier
import ddm.pageflow.{ConnectionDescription, PageID}
import shapeless.{:+:, CNil}

object Start extends Page {
  type Connections = DeadEnd.type :+: PageB :+: ErrorPage[Start.type] :+: CNil
  protected val typeEvidence: CoproductSubtypeUnifier[Connections, Page] = implicitly

  protected def handleImpl(input: String): Connections =
    input match {
      case "A" => DeadEnd
      case "B" => new PageB(count = 3)
      case  _  => new ErrorPage[Start.type](message = "Unrecognised option", () => Start)
    }

  implicit val pageID: PageID[Start.type] =
    PageID.of("Start")

  implicit val cdDeadEnd: ConnectionDescription[Start.type, DeadEnd.type] =
    ConnectionDescription.of[Start.type, DeadEnd.type]("On submitting 'A'")

  implicit val cdB: ConnectionDescription[Start.type, PageB] =
    ConnectionDescription.of[Start.type, PageB]("On submitting 'B'")

  implicit val cdError: ConnectionDescription[Start.type, ErrorPage[Start.type]] =
    ConnectionDescription.of[Start.type, ErrorPage[Start.type]]("On submitting something other than 'A' or 'B'")
}
