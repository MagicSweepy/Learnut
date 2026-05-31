package learnut.optic

import learnut.kind.Applicative

trait Lens[S, A] extends Optic[S, S, A, A] {
  def view(s: S): A
  
  def update(a: A, s: S): S

  override def modifyF[F[_]](f: A => F[A])(using ap: Applicative[F]): S => F[S] 
    = s => ap.map(f(view(s)))(a => update(a, s))
}

object Lens {
  def of[S, A](view$: S => A, update$: (A, S) => S): Lens[S, A] = new Lens[S, A] {
    override def view(s: S): A = view$.apply(s)
    
    override def update(a: A, s: S): S = update$.apply(a, s)
  }
}