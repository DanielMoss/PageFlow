name := "PageFlow"

version := "0.1"

scalaVersion := "2.13.1"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.3.3",
  "org.jgrapht" % "jgrapht-core" % "1.3.0",
  "org.jgrapht" % "jgrapht-io" % "1.3.0"
)

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full)
