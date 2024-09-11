import mill._
import mill.scalalib._
import mill.scalalib.publish._
import mill.scalalib.scalafmt._
import mill.define.Cross

object v {
  val scalaCrossVersions = Seq(
    "2.13.14",
    "3.3.3"
  )

  def scalaReflect(scalaVersion: String): Agg[Dep] =
    if (scalaVersion.startsWith("2")) Agg(ivy"org.scala-lang:scala-reflect:$scalaVersion")
    else Agg.empty[Dep]
}

object macros extends Cross[Macros](v.scalaCrossVersions)

trait Macros extends CrossSbtModule {

  override def ivyDeps = T { v.scalaReflect(crossScalaVersion) }
}

object core extends Cross[Core](v.scalaCrossVersions)

trait Core extends CrossSbtModule {
  override def scalacOptions = T { Seq("-language:experimental.macros") }
  override def moduleDeps = Seq(macros(crossScalaVersion))
}
