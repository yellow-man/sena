name := """sena"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "mysql"                     % "mysql-connector-java" % "5.1.38",
  "args4j"                    % "args4j"               % "2.33",
  "org.apache.httpcomponents" % "httpclient"           % "4.5.2",
  "net.sf.opencsv"            % "opencsv"              % "2.3"
)
