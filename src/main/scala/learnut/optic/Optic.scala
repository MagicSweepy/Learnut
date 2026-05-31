package learnut.optic

import learnut.kind.Applicative

// Van Laarhoven encoding
trait Optic[S, T, A, B] {
  def modifyF[F[_]](f: A => F[B])(using Applicative[F]): S => F[T]

  def andThen[A1, B1](other: Optic[A, B, A1, B1]): Optic[S, T, A1, B1] = Optic.Composed(this, other)
}

object Optic {
  private final class Composed[S, T, A, B, A1, B1](outer: Optic[S, T, A, B],
                                                   inner: Optic[A, B, A1, B1]) extends Optic[S, T, A1, B1] {
    override def modifyF[F[_]](f: A1 => F[B1])(using Applicative[F]): S => F[T] = outer.modifyF(inner.modifyF(f))
  }

  final class Composition[S, T, A, B](optics: List[Optic[?, ?, ?, ?]]) extends Optic[S, T, A, B] {
    override def modifyF[F[_]](f: A => F[B])(using G: Applicative[F]): S => F[T] = {
      val proof: List[Any => Any] = optics.reverse.map { optic =>
          val unchecked = optic.asInstanceOf[Optic[Any, Any, Any, Any]]
          (prev: Any) => unchecked.modifyF(prev.asInstanceOf[Any => F[Any]])(using G)
      }
      proof.foldLeft(f: Any) { (current, step) => step(current) }.asInstanceOf[S => F[T]]
    }

    override def toString: String = optics.mkString("(", " ◦ ", ")")
  }
}