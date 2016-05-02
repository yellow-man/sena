name := """sena"""

version := "0.0.1"

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
  "net.sf.opencsv"            % "opencsv"              % "2.3",
  "org.jsoup"                 % "jsoup"                % "1.9.1",
  "org.apache.commons"        % "commons-email"        % "1.4",
  "commons-io"                % "commons-io"           % "2.5"
)
