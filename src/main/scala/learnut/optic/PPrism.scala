package learnut.optic

import learnut.kind.{Profunctor, Cocartesian}

trait PPrism[S, T, A, B] extends POptic[S, T, A, B] {
  def snap(s: S): Either[T, A]

  def build(b: B): T

  override def eval[P[_, _]](using p: Profunctor[P]): P[A, B] => P[S, T] = pab => {
    val pC = p.asInstanceOf[Cocartesian[P]]
    pC.dimap(pC.right(pab))(snap, _.fold(identity, build))
  }
}

object PPrism {
  def of[S, T, A, B](snap$: S => Either[T, A], build$: B => T): PPrism[S, T, A, B] = new PPrism[S, T, A, B] {
    override def snap(s: S): Either[T, A] = snap$.apply(s)

    override def build(b: B): T = build$.apply(b)
  }
}
