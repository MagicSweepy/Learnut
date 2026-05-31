package learnut.kind

trait BiFunctor[F[_, _]] {
  def bimap[A1, B1, A2, B2](fab: F[A1, B1])(f: A1 => A2, g: B1 => B2): F[A2, B2]
  
  def lmap[A1, B1, A2](fab: F[A1, B1])(f: A1 => A2): F[A2, B1] = bimap(fab)(f, identity)
  
  def rmap[A1, B1, B2](fab: F[A1, B1])(g: B1 => B2): F[A1, B2] = bimap(fab)(identity, g)
}
