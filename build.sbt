name := """hoppR"""

version := "0.1"

scalaVersion := "2.11.1"

mainClass in Compile := Some("HelloHoppr")

resolvers += "spray repo" at "http://repo.spray.io"

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= List(
  "com.github.tototoshi"    %% "slick-joda-mapper"   % "1.2.0",
  "com.novocode"             % "junit-interface"     % "0.10",
  "com.typesafe.akka" % "akka-actor_2.11" % "2.3.8",
  "com.typesafe.akka" % "akka-slf4j_2.11" % "2.3.8",
  "com.typesafe.akka" % "akka-testkit_2.11" % "2.3.8",
  "com.typesafe.slick"      %% "slick"               % "2.1.0",
  "io.spray"                 % "spray-can_2.11"      % "1.3.1",
  "io.spray"                 % "spray-routing_2.11"  % "1.3.1",
  "io.spray"                 % "spray-json_2.11"     % "1.2.6",
  "io.spray"                 % "spray-testkit_2.11"  % "1.3.1",
  "joda-time"                % "joda-time"           % "2.4",
  "net.databinder.dispatch"  % "dispatch-core_2.11"  % "0.11.1",
  "org.joda"                 % "joda-convert"        % "1.6",
  "org.scalaj"              %% "scalaj-http"         % "0.3.16",
  "org.scalatest"           %% "scalatest"           % "2.1.6"             % "test",
  "org.slf4j"                % "slf4j-nop"           % "1.6.4",
  "org.seleniumhq.webdriver" % "webdriver-selenium"  % "0.9.7376",
  "org.seleniumhq.webdriver" % "webdriver-htmlunit"  % "0.9.7376",
  "org.specs2"               % "specs2_2.11"         % "2.4",
  "postgresql"               % "postgresql"          % "9.1-901.jdbc4",
  "org.json4s"              %% "json4s-native"       % "3.2.10",
  "rome"                     % "rome"                % "1.0"
)

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-Ywarn-dead-code",
  "-language:_",
  "-target:jvm-1.7",
  "-encoding", "UTF-8"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")

parallelExecution in Test := false