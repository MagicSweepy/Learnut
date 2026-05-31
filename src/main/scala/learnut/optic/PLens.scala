package learnut.optic

import learnut.kind.{Profunctor, Cartesian}

trait PLens[S, T, A, B] extends POptic[S, T, A, B] {
  def view(s: S): A
  def update(b: B, s: S): T

  override def eval[P[_, _]](using p: Profunctor[P]): P[A, B] => P[S, T] = pab => {
    val pF = p.asInstanceOf[Cartesian[P]]
    pF.dimap(pF._1(pab))(s => (view(s), s), (b, s) => update(b, s))
  }
}

object PLens {
  def of[S, T, A, B](view$: S => A, update$: (B, S) => T): PLens[S, T, A, B] = new PLens[S, T, A, B] {
    override def view(s: S): A = view$.apply(s)

    override def update(b: B, s: S): T = update$.apply(b, s)
  }
}
