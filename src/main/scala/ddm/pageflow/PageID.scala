package ddm.pageflow

import ddm.pageflow.pages.Page

object PageID {
  def apply[P](implicit id: PageID[P]): PageID[P] = id

  def of[P <: Page](raw: String): PageID[P] =
    new PageID[P](raw) {}
}

sealed abstract case class PageID[+P](raw: String)
