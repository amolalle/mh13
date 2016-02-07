/**
 * 
 */
package com.mh13.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

/**
 * @author amola
 *
 */
public class SparkSample {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setAppName("first_spark").setMaster("local");
		JavaSparkContext sc = new JavaSparkContext(conf);
		JavaRDD<String> lines =  sc.textFile("/Tools/spark.txt");
		lines.map(new Function<String, Integer>() {
			public Integer call(String line) throws Exception {
				return Integer.parseInt(line)*100;
			}
		}).saveAsTextFile("file:///Tools/spark_out.txt");

	}
	
}
