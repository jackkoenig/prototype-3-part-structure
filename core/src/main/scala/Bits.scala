
package fakechisel

import fakechisel.macros.SourceInfoTransform

abstract class Bits(val width: Option[Int]) {

  override def toString: String = s"Bits($width)"

  final def tail(n: Int): UInt = macro SourceInfoTransform.nArg

  final def head(n: Int): UInt = macro SourceInfoTransform.nArg

  def do_tail(n: Int)(implicit info: FakeSourceInfo): UInt = {
    val w = width match {
      case Some(x) =>
        require(x >= n, s"Can't tail($n) for width $x < $n")
        Some(x - n)
      case None => None
    }
    println(s"[$info] Calling tail on $this with $n gives resulting width $w")
    new UInt(w)
  }

  def do_head(n: Int)(implicit info: FakeSourceInfo): UInt = {
    width match {
      case Some(x) => require(x >= n, s"Can't head($n) for width $x < $n")
      case None => ()
    }
    println(s"[$info] Calling head on $this with $n gives resulting width $n")
    new UInt(Some(n))
  }
}

class UInt(width: Option[Int]) extends Bits(width) {
  final def +(n: UInt): UInt = macro SourceInfoTransform.nArg

  def do_+(n: UInt)(implicit info: FakeSourceInfo): UInt = {
    val w = (this.width, n.width) match {
      case (None, _) => None
      case (_, None) => None
      case (Some(x), Some(y)) => Some(x + y)
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
  }
}
