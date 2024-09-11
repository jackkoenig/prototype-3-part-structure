
package fakechisel

import fakechisel.macros.SourceInfoTransform

class UInt(width: Option[Int]) extends Bits(width) with UIntImpl {
  final def +(n: UInt): UInt = macro SourceInfoTransform.nArg

  def do_+(n: UInt)(implicit info: FakeSourceInfo): UInt = _plusImpl(n)

  final def -(n: UInt): UInt = macro SourceInfoTransform.nArg

  def do_-(n: UInt)(implicit info: FakeSourceInfo): UInt = _minusImpl(n)
}
