package learnut.optic

import learnut.kind.{Profunctor, Wander, Traversable}

trait PFold[S, T, A, B] extends POptic[S, T, A, B] {
  def toList(s: S): List[A]
  
  def review(s: S): T

  override def eval[P[_, _]](using p: Profunctor[P]): P[A, B] => P[S, T] = pab => {
    val pF = p.asInstanceOf[Wander[P]]
    pF.dimap(pF._1(pF.wander[A, B, List](pab)))(s => (toList(s), s), { case (_, s) => review(s) })
  }
}

object PFold {
  def of[S, T, A, B](toList$: S => List[A], review$: S => T): PFold[S, T, A, B] = new PFold[S, T, A, B] {
    override def toList(s: S): List[A] = toList$.apply(s)

    override def review(s: S): T = review$.apply(s)
  }
}
