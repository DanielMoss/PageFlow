package ddm.pageflow

import ddm.pageflow.pages.Page
import shapeless.ops.coproduct.LiftAll
import shapeless.ops.hlist.{Diff, LeftFolder, Mapper, Prepend, SubtypeUnifier, ToTraversable}
import shapeless.{::, HList, HNil, Poly1, Poly2}

import scala.language.implicitConversions

object FlowAnalyser {
  def describeConnections[P <: Page,
                          ConnectionDescriptions <: HList,
                          SimplifiedConnDescriptions <: HList](p: P)
                                                              (implicit connectionDescriptions: LiftAll.Aux[Lambda[X => ConnectionDescription[P, X]],
                                                                                                            P#Connections,
                                                                                                            ConnectionDescriptions],
                                                               typeUnifier: SubtypeUnifier.Aux[ConnectionDescriptions,
                                                                                               ConnectionDescription[P, _],
                                                                                               SimplifiedConnDescriptions],
                                                               toList: ToTraversable.Aux[SimplifiedConnDescriptions,
                                                                                         List,
                                                                                         ConnectionDescription[P, _]]): List[ConnectionDescription[P, _]] =
    getConnectionDescriptions(p)

  private object getConnectionDescriptions extends Poly1 {
    implicit def default[P <: Page,
                         ConnectionDescriptions <: HList,
                         SimplifiedConnDescriptions <: HList](implicit connectionDescriptions: LiftAll.Aux[Lambda[X => ConnectionDescription[P, X]],
                                                                                                           P#Connections,
                                                                                                           ConnectionDescriptions],
                                                              typeUnifier: SubtypeUnifier.Aux[ConnectionDescriptions,
                                                                                              ConnectionDescription[P, _],
                                                                                              SimplifiedConnDescriptions],
                                                              toList: ToTraversable.Aux[SimplifiedConnDescriptions,
                                                                                        List,
                                                                                        ConnectionDescription[P, _]]): Case.Aux[P, List[ConnectionDescription[P, _]]] =
      at[P](_ => connectionDescriptions.instances.unifySubtypes.toList)
  }

  def exploreFrom[P <: Page, AccessiblePages <: HList](p: P)
                                                 (implicit explore: LeftFolder.Aux[P :: HNil, P :: HNil, recursivelyExplore.type, AccessiblePages],
                                                  toList: ToTraversable.Aux[AccessiblePages, List, Page]): List[Page] =
    exploreFromImpl(p).toList

  private def exploreFromImpl[P <: Page,  FoldResult <: HList](p: P)
                                                              (implicit explore: LeftFolder.Aux[P :: HNil,
                                                                                                P :: HNil,
                                                                                                recursivelyExplore.type,
                                                                                                FoldResult]): FoldResult =
    HList(p).foldLeft(HList(p))(recursivelyExplore)

  private object recursivelyExplore extends Poly2 {
    implicit def default[P <: Page,
                         Acc <: HList,
                         ConnectionExamples <: HList,
                         ConnectionPages <: HList,
                         NewPages <: HList,
                         PrependResult <: HList,
                         Result <: HList](implicit connections: LiftAll.Aux[Example, P#Connections, ConnectionExamples],
                                          mapper: Mapper.Aux[getPageFromExample.type, ConnectionExamples, ConnectionPages],
                                          differ: Diff.Aux[ConnectionPages, Acc, NewPages],
                                          prepend: Prepend.Aux[Acc, NewPages, PrependResult],
                                          folder: LeftFolder.Aux[NewPages, PrependResult, recursivelyExplore.type, Result]): Case.Aux[Acc, P, Result] =
      at[Acc, P] { (acc, _) =>
        val newPages = connections.instances.map(getPageFromExample).diff[Acc]
        newPages.foldLeft(acc ++ newPages)(recursivelyExplore)
      }
  }

  private object getPageFromExample extends Poly1 {
    implicit def default[P <: Page]: Case.Aux[Example[P], P] =
      at[Example[P]](_.get)
  }

  def findAllAccessibleConnections[P <: Page,
                                   AccessiblePages <: HList,
                                   AllConnections <: HList](p: P)
                                                           (implicit explorer: LeftFolder.Aux[P :: HNil,
                                                                                              P :: HNil,
                                                                                              recursivelyExplore.type,
                                                                                              AccessiblePages],
                                                            connectionsGetter: Mapper.Aux[getConnectionDescriptions.type,
                                                                                          AccessiblePages,
                                                                                          AllConnections],
                                                            connectionsAccumulator: LeftFolder.Aux[AllConnections,
                                                                                                   List[ConnectionDescription[_, _]],
                                                                                                   accumulateDescriptions.type,
                                                                                                   List[ConnectionDescription[_, _]]]): List[ConnectionDescription[_, _]] =
    exploreFromImpl(p)
      .map(getConnectionDescriptions)
      .foldLeft(List.empty[ConnectionDescription[_, _]])(accumulateDescriptions)

  private object accumulateDescriptions extends Poly2 {
    implicit def default[P <: Page]: Case.Aux[List[ConnectionDescription[_, _]],
                                              List[ConnectionDescription[P, _]],
                                              List[ConnectionDescription[_, _]]] =
      at[List[ConnectionDescription[_, _]], List[ConnectionDescription[P, _]]](_ ++ _)
  }
}
