
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

// Normally this would be in a different file, but its here to padd up the size of the
// cross-compilable version of this
object Main {
  def main(args: Array[String]): Unit = {
    println("Hello World!")
    val x = new UInt(Some(3))
    println(x)
    println(x.tail(3))
    val y = new UInt(None)
    println(y)
    println(y.head(4))

    val z = x + y
    println(z)

    val a = new UInt(Some(2))
    val b = x - a
    println(b)

    val c = x(2)(0)
    println(c)
  }
}
