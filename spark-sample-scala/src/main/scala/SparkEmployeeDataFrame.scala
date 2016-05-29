import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

case class Employee(id: Int,dob: String,name: String,gender: String,doj: String)

case class Salary(id: Int,amount: Int,fromDate: String,toDate: String)

//pair rdd join example
object SparkEmployeeDataFrame extends App{
	val conf = new SparkConf().setAppName("spark-employee-app");
	val sc = new SparkContext(conf)
	println("SparkContext created")

	val employeeFile = sc.textFile("file:///Users/amol/tmp/data/employees/all_employees.csv")

	val salariesFile = sc.textFile("file:///Users/amol/tmp/data/employees/all_salaries.csv")

	//create employees data frames
	val allEmployees = employeeFile.map(row => {val fields = row.split(",")
		Employee(fields(0).toInt,fields(1),fields(2),fields(4),fields(5))
		}).toDF()
	
	//create salaries data frames						 
	val salaries = salariesFile.map(row => {val fields = row.split(",")
		Salary(fields(0).toInt,fields(1).toInt,fields(2),fields(3))
		})

	//use group by operation on data frames.
	//should be followed by aggregation operator
	salaries.groupBy(salaries("id")).sum("amount").show()

	

	
}