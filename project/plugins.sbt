addSbtPlugin("org.scalameta"             % "sbt-scalafmt" % "2.0.7")
addSbtPlugin("ch.epfl.scala"             % "sbt-scalafix" % "0.9.7-1")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.1.8")

resolvers ++= Seq(
  Resolver.bintrayIvyRepo("epfl-lara", "sbt-plugins"),
  Resolver.bintrayRepo("epfl-lara", "princess"),
)

val StainlessVersion = "0.7.4"

addSbtPlugin("ch.epfl.lara" % "sbt-stainless" % StainlessVersion)

