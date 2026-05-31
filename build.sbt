ThisBuild / organization := "learnut"
ThisBuild / version := "1.0.0"
ThisBuild / scalaVersion  := "3.8.3"
ThisBuild / scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-unchecked",
    "-Wunused:all",
    "-Xfatal-warnings")

lazy val root = project
    .in(file("."))
    .settings(name := "learnut" )