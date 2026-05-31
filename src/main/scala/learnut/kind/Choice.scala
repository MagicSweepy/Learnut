package learnut.kind

trait Choice[L, R] {
  def left(): L
  
  def right(): R
  
  def isLeft: Boolean
  
  def isRight: Boolean
  
  def lmap[L2](f: L => L2): Choice[L2, R]
  
  def rmap[R2](f: R => R2): Choice[L, R2]
  
  def bmap[T](l: L => T, r: R => T): T
  
  def flatLMap[L2](f: L => Choice[L2, R]): Choice[L2, R]
  
  def flatRMap[R2](f: R => Choice[L, R2]): Choice[L, R2]
}
