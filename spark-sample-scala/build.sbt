name := "spark-sample"
version := "0.0.1"
libraryDependencies ++= Seq("org.apache.spark" % "spark-core_2.10" % "1.6.1" % "provided")


jarName in assembly := "spark-sample.jar"

assemblyOption in assembly :=  (assemblyOption in assembly).value.copy(includeScala = false)

