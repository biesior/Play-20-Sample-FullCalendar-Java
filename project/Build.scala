import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

  val appName = "FullCalendar"
  val appVersion = "1.1.2-Snapshot"

  val appDependencies = Seq(
    // Add your project dependencies here,
    "mysql" % "mysql-connector-java" % "5.1.18"
  )

  val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
    // Add your own project settings here
  )

}
