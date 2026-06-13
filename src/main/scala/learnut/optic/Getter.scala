package learnut.optic

import learnut.kind.Applicative

trait Getter[S, A] extends Optic[S, S, A, A] {
  def get(s: S): A

  override def modifyF[F[_]](f: A => F[A])(using ap: Applicative[F]): S => F[S] = s => ap.map(f(get(s)))(_ => s)
}

object Getter {
  def of[S, A](get$: S => A): Getter[S, A] = (s: S) => get$.apply(s)
}
