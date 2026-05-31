package learnut.kind

trait Alternative[F[_]] extends Applicative[F] {
  def empty[A](): F[A]

  def orElse[A](fa: F[A])(eval: ? => F[A]): F[A]
}
