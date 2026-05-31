package learnut.kind

trait Cartesian[F[_, _]] extends Profunctor[F] {
  def _1[A, B, C](fab: F[A, B]): F[(A, C), (B, C)]

  def _2[A, B, C](fab: F[A, B]): F[(C, A), (C, B)] = dimap(_1(fab))(swap, swap)

  private def swap[X, Y]: ((Y, X)) => (X, Y) = { case (y, x) => (x, y) }
}
