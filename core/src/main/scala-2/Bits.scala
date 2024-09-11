
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
