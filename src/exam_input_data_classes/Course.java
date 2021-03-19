package exam_input_data_classes;
import java.util.HashSet;

public class Course {
	String courseId;
	String courseName;
	HashSet<String> studentRollNumbers; //all students who are registered in this course. (irrespective of their sections)
	boolean toBeScheduled;
	
	Course(String courseId, String courseName, boolean toBeScheduled)
	{
		this.courseId=courseId;
		this.courseName=courseName;
		this.toBeScheduled=toBeScheduled;
		this.studentRollNumbers=new HashSet<String>();
	}

	
	Course(String courseId, String courseName, HashSet<String> studentRollNumbers)
	{
		this.courseId=courseId;
		this.courseName=courseName;
		this.studentRollNumbers=studentRollNumbers;
	}


	public String getCourseId() {
		return courseId;
	}


	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}


	public String getCourseName() {
		return courseName;
	}


	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	
	public HashSet<String> getStudentRollNumbers() {
		return studentRollNumbers;
	}

	public boolean getToBeScheduled()
	{
		return this.toBeScheduled;
	}
	
	public void setToBeScheduled(boolean toBeScheduled) {
		this.toBeScheduled=toBeScheduled;
	}
	
	public void setStudentRollNumbers(HashSet<String> studentRollNumbers) {
		this.studentRollNumbers = studentRollNumbers;
	}

}
