name := "bundle-exercise"

organization := "com.andyczerwonka"

version := "0.0.1"

scalaVersion := "2.12.1"

resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.0.1",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test" withSources() withJavadoc(),
  "org.scalacheck" %% "scalacheck" % "1.13.4" % "test" withSources() withJavadoc()
)

initialCommands := "import com.andyczerwonka.bundleexercise._"
