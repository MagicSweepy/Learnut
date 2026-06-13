package learnut.optic

import learnut.kind.{Applicative, Monoid}

trait Fold[S, A] extends Optic[S, S, A, A] {
  def toList(s: S): List[A]

  def foldMap[M](s: S)(f: A => M)(using m: Monoid[M]): M = toList(s).foldLeft(m.empty())((acc, a) => m.combine(acc, f(a)))

  override def modifyF[F[_]](f: A => F[A])(using ap: Applicative[F]): S => F[S] = s => {
    val combined = toList(s).map(f).foldLeft(ap.pure(())) { (acc, fa) =>
      ap.ap(ap.map(acc)(_ => (_: A) => ()))(fa)
    }
    ap.map(combined)(_ => s)
  }
}

object Fold {
  def of[S, A](toList$: S => List[A]): Fold[S, A] = s => toList$.apply(s)
}
