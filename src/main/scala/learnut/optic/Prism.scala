package learnut.optic

import learnut.kind.Applicative

trait Prism[S, A] extends Optic[S, S, A, A] {
  def snap(s: S): Either[S, A]
  
  def build(a: A): S

  override def modifyF[F[_]](f: A => F[A])(using ap: Applicative[F]): S => F[S] = s => snap(s) match {
    case Left(s)  => ap.pure(s)
    case Right(a) => ap.map(f(a))(build)
  }
}

object Prism {
  def of[S, A](snap$: S => Either[S, A], build$: A => S): Prism[S, A] = new Prism[S, A] {
    override def snap(s: S): Either[S, A] = snap$.apply(s)
    
    override def build(a: A): S = build$.apply(a)
  }
}
