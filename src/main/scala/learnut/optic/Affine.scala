package learnut.optic

import learnut.kind.Applicative

trait Affine[S, A] extends Optic[S, S, A, A] {
  def preview(s: S): Either[S, A]
  
  def set(a: A, s: S): S

  override def modifyF[F[_]](f: A => F[A])(using ap: Applicative[F]): S => F[S] = s => preview(s) match
    case Left(s)  => ap.pure(s)
    case Right(a) => ap.map(f(a))(a1 => set(a1, s))
}

object Affine {
  def of[S, A](preview$: S => Either[S, A], set$: (A, S) => S): Affine[S, A] = new Affine[S, A] {
    override def preview(s: S): Either[S, A] = preview$.apply(s)

    override def set(a: A, s: S): S = set$.apply(a, s)
  }
}
