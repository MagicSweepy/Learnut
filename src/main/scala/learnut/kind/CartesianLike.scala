package learnut.kind

// F[A] ~ (C, A)
trait CartesianLike[F[_], C] extends Functor[F] with Traversable[F] {
  def to[A](fa: F[A]): (C, A)
  def from[A](fa: (C, A)): F[A]

  override def map[A, B](fa: F[A])(f: A => B): F[B] = {
    val (c, a) = to(fa)
    from((c, f(a)))
  }

  override def foldMap[A, M](m: Monoid[M])(fa: F[A])(f: A => M): M = {
    val (_, a) = to(fa)
    f(a)
  }

  override def traverse[A, B, T[_]](ap: Applicative[T])(fa: F[A])(f: A => T[B]): T[F[B]] = {
    val (c, a) = to(fa)
    ap.map(f(a))(b => from((c, b)))
  }
}
