package learnut.kind

trait Profunctor[F[_, _]] {
  def dimap[A1, B1, A2, B2](fab: F[A1, B1])(f: A2 => A1, g: B1 => B2): F[A2, B2]
  
  def lmap[A1, B1, A2](fab: F[A1, B1])(f: A2 => A1): F[A2, B1] = dimap(fab)(f, identity)
  
  def rmap[A1, B1, B2](fab: F[A1, B1])(g: B1 => B2): F[A1, B2] = dimap(fab)(identity, g)
}
