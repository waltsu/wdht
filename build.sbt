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
resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"


ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

libraryDependencies ++= Seq(
  "joda-time" % "joda-time" % "2.8.1",
  "com.tumblr" %% "colossus" % "0.8.1",
  "com.typesafe.akka" %% "akka-actor" % "2.4.11",
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "org.postgresql" % "postgresql" % "9.4.1211.jre7",
  "com.typesafe" % "config" % "1.3.1",
  "org.flywaydb" % "flyway-core" % "4.0.3",
  "com.typesafe.play" % "play-json_2.11" % "2.5.10",

  "org.specs2" %% "specs2-core" % "3.8.5" % "test",
  "org.specs2" %% "specs2-mock" % "3.8.5" % "test",
  "org.specs2" %% "specs2-junit" % "3.8.5" % "test",
  "com.tumblr" %% "colossus-testkit" % "0.8.1" % "test"
)

fork in Test := false
sbt.Keys.fork in Test := false
fork in run := true

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

// Disable ScalaDoc documentation generation
sources in (Compile,doc) := Seq.empty

// Experimental sbt feature which should make resolving dependencies much faster. See http://www.scala-sbt.org/1.0/docs/Cached-Resolution.html.
updateOptions := updateOptions.value.withCachedResolution(true)
