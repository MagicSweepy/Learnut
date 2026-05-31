package learnut.kind

trait Selective[F[_]] extends Applicative[F] {
  def select[A, B](fab: F[Choice[A, B]])(ff: F[A => B]): F[B]
}
