name := """wdht"""

version := "0.0.1"

scalaVersion := "2.11.8"

externalResolvers := externalResolvers.value.filter(! _.toString.contains("https://repo.typesafe.com/typesafe/releases"))

externalResolvers ++= Seq(
  Resolver.jcenterRepo,
  Resolver.typesafeRepo("releases")
)

resolvers += Resolver.url("Typesafe Ivy releases", url("https://repo.typesafe.com/typesafe/ivy-releases"))(Resolver.ivyStylePatterns)
resolvers += "Maven central" at "http://repo1.maven.org/maven2/"

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

libraryDependencies ++= Seq(
  "joda-time" % "joda-time" % "2.8.1"
)

fork in Test := false
sbt.Keys.fork in Test := false
fork in run := true

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

// Disable ScalaDoc documentation generation
sources in (Compile,doc) := Seq.empty

// Experimental sbt feature which should make resolving dependencies much faster. See http://www.scala-sbt.org/1.0/docs/Cached-Resolution.html.
updateOptions := updateOptions.value.withCachedResolution(true)
