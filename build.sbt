// Build file for Scala and Chisel projects

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)


lazy val chisel = (project in file("chisel3"))

lazy val root = (project in file("."))
  .dependsOn(chisel)
  .settings(
    organization := "FreeChips",
    name := "chisel-project",
    version := "0.0.1",
    scalaVersion := "2.12.10",
    maxErrors := 3,
    libraryDependencies ++= Seq(
      "edu.berkeley.cs" %% "chisel3"         % "3.4-SNAPSHOT",
      "edu.berkeley.cs" %% "chisel-testers2" % "0.1-SNAPSHOT",
      "edu.berkeley.cs" %% "chisel-iotesters"% "1.5-SNAPSHOT"
    ),
    fork := true
  )

// Refine scalac params from tpolecat
scalacOptions --= Seq(
  "-Xfatal-warnings"
)
scalacOptions ++= Seq(
  "-Xsource:2.11"
)
scalacOptions += "-language:reflectiveCalls"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)

// Aliases
addCommandAlias("com", "all compile test:compile")
addCommandAlias("rel", "reload")
addCommandAlias("fix", "all compile:scalafix test:scalafix")
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll")
