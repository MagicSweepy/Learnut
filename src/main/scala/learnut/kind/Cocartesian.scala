package learnut.kind

trait Cocartesian[F[_, _]] extends Profunctor[F] {
  def left[A, B, C](fab: F[A, B]): F[Either[A, C], Either[B, C]]

  def right[A, B, C](fab: F[A, B]): F[Either[C, A], Either[C, B]] = dimap(left(fab))(swap, swap)

  private def swap[X, Y]: Either[Y, X] => Either[X, Y] = {
    case Left(y)  => Right(y)
    case Right(x) => Left(x)
  }
}
