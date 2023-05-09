ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

libraryDependencies += "org.scalafx" %% "scalafx" % "16.0.0-R24"
libraryDependencies ++= {
  // Determine OS version of JavaFX binaries
  lazy val osName = System.getProperty("os.name") match {
    case n if n.startsWith("Linux") => "linux"
    case n if n.startsWith("Mac") => "mac"
    case n if n.startsWith("Windows") => "win"
    case _ => throw new Exception("Unknown platform!")
  }
  Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
    .map(m => "org.openjfx" % s"javafx-$m" % "16" classifier osName)
}

libraryDependencies ++= Seq(

  // Start with this one
  "org.tpolecat" %% "doobie-core"      % "1.0.0-RC1",

  // And add any of these as needed
  "org.tpolecat" %% "doobie-h2"        % "1.0.0-RC1",          // H2 driver 1.4.200 + type mappings.
  "org.tpolecat" %% "doobie-hikari"    % "1.0.0-RC1",          // HikariCP transactor.
  "org.tpolecat" %% "doobie-postgres"  % "1.0.0-RC1",          // Postgres driver 42.3.1 + type mappings.
  "org.tpolecat" %% "doobie-specs2"    % "1.0.0-RC1" % "test", // Specs2 support for typechecking statements.
  "org.tpolecat" %% "doobie-scalatest" % "1.0.0-RC1" % "test"  // ScalaTest support for typechecking statements.

)

libraryDependencies += "com.typesafe" % "config" % "1.3.1"

val http4sVersion      = "0.23.18"

libraryDependencies ++=Seq(
  "org.http4s"               %% "http4s-dsl"                    % http4sVersion,
  "org.http4s"               %% "http4s-ember-server"           % http4sVersion,
  "org.http4s"               %% "http4s-ember-client"           % http4sVersion,
  "org.http4s"               %% "http4s-circe"                  % http4sVersion,
  "org.http4s"               %% "http4s-jdk-http-client"        % "0.9.0",
)

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime




lazy val root = (project in file("."))
  .settings(
    name := "ProjectTetris"
  )
