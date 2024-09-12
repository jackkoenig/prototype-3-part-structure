
package fakechisel

private[fakechisel] trait BitsImpl {

  def width: Option[Int]

  override def toString: String = s"Bits($width)"

  protected def _tailImpl(n: Int)(implicit info: FakeSourceInfo): UInt = {
    val w = width match {
      case Some(x) =>
        require(x >= n, s"Can't tail($n) for width $x < $n")
        Some(x - n)
      case None => None
    }
    println(s"[$info] Calling tail on $this with $n gives resulting width $w")
    new UInt(w)
  }

  protected def _headImpl(n: Int)(implicit info: FakeSourceInfo): UInt = {
    width match {
      case Some(x) => require(x >= n, s"Can't head($n) for width $x < $n")
      case None => ()
    }
    println(s"[$info] Calling head on $this with $n gives resulting width $n")
    new UInt(Some(n))
  }

  protected def _applyImpl(n: Int)(implicit info: FakeSourceInfo): UInt = {
    width match {
      case Some(x) => require(x > n, s"Can't extract bit $n for width $x")
      case None => ()
    }
    println(s"[$info] Calling apply on $this with $n")
    new UInt(Some(1))
  }
}

