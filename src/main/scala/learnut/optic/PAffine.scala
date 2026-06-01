package learnut.optic

import learnut.kind.{Profunctor, Cartesian, Cocartesian}

trait PAffine[S, T, A, B] extends POptic[S, T, A, B] {
  def preview(s: S): Either[T, A]
  def set(b: B, s: S): T

  override def eval[P[_, _]](using p: Profunctor[P]): P[A, B] => P[S, T] = pab => {
    val pF = p.asInstanceOf[Cartesian[P] & Cocartesian[P]]
    pF.dimap(pF.right(pF._1(pab)))(s => preview(s).map(a => (a, s)), _.fold(identity, (b, s) => set(b, s)))
  }
}

object PAffine {
  def of[S, T, A, B](preview$: S => Either[T, A], set$: (B, S) => T): PAffine[S, T, A, B] = new PAffine[S, T, A, B] {
    override def preview(s: S): Either[T, A] = preview$.apply(s)

    override def set(b: B, s: S): T = set$.apply(b, s)
  }
}
