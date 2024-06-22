// https://typelevel.org/sbt-typelevel/faq.html#what-is-a-base-version-anyway
ThisBuild / tlBaseVersion := "0.15" // your current series x.y

ThisBuild / organization := "org.feijoas"
ThisBuild / organizationName := "bwignall"
ThisBuild / startYear := Some(2024)
ThisBuild / licenses := Seq(License.Apache2)
ThisBuild / developers := List(
  // your GitHub handle and name
  tlGitHubDev("bwignall", "Brian Wignall")
)

// publish website from this branch
ThisBuild / githubWorkflowPublishTargetBranches := Seq()

// dependency tracking
ThisBuild / tlCiDependencyGraphJob := true

// generate documentation
ThisBuild / tlCiDocCheck := true

// Not currently bothering to generate/check headers
ThisBuild / tlCiHeaderCheck := false

/** Scala */
val Scala212 = "2.12.19"
val Scala213 = "2.13.13"
val Scala3 = "3.3.3"
ThisBuild / crossScalaVersions := Seq(Scala213)
ThisBuild / scalaVersion := Scala213 // the default Scala

/** Library Dependencies */

// Versions:
val GuavaVersion = "33.2.1-jre"
val findbugsV = "3.0.2"
val junitV = "5.10.2"
val mockitoV = "5.11.0"
val scalacheckV = "3.2.18.0"
val scalamockV = "6.0.0"
val scalatestV = "3.2.18"

// compile dependencies
val guava = "com.google.guava" % "guava" % GuavaVersion
val findbugs = "com.google.code.findbugs" % "jsr305" % findbugsV

lazy val commonJvmSettings = Seq(
  libraryDependencies ++= Seq(
    guava,
    findbugs
  )
)

// test dependencies
val guavaTestlib = "com.google.guava" % "guava-testlib" % GuavaVersion % Test
val junitApi = "org.junit.jupiter" % "junit-jupiter-api" % junitV % Test
val junitEngine = "org.junit.jupiter" % "junit-jupiter-engine" % junitV % Test
val scalatest = "org.scalatest" %% "scalatest" % scalatestV % Test
val scalactic = "org.scalactic" %% "scalactic" % scalatestV
val scalacheck = "org.scalatestplus" %% "scalacheck-1-18" % scalacheckV % Test
//val scalacheck = "org.scalacheck" %% "scalacheck" % scalacheckV % Test
val scalamock = "org.scalamock" %% "scalamock" % scalamockV % Test
//val mockito = "org.mockito" % "mockito-core" % mockitoV % Test
val mockito = "org.scalatestplus" %% "mockito-5-10" % scalacheckV % "test"

lazy val testingDependencies = Seq(
  libraryDependencies ++= Seq(
    guavaTestlib,
    junitApi,
    junitEngine,
    scalatest,
    scalactic,
    scalacheck,
    scalamock,
    mockito
  )
)

lazy val root = tlCrossRootProject.aggregate(core)

lazy val core = crossProject(JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(
    name := "mango"
  )
  .settings(testingDependencies)
  .jvmSettings(commonJvmSettings)
