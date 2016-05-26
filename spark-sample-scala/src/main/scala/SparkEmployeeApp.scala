import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

case class Employee(id: Int,dob: String,name: String,gender: String,doj: String)

case class Salary(id: Int,amount: Int,fromDate: String,toDate: String)

//pair rdd join example
object SparkEmployeeApp extends App{
	val conf = new SparkConf().setAppName("spark-employee-app");
	val sc = new SparkContext(conf)
	println("SparkContext created")

	val employeeFile = sc.textFile("file:///Users/amol/tmp/data/employees/all_employees.csv")

	val salariesFile = sc.textFile("file:///Users/amol/tmp/data/employees/all_salaries.csv")

	//create pair rdd with (empid,Employee)
	val allEmployees = employeeFile.map(row => {val fields = row.split(",")
		(fields(0).toInt,Employee(fields(0).toInt,fields(1),fields(2),fields(4),fields(5)))
		})
	//create a pair rdd with (empid,Salary)							 
	val salaries = salariesFile.map(row => {val fields = row.split(",")
		(fields(0).toInt,Salary(fields(0).toInt,fields(1).toInt,fields(2),fields(3)))
		})

	//join emoloyees with salaries
	val empSalaries = allEmployees.join(salaries)

	empSalaries.persist()

	//sum all salary amount for each employee
	val empTotalSalaries = empSalaries.reduceByKey( (x,y) => (x._1,Salary(x._1.id,(x._2.amount + y._2.amount),"","")) )

	println(empTotalSalaries.lookup(266991)(0)._2.amount)
	
}