name := "SlickPersistence"

version := "0.1"

scalaVersion := "2.12.7"

libraryDependencies += "org.jsoup" % "jsoup" % "1.11.3"
libraryDependencies += "com.typesafe.slick" %% "slick" % "3.2.1"
libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.25"
libraryDependencies += "com.typesafe.slick" %% "slick-hikaricp" % "3.2.1"
libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.14"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.4.2",
  "org.apache.spark" %% "spark-sql" % "2.4.2",
  "org.apache.spark" %% "spark-streaming" % "2.4.2",
  "org.apache.spark" %% "spark-mllib" % "2.4.2"
)