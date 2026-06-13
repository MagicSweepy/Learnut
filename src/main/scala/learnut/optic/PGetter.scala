package learnut.optic

import learnut.kind.{Profunctor, Cartesian}

trait PGetter[S, T, A, B] extends POptic[S, T, A, B] {
  def get(s: S): A
  
  def getT(s: S): T

  override def eval[P[_, _]](using p: Profunctor[P]): P[A, B] => P[S, T] = pab => {
    val pF = p.asInstanceOf[Cartesian[P]]
    pF.dimap(pF._1(pab))(s => (get(s), s), (_, s) => getT(s))
  }
}

object PGetter {
  def of[S, T, A, B](get$: S => A, getT$: S => T): PGetter[S, T, A, B] = new PGetter[S, T, A, B] {
    override def get(s: S): A = get$.apply(s)
    
    override def getT(s: S): T = getT$.apply(s)
  }
}
