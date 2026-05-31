package learnut.optic

import learnut.kind.Profunctor

// Profunctor encoding
trait POptic[S, T, A, B] {
  def eval[P[_, _]](using Profunctor[P]): P[A, B] => P[S, T]

  def andThen[U, V](other: POptic[A, B, U, V]): POptic[S, T, U, V] = POptic.Composed(this, other)
}

object POptic {
  private final class Composed[S, T, A1, B1, A2, B2](outer: POptic[S, T, A1, B1],
                                                     inner: POptic[A1, B1, A2, B2]) extends POptic[S, T, A2, B2] {
    override def eval[P[_, _]](using P: Profunctor[P]): P[A2, B2] => P[S, T] = outer.eval.compose(inner.eval)
  }

  final class Composition[S, T, A, B](optics: List[POptic[?, ?, ?, ?]]) extends POptic[S, T, A, B] {
    override def eval[P[_, _]](using P: Profunctor[P]): P[A, B] => P[S, T] = {
      val proof: List[Any => Any] = optics.reverse.map { optic =>
        val unchecked = optic.asInstanceOf[POptic[Any, Any, Any, Any]]
        unchecked.eval(using P).asInstanceOf[Any => Any]
      }
      input => proof.foldLeft(input: Any)((acc, f) => f(acc)).asInstanceOf[P[S, T]]
    }

    override def toString: String = optics.mkString("(", " ◦ ", ")")
  }
}