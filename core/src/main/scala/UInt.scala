
package fakechisel

import fakechisel.macros.SourceInfoTransform

class UInt(width: Option[Int]) extends Bits(width) {
  final def +(n: UInt): UInt = macro SourceInfoTransform.nArg

  def do_+(n: UInt)(implicit info: FakeSourceInfo): UInt = {
    val w = (this.width, n.width) match {
      case (None, _) => None
      case (_, None) => None
      case (Some(x), Some(y)) => Some((x max y) + 1)
    }
    println(s"[$info] Calling + on $this with $n gives resulting width $w")
    new UInt(w)
  }

  final def -(n: UInt): UInt = macro SourceInfoTransform.nArg

  def do_-(n: UInt)(implicit info: FakeSourceInfo): UInt = {
    val w = (this.width, n.width) match {
      case (None, _) => None
      case (_, None) => None
      case (Some(x), Some(y)) => Some(x - y)
    }
    println(s"[$info] Calling - on $this with $n gives resulting width $w")
    new UInt(w)
  }
}
