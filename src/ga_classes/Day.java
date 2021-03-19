package ga_classes;

public class Day {

	protected Slot []slots;
//	HashMap<String, String> coursesScheduled;
	
//	public Day()
//	{
//		this.coursesScheduled=new HashMap<String, String>();
//	}
	
//	public HashMap<String, String> getCoursesScheduled()
//	{
//		return this.coursesScheduled;
		
//	}
	
	public Day()
	{
		this.slots=null;
	}
	public Day(int slotCount)
	{
		this.slots=new Slot[slotCount];
	}
	
	public Slot[] getSlots()
	{
		return this.slots;
	}
	
}
