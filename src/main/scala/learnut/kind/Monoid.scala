package learnut.kind

trait Monoid[A] extends SemiGroup [A] {

  def empty(): A
  
  final def isEmpty(a: A): Boolean = empty() == a
}

object Monoid {

  def intAdd(): Monoid[Int] = new Monoid[Int] {
    override def empty(): Int = 0

    override def combine(a: Int, b: Int): Int = a + b
  }

  def intMulti(): Monoid[Int] = new Monoid[Int] {
    override def empty(): Int = 0

    override def combine(a: Int, b: Int): Int = a * b
  }

  def boolAnd(): Monoid[Boolean] = new Monoid[Boolean] {
    override def empty(): Boolean = false

    override def combine(a: Boolean, b: Boolean): Boolean = a && b
  }

  def boolOr(): Monoid[Boolean] = new Monoid[Boolean] {
    override def empty(): Boolean = false

    override def combine(a: Boolean, b: Boolean): Boolean = a || b
  }

  def string(): Monoid[String] = new Monoid[String] {
    override def empty(): String = ""

    override def combine(a: String, b: String): String = a + b
  }
  
  def seq[A](): Monoid[Seq[A]] = new Monoid[Seq[A]] {
    override def empty(): Seq[A] = Seq()

    override def combine(a: Seq[A], b: Seq[A]): Seq[A] = a.++(b)
  }
}
