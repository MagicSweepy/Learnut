package learnut.kind

trait Foldable[F[_]] {
  def foldMap[A, M](m: Monoid[M])(fa: F[A])(f: A => M): M
}
