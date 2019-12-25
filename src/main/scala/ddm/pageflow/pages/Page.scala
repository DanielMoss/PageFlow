package ddm.pageflow.pages

import ddm.pageflow.utils.CoproductSubtypeUnifier
import shapeless.Coproduct
import shapeless.ops.coproduct.Inject

import scala.language.implicitConversions

trait Page {
  type Connections <: Coproduct
  protected val typeEvidence: CoproductSubtypeUnifier[Connections, Page]

  implicit final protected def toConnection[P <: Page](page: P)
                                                      (implicit inject: Inject[Connections, P]): Connections =
    Coproduct(page)

  final def handle(input: String): Page =
    typeEvidence.apply(handleImpl(input))

  protected def handleImpl(input: String): Connections
}
