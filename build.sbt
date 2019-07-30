name := "pubnative-data-engineering"

version := "1.0"

scalaVersion := "2.12.6"

mainClass in (Compile, packageBin) := Some("net.pubnative.Main")

lazy val akkaVersion = "2.6.0-M5"
lazy val sprayVersion = "1.3.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "io.spray" %% "spray-json" % sprayVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)
