organization  := "com.herokuapp.ochimikan"

name          := "ochimikan-record"

version       := "0.0.1"

scalaVersion  := "2.11.2"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV  = "2.3.6"
  val sprayV = "1.3.2"
  Seq(
    "io.spray"          %% "spray-can"       % sprayV,
    "io.spray"          %% "spray-routing"   % sprayV,
    "io.spray"          %% "spray-testkit"   % sprayV % "test",
    "io.spray"          %% "spray-json"      % "1.3.1",
    "com.typesafe.akka" %% "akka-actor"      % akkaV,
    "com.typesafe.akka" %% "akka-testkit"    % akkaV % "test",
    "org.specs2"        %% "specs2-core"     % "2.3.13" % "test",
    "com.nimbusds"      %  "nimbus-jose-jwt" % "3.2",
    "org.mongodb"       %% "casbah-core"     % "2.7.3"
  )
}

Revolver.settings
