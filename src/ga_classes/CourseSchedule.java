package ga_classes;

public class CourseSchedule {
	int slot;
	int day;
	String courseId;
	
	public CourseSchedule(String courseId, int slot, int day)
	{
		this.courseId=courseId;
		this.slot=slot;
		this.day=day;
	}
	
	public CourseSchedule(CourseSchedule that)
	{
		this.slot=that.slot;
		this.day=that.day;
		this.courseId=that.courseId;
	}
	
	public int getSlot()
	{
		return this.slot;
	}
	
	public int getDay()
	{
		return this.day;
	}
	
	public String getCourseId()
	{
		return this.courseId;
	}
}
