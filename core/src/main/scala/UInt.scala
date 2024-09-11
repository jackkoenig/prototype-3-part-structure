
package fakechisel

private[fakechisel] trait UIntImpl { self: Bits =>

  def width: Option[Int]

  protected def _plusImpl(n: UInt)(implicit info: FakeSourceInfo): UInt = {
    val w = (this.width, n.width) match {
      case (None, _) => None
      case (_, None) => None
      case (Some(x), Some(y)) => Some((x max y) + 1)
    }
    println(s"[$info] Calling + on $this with $n gives resulting width $w")
    new UInt(w)
  }

  protected def _minusImpl(n: UInt)(implicit info: FakeSourceInfo): UInt = {
    val w = (this.width, n.width) match {
      case (None, _) => None
      case (_, None) => None
      case (Some(x), Some(y)) => Some(x - y)
    }
    println(s"[$info] Calling - on $this with $n gives resulting width $w")
    new UInt(w)
  }
}
