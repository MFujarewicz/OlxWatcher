ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.6"

lazy val root = (project in file("."))
  .settings(
    name := "OlxWatcher"
  )


libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "2.2.1"
libraryDependencies += "com.twilio.sdk" % "twilio" % "8.28.0"
