import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

object SparkSample extends App{
	val conf = new SparkConf().setAppName("spark-sample-app");
	val sc = new SparkContext(conf)
	println("SparkContext created")

	val lines = sc.textFile("file:///Users/amol/tmp/data/sample.txt")

	val words = lines.flatMap(_.split(" ")) 

	val counts = words.map(word => (word,1)).reduceByKey{case (x,y) => x+y}

	counts.saveAsTextFile("file:///Users/amol/tmp/data/sample_output.txt")
}