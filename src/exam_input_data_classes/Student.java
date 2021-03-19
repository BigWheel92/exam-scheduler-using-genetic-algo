package exam_input_data_classes;
import java.util.HashSet;

public class Student {

	private String rollNumber;
	private String name;
	private String degree;
	private HashSet<String> courses;

	public Student(String rollNumber, String name, String degree) {
		super();
		this.rollNumber = rollNumber;
		this.name = name;
		this.degree = degree;
		this.courses = new HashSet<String>();
	}

	public Student(String rollNumber, String name, String degree,
		HashSet<String> courses) {
		super();
		this.rollNumber = rollNumber;
		this.name = name;
		this.degree = degree;

		this.courses = courses;
	}

	public String getRollNumber() {
		return rollNumber;
	}

	public void setRollNumber(String rollNumber) {
		this.rollNumber = rollNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}


	public HashSet<String> getCourses() {
		return courses;
	}

	public void setCourses(HashSet<String> courses) {
		this.courses = courses;
	}

}
