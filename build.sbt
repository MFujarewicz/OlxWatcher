ThisBuild / version := "1.0.0-mlody"

ThisBuild / scalaVersion := "2.13.6"

lazy val root = (project in file("."))
  .settings(
    name := "OlxWatcher",
    assembly / assemblyJarName := "OlxWatcher.jar",
  )

assembly / mainClass := Some("Main")

libraryDependencies += "com.twilio.sdk" % "twilio" % "9.2.5"
libraryDependencies += "com.softwaremill.sttp.client4" %% "core" % "4.0.0-M4"
libraryDependencies += "com.github.pureconfig" %% "pureconfig" % "0.17.4"



ThisBuild / assemblyMergeStrategy := {
  case PathList("javax", "servlet", xs@_*) => MergeStrategy.first
  case PathList(ps@_*) if ps.last endsWith ".html" => MergeStrategy.first
  case "application.conf" => MergeStrategy.concat
  case "module-info.class" => MergeStrategy.discard
  case PathList("META-INF", _*) => MergeStrategy.discard
  case x =>
    val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
    oldStrategy(x)
}
