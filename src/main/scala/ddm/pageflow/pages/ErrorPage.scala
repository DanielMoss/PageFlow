package ddm.pageflow.pages

import ddm.pageflow.utils.CoproductSubtypeUnifier
import ddm.pageflow.{ConnectionDescription, Example, PageID}
import shapeless.{:+:, CNil}

object ErrorPage {
  implicit def cd[Return <: Page : PageID]: ConnectionDescription[ErrorPage[Return], Return] =
    ConnectionDescription.of[ErrorPage[Return], Return]("Always")

  implicit def example[Return <: Page : Example]: Example[ErrorPage[Return]] =
    Example.of(new ErrorPage[Return]("Some description of the problem", () => Example[Return].get))

  implicit def pageID[P <: Page]: PageID[ErrorPage[P]] =
    PageID.of("Error")
}

final class ErrorPage[Return <: Page](message: String, f: () => Return) extends Page {
  type Connections = Return :+: CNil
  protected val typeEvidence: CoproductSubtypeUnifier[Connections, Page] = implicitly

  protected def handleImpl(input: String): Connections =
    f()
}
