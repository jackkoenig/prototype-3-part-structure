// SPDX-License-Identifier: Apache-2.0
// This is simplified version taken from chipsalliance/chisel (Apache 2.0 licensed) for the example:

package fakechisel.macros

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

class SourceInfoTransform(val c: Context) {

  import c.universe._
  def thisObj: Tree = c.prefix.tree
  def implicitSourceInfo = q"""fakechisel.FakeSourceInfo("blah")"""

  /** Returns the TermName of the transformed function, which is the applied function name with do_
    * prepended.
    */
  def doFuncTerm: TermName = {
    val funcName = c.macroApplication match {
      case q"$_.$funcName[..$_](...$_)" => funcName
      case _ =>
        throw new Exception(
          s"Could not resolve function name from macro application: ${showCode(c.macroApplication)}"
        )
    }
    TermName("do_" + funcName)
  }

  def nArg(n: c.Tree): c.Tree = {
    q"$thisObj.$doFuncTerm($n)($implicitSourceInfo)"
  }
}
