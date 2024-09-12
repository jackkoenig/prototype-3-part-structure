package fakechisel

abstract class Bits(val width: Option[Int]) extends BitsImpl {

  final def tail(n: Int)(using FakeSourceInfo): UInt = _tailImpl(n)

  final def head(n: Int)(using FakeSourceInfo): UInt = _headImpl(n)

  final def apply(n: Int)(using FakeSourceInfo): UInt = _applyImpl(n)
}
