package learnut.kind

// F[A] ~ Either[C, A]
trait Cocartesian[F[_], C] extends Functor[F] with Traversable[F] {
  def to[A](fa: F[A]): Either[C, A]
  def from[A](fa: Either[C, A]): F[A]

  override def map[A, B](fa: F[A])(f: A => B): F[B] = to(fa) match 
    case Left(c)  => from(Left(c))
    case Right(a) => from(Right(f(a)))

  override def foldMap[A, M](m: Monoid[M])(fa: F[A])(f: A => M): M = to(fa) match 
    case Left(_)  => m.empty()
    case Right(a) => f(a)

  override def traverse[A, B, T[_]](ap: Applicative[T])(fa: F[A])(f: A => T[B]): T[F[B]] = to(fa) match 
    case Left(c)  => ap.point(from(Left(c)))
    case Right(a) => ap.map(f(a))(b => from(Right(b)))
}
