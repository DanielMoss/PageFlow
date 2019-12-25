package ddm.pageflow.utils

import shapeless.{:+:, CNil, Coproduct, DepFn1, Inl, Inr}

trait CoproductSubtypeUnifier[C <: Coproduct, S] extends DepFn1[C] with Serializable { type Out = S }

object CoproductSubtypeUnifier {
  def apply[C <: Coproduct, S](implicit unifier: CoproductSubtypeUnifier[C, S]): CoproductSubtypeUnifier[C, S] =
    unifier

  implicit def cNilUnifier[S]: CoproductSubtypeUnifier[CNil, S] =
    _.impossible

  implicit def coproductUnifier[H, T <: Coproduct, S](implicit st: H <:< S,
                                                      tailUnifier: CoproductSubtypeUnifier[T, S]): CoproductSubtypeUnifier[H :+: T, S] =
    (c: H :+: T) => c match {
      case Inl(h) => h
      case Inr(t) => tailUnifier(t)
    }
}
