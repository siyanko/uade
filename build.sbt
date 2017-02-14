
scalaVersion := "2.11.8"
    
// The Play project itself
lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """uade"""
  )
