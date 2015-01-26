name := """school"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
	.enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "jp.t2v" %% "play2-auth"      % "0.13.0",
  "jp.t2v" %% "play2-auth-test" % "0.13.0" % "test",
  "org.scalikejdbc" %% "scalikejdbc"       % "2.2.1",
  "com.h2database"  %  "h2"                % "1.4.184",
  "ch.qos.logback"  %  "logback-classic"   % "1.1.2",
  "mysql"           % "mysql-connector-java" % "5.1.21",
  "org.webjars"          %% "webjars-play"                  % "2.3.0",
  "org.webjars"          %  "bootstrap"                     % "3.2.0",
  "org.reactivemongo"    %% "play2-reactivemongo"           % "0.10.5.0.akka23",
  "org.mindrot"          % "jbcrypt"                        % "0.3m"
)
