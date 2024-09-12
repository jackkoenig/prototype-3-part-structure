
package fakechisel

import fakechisel.macros.SourceInfoTransform

abstract class Bits(val width: Option[Int]) extends BitsImpl {

  override def toString: String = s"Bits($width)"

  final def tail(n: Int): UInt = macro SourceInfoTransform.nArg

  final def head(n: Int): UInt = macro SourceInfoTransform.nArg

  final def apply(n: Int): UInt = macro SourceInfoTransform.nArg

  def do_tail(n: Int)(implicit info: FakeSourceInfo): UInt = _tailImpl(n)

  def do_head(n: Int)(implicit info: FakeSourceInfo): UInt = _headImpl(n)

  def do_apply(n: Int)(implicit info: FakeSourceInfo): UInt = _applyImpl(n)
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
