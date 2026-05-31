package learnut.kind

trait Traversable[F[_]] extends Functor[F] with Foldable[F] {
  def traverse[A, B, T[_]](ap: Applicative[T])(fa: F[A])(f: A => T[B]): T[F[B]]

  final def flip[A, T[_]](ap: Applicative[T])(fa: F[T[A]]): T[F[A]] = traverse(ap)(fa)(identity)
}