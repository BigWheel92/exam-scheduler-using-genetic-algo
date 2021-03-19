package ga_classes;
import java.util.HashSet;

public class Slot{
	
	HashSet<String> coursesScheduled;

	public Slot()
	{
		this.coursesScheduled=new HashSet<String>();
	}
	
	public HashSet<String> getCoursesScheduled()
	{
		return this.coursesScheduled;
		
	}
	
}
