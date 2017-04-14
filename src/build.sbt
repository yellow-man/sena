name := """sena-batch"""

version := "1.2.0-1.3"

lazy val core = (project in file("modules/sena-core/src")).enablePlugins(PlayJava).settings(javacOptions in (Compile,doc) += "-Xdoclit:none")

lazy val root = (project in file(".")).enablePlugins(PlayJava).dependsOn(core).aggregate(core).settings(javacOptions in (Compile,doc) ++= Seq("-notimestamp", "-linksource"))

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "args4j"    % "args4j" % "2.33",
  "org.jsoup" % "jsoup"  % "1.9.1"
)
