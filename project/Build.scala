import sbt._
import Keys._

object BeanDiffBuild extends Build {
    
  val JavaDoc = config("genjavadoc") extend Compile

  val javadocSettings = inConfig(JavaDoc)(Defaults.configSettings) ++ Seq(
    libraryDependencies += compilerPlugin("com.typesafe.genjavadoc" %% "genjavadoc-plugin" % "0.4" cross CrossVersion.full),
    scalacOptions <+= target map (t => "-P:genjavadoc:out=" + (t / "java")),
    packageDoc in Compile <<= packageDoc in JavaDoc,
    sources in JavaDoc <<=
      (target, compile in Compile, sources in Compile) map ((t, c, s) =>
        (t / "java" ** "*.java").get ++ s.filter(_.getName.endsWith(".java"))),
    javacOptions in JavaDoc := Seq(),
    artifactName in packageDoc in JavaDoc :=
      ((sv, mod, art) =>
        "" + mod.name + "_" + sv.binary + "-" + mod.revision + "-javadoc.jar")
  )
  
  lazy val root = Project("beandiff", file(".")).settings(javadocSettings: _*)
//      addSbtPlugin("com.github.shivawu" % "sbt-maven-plugin" % "0.1.0")
//  )

}
