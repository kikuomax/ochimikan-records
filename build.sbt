organization  := "com.herokuapp.ochimikan"

name          := "ochimikan-record"

version       := "0.0.1"

scalaVersion  := "2.11.4"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV  = "2.3.6"
  val sprayV = "1.3.2"
  val log4jV = "2.1"
  Seq(
    "io.spray"                 %% "spray-can"        % sprayV,
    "io.spray"                 %% "spray-routing"    % sprayV,
    "io.spray"                 %% "spray-testkit"    % sprayV % "test",
    "io.spray"                 %% "spray-json"       % "1.3.1",
    "com.typesafe.akka"        %% "akka-actor"       % akkaV,
    "com.typesafe.akka"        %% "akka-testkit"     % akkaV % "test",
    "com.typesafe.akka"        %% "akka-slf4j"       % akkaV,
    "org.specs2"               %% "specs2-core"      % "2.3.13" % "test",
    "com.nimbusds"             %  "nimbus-jose-jwt"  % "3.2",
    "org.mongodb"              %% "casbah-core"      % "2.7.3",
    "com.typesafe"             %  "config"           % "1.2.1",
    "org.apache.logging.log4j" %  "log4j-api"        % log4jV,
    "org.apache.logging.log4j" %  "log4j-core"       % log4jV,
    "org.apache.logging.log4j" %  "log4j-slf4j-impl" % log4jV
  )
}

Revolver.settings
