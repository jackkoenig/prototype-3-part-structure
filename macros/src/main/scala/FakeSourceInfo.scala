
package fakechisel

case class FakeSourceInfo(value: String)

object FakeSourceInfo {
  implicit val default: FakeSourceInfo = new FakeSourceInfo("blah")
}
