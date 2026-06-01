package learnut.optic

import learnut.kind.{Applicative, Traversable}

trait Traversal[S, A] extends Optic[S, S, A, A]

object Traversal {
  def fromTraversable[G[_], A](using G: Traversable[G]): Traversal[G[A], A] = new Traversal[G[A], A] {
    override def modifyF[F[_]](f: A => F[A])(using ap: Applicative[F]): G[A] => F[G[A]] = ga => G.traverse(ap)(ga)(f)
  }
}