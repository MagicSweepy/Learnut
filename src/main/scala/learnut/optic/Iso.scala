package learnut.optic

import learnut.kind.Applicative

trait Iso[S, A] extends Optic[S, S, A, A] {
  def from(s: S): A
  
  def to(a: A): S

  override def modifyF[F[_]](f: A => F[A])(using ap: Applicative[F]): S => F[S] = s => ap.map(f(from(s)))(to)
}

object Iso {
  def of[S, A](from$: S => A, to$: A => S): Iso[S, A] = new Iso[S, A] {
    override def from(s: S): A = from$.apply(s)

    override def to(a: A): S = to$.apply(a)
  }
}