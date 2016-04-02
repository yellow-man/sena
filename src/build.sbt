name := """sena"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  // mysql5.5.x ----------
  "mysql" % "mysql-connector-java" % "5.1.38"
)
