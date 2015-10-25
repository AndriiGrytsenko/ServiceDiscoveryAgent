lazy val commonSettings = Seq(
  organization := "org.myproject",
  version := "0.1.0",
  // set the Scala version used for the project
  scalaVersion := "2.11.5"
)

name := "serviceDiscoveryAgent"

version := "1.0"

scalaVersion := "2.11.7"

resolvers += "twttr" at "https://maven.twttr.com/"

libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.6.3"

libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.3"

libraryDependencies += "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.6.1"

libraryDependencies += "com.twitter" %% "twitter-server" % "1.15.0"

libraryDependencies += "com.twitter" %% "util-zk" % "6.29.0"

//libraryDependencies += "com.twitter" %% "finagle-stats" % "6.29.0"




