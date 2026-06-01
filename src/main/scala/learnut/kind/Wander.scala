package learnut.kind

trait Wander[P[_, _]] extends Cartesian[P] with Cocartesian[P] {
  def wander[A, B, G[_]](pab: P[A, B])(using G: Traversable[G]): P[G[A], G[B]]
}
