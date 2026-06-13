package learnut.kind

trait Traversable[F[_]] extends Functor[F] with Foldable[F] {
  def traverse[A, B, T[_]](ap: Applicative[T])(fa: F[A])(f: A => T[B]): T[F[B]]

  final def flip[A, T[_]](ap: Applicative[T])(fa: F[T[A]]): T[F[A]] = traverse(ap)(fa)(identity)
}

object Traversable {
  given list: Traversable[List] = new Traversable[List] {
    override def traverse[A, B, T[_]](ap: Applicative[T])(fa: List[A])(f: A => T[B]): T[List[B]]
      = fa.foldRight(ap.pure(List.empty[B])) { (a, tb) => ap.ap(ap.map(tb)(tail => (b: B) => b :: tail))(f(a)) }

    override def map[A, B](fa: List[A])(f: A => B): List[B] = fa.map(f)

    override def foldMap[A, M](m: Monoid[M])(fa: List[A])(f: A => M): M 
      = fa.foldLeft(m.empty())((acc, a) => m.combine(acc, f(a)))
  }
}