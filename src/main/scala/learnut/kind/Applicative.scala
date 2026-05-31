package learnut.kind

trait Applicative[F[_]] extends Functor[F] {
  def point[A](a: A): F[A]
  
  def lift[A, B](ff: F[A => B]): F[A] => F[B]
  
  def ap[A, B](ff: F[A => B])(fa: F[A]): F[B] = lift(ff).apply(fa)
  
  final def ap[A, B](f: A => B)(fa: F[A]): F[B] = map(fa)(f)
}
