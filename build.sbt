import com.trueaccord.scalapb.{ScalaPbPlugin => PB}
import com.typesafe.sbt.SbtSite.SiteKeys._
import com.typesafe.sbt.SbtGhPages.GhPagesKeys._

scalaVersion in ThisBuild := "2.11.8"

lazy val cross = project.in(file(".")).
  aggregate(ds3arJS, ds3arJVM)
  .settings(site.settings)
  .settings(ghpages.settings)
  .settings(
    addCommandAlias("ssite", "; ds3arJVM/run ; fastOptJS"),
    addCommandAlias("ssite-pub", "; ds3arJVM/run ; fullOptJS ; ghpages-push-site"),
    publish := {},
    publishLocal := {},
    git.remoteRepo := "git@github.com:Tzbob/ds3-ar.git",
    includeFilter in makeSite := "*"
  )

lazy val protobufSource = sourceDirectory in PB.protobufConfig := file("shared/src/main/protobuf")
lazy val protobufIncludePath = PB.includePaths in PB.protobufConfig := Seq(file("shared/src/main/protobuf"))
lazy val protobufRunCommand = PB.runProtoc in PB.protobufConfig := (args => com.github.os72.protocjar.Protoc.runProtoc("-v300" +: args.toArray))
lazy val protobufVersion = (version in PB.protobufConfig := "3.0.0-beta-1")
lazy val protobufSettings = PB.protobufSettings :+ protobufSource :+ protobufIncludePath :+ protobufRunCommand:+ protobufVersion

lazy val ds3ar = crossProject.in(file("."))
  .settings(
    organization := "be.tzbob",
    name := "ds3ar",
    autoCompilerPlugins := true,

    unmanagedSourceDirectories in Compile += file("src/site/"),

    scalaJSOutputWrapper := ("", "ds3ar.ui.Main().main();"),

    resolvers += Resolver.sonatypeRepo("releases"),
    resolvers += Resolver.sonatypeRepo("snapshots"),
    resolvers += Resolver.bintrayRepo("stacycurl", "repo"),

    scalacOptions ++= Seq(
      "-encoding", "UTF-8",
      "-target:jvm-1.6",
      "-feature",
      "-deprecation",
      "-Xlint",
      "-Yinline-warnings",
      "-Yno-adapted-args",
      "-Ywarn-dead-code",
      // "-Ywarn-numeric-widen",
      "-Ywarn-value-discard",
      "-Xfuture",
      "-language:higherKinds"
    ),

    libraryDependencies ++= Seq(
      "org.typelevel" %%% "cats" % "0.5.0",
      "com.lihaoyi" %%% "scalatags" % "0.5.5",
      "com.chuusai" %% "shapeless" % "2.3.1",
      "be.tzbob" %%% "hokko" % "0.2.0-SNAPSHOT",
      "org.scalatest" %%% "scalatest" % "3.0.0-M10" % "test"
    )
  )
  .jvmSettings(protobufSettings: _*)
  .jvmSettings(
    libraryDependencies ++= Seq(
      "com.github.stacycurl" %% "delta-generic" % "1.0.18",
      "com.nrinaudo" %% "kantan.csv-generic" % "0.1.10"
    )
  )
  .jsSettings(protobufSettings: _*)
  .jsSettings(
    libraryDependencies ++= Seq(
      "com.trueaccord.scalapb" %%% "scalapb-runtime" % "0.5.16",
      "com.trueaccord.scalapb" %%% "scalapb-runtime" % "0.5.16" % PB.protobufConfig
    )
  )

// Needed, so sbt finds the projects
lazy val ds3arJVM = ds3ar.jvm
lazy val ds3arJS = ds3ar.js.settings(
  artifactPath in Compile in fastOptJS :=
    file("src/site/main-fastopt.js"),
  artifactPath in Compile in fullOptJS :=
    file("src/site/main-fullopt.js")
)
