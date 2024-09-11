
package fakechisel

class UInt(width: Option[Int]) extends Bits(width) with UIntImpl {

  final def +(n: UInt)(using FakeSourceInfo): UInt = _plusImpl(n)

  final def -(n: UInt)(using FakeSourceInfo): UInt = _minusImpl(n)
}
