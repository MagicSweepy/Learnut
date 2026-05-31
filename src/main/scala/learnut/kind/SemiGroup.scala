package learnut.kind

@FunctionalInterface
trait SemiGroup[A] {
  def combine(a: A, b: A): A
}

object SemiGroup {
  
  def intAdd(): SemiGroup[Int] = (a, b) => a + b
  
  def intMulti(): SemiGroup[Int] = (a, b) => a * b

  def boolAnd(): SemiGroup[Boolean] = (a, b) => a && b

  def boolOr(): SemiGroup[Boolean] = (a, b) => a || b

  def string(): SemiGroup[String] = (a, b) => a + b

  def seq[A](): SemiGroup[Seq[A]] = (a, b) => a.++(b)
}