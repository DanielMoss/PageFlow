package ddm.pageflow

import ddm.pageflow.pages.Page

object Example {
  def apply[P](implicit ex: Example[P]): Example[P] = ex

  def of[P <: Page](p: P): Example[P] =
    new Example[P] { val get: P = p }

  implicit def singletons[P <: Page : ValueOf]: Example[P] =
    Example.of(valueOf[P])
}

sealed trait Example[P] {
  def get: P
}
