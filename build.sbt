/** Organization */
ThisBuild / organization := "org.feijoas"

/** Library Meta */
ThisBuild / tlBaseVersion := "0.14" // your current series x.y

/** Scala */
val Scala213 = "2.13.13"
val Scala3 = "3.3.3"
ThisBuild / crossScalaVersions := Seq(Scala213, Scala3)
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

// test dependencies
val guavaTestlib = "com.google.guava" % "guava-testlib" % GuavaVersion % Test
val junit = "junit" % "junit" % junitV % Test
val scalatest = "org.scalatest" %% "scalatest" % scalatestV % Test
val scalactic = "org.scalactic" %% "scalactic" % scalatestV
val scalacheck = "org.scalatestplus" %% "scalacheck-1-18" % scalacheckV % Test
//val scalacheck = "org.scalacheck" %% "scalacheck" % scalacheckV % Test
val scalamock = "org.scalamock" %% "scalamock" % scalamockV % Test
//val mockito = "org.mockito" % "mockito-core" % mockitoV % Test
val mockito = "org.scalatestplus" %% "mockito-5-10" % scalacheckV % "test"

//lazy val root = tlCrossRootProject.aggregate(core)

lazy val core = // crossProject(JVMPlatform)
  // .crossType(CrossType.Pure)
  project
    .in(file("."))
    .settings(
      name := "mango",
      libraryDependencies ++= Seq(
        guava,
        findbugs,
        guavaTestlib,
        "org.junit.jupiter" % "junit-jupiter-api" % junitV % Test,
        "org.junit.jupiter" % "junit-jupiter-engine" % junitV % Test,
        scalatest,
        scalactic,
        scalacheck,
        scalamock,
        mockito
      )
    )
