package learnut.optic

import learnut.kind.Profunctor

trait PIso[S, T, A, B] extends POptic[S, T, A, B] {
  def from(s: S): A
  
  def to(b: B): T

  override def eval[P[_, _]](using p: Profunctor[P]): P[A, B] => P[S, T] = a => p.dimap(a)(from, to)
}

object PIso {
  def of[S, T, A, B](from$: S => A, to$: B => T): PIso[S, T, A, B] = new PIso[S, T, A, B] {
    override def from(s: S): A = from$.apply(s)

    override def to(b: B): T = to$.apply(b)
  }
}
