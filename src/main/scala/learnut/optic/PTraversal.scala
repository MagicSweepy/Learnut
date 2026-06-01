package learnut.optic

import learnut.kind.{Profunctor, Wander, Traversable}

trait PTraversal[S, T, A, B] extends POptic[S, T, A, B]

object PTraversal {
  def fromTraversable[G[_], A, B](using Traversable[G]): PTraversal[G[A], G[B], A, B] = new PTraversal[G[A], G[B], A, B] {
    override def eval[P[_, _]](using p: Profunctor[P]): P[A, B] => P[G[A], G[B]] = pab => {
      val pF = p.asInstanceOf[Wander[P]]
      pF.wander(pab)
    }
  }
}