package ga_classes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import exam_input_data_classes.*;

public class Chromosome {

	private Day[] days;
	private HashMap<String, CourseSchedule> courseSchedule; //the key is course id. We get the CourseSchedule object
													//for each key. This object has slot number and date
													//on which the course of given key(courseid) is scheduled.
	private int fitness;
	private int unfitness;

	public String toString(ArrayList<String> coursesToBeScheduled)
	{
		StringBuilder string=new StringBuilder();
		for (int i=0; i<coursesToBeScheduled.size(); i++)
		{
			CourseSchedule current=courseSchedule.get(coursesToBeScheduled.get(i));
			string.append(String.valueOf(current.getDay())+ String.valueOf(current.getSlot()));
		}
		return string.toString();
	}
	
	//cross over constructor
	public Chromosome(Chromosome p1, Chromosome p2, Random random, ProblemData problemData)
	{
		this.fitness = 0;
		this.unfitness = 0;
		this.days = null;
		this.courseSchedule = null;
		
		int totalDays = problemData.getTotalDays();
		int totalSlots = problemData.getTotalSlotsPerDay();
		int totalCoursesToSchedule = problemData.getCoursesToBeScheduled().size();

		this.days = new Day[totalDays];
		for (int i = 0; i < totalDays; i++)
		{
			this.days[i]=new Day();
			this.days[i].slots = new Slot[totalSlots];
			for (int j=0; j<totalSlots; j++)
				this.days[i].slots[j]=new Slot();
		}

		this.courseSchedule = new HashMap<String, CourseSchedule>();


		int crossOverPoint = random.nextInt(totalCoursesToSchedule);
		Chromosome firstParent = null;
		Chromosome secondParent = null;

		//deciding which parent will be the first and which will be second during crossover
		int randomNo = random.nextInt(2);
		if (randomNo == 0)
		{
			firstParent = p1;
			secondParent = p2;
		}
		else
		{
			firstParent = p2;
			secondParent = p1;
		}

		ArrayList<String> coursesToBeScheduled = problemData.getCoursesToBeScheduled();

		for (int i = 0; i <= crossOverPoint; i++)
		{
			String courseIdOfNextCourse = coursesToBeScheduled.get(i);
			CourseSchedule cs = new CourseSchedule(firstParent.courseSchedule.get(courseIdOfNextCourse));
			this.courseSchedule.put(courseIdOfNextCourse, cs);

			this.days[cs.day].slots[cs.slot].coursesScheduled.add(courseIdOfNextCourse);

		}

		for (int i = crossOverPoint + 1; i < totalCoursesToSchedule; i++)
		{
			String courseIdOfNextCourse = coursesToBeScheduled.get(i);
			CourseSchedule cs = new CourseSchedule(secondParent.courseSchedule.get(courseIdOfNextCourse));
			this.courseSchedule.put(courseIdOfNextCourse, cs);

			this.days[cs.day].slots[cs.slot].coursesScheduled.add(courseIdOfNextCourse);
		}
	}

	//randomly create a schedule
	public Chromosome(Random random, ProblemData problemData)
	{
		int totalCourses = problemData.getCoursesToBeScheduled().size();

		this.days = new Day[problemData.getTotalDays()];
		for (int i = 0; i < problemData.getTotalDays(); i++)
		{
			this.days[i]=new Day();
			this.days[i].slots = new Slot[problemData.getTotalSlotsPerDay()];
			for (int j=0; j<problemData.getTotalSlotsPerDay(); j++)
				this.days[i].slots[j]=new Slot();
		}

		this.courseSchedule = new HashMap<String, CourseSchedule>();

		for (int i = 0; i < totalCourses; i++)
		{
			String courseId = problemData.getCoursesToBeScheduled().get(i);
			int dayNumber=-1;
			int slotNumber=-1;
			if (problemData.getPreferences().containsKey(courseId))
			{
				
				dayNumber=problemData.getPreferences().get(courseId).getDay();
				slotNumber=problemData.getPreferences().get(courseId).getSlot();
				//System.out.println(courseId+"! "+dayNumber+"! "+slotNumber);
			}
		
			else {
				dayNumber = random.nextInt(problemData.getTotalDays());
			    slotNumber = random.nextInt(problemData.getTotalSlotsPerDay());
			}
			
			this.days[dayNumber].slots[slotNumber].coursesScheduled.add(courseId);

			CourseSchedule cs = new CourseSchedule(courseId, slotNumber, dayNumber);
			this.courseSchedule.put(courseId, cs);

		}

	}
	
	public void mutate(ProblemData problemData, Random random, double mutationRate)
	{
		int mutationProbability=  (int)(100*mutationRate);
		if (mutationProbability==0)
			return;
		
		int totalCoursesToSchedule= problemData.getCoursesToBeScheduled().size();		
		for (int i=0; i<totalCoursesToSchedule; i++)
		{
			
			if (random.nextInt(100)+1<= mutationProbability)
			{
				String courseId= problemData.getCoursesToBeScheduled().get(i);
				if (problemData.getPreferences().containsKey(courseId))
					continue;
				
				int randomDay=random.nextInt(problemData.getTotalDays());
				int randomSlot=random.nextInt(problemData.getTotalSlotsPerDay());
				
				int previouslyAssignedDay= this.courseSchedule.get(courseId).day;
				int previouslyAssignedSlot= this.courseSchedule.get(courseId).slot;
				this.days[previouslyAssignedDay].slots[previouslyAssignedSlot].coursesScheduled.remove(courseId);
				
				this.courseSchedule.get(courseId).day=randomDay;
				this.courseSchedule.get(courseId).slot=randomSlot;
				this.days[randomDay].slots[randomSlot].coursesScheduled.add(courseId);
			}
		}
	}

	public Day[] getDayWiseSchedule()
	{
		return this.days;
	}

	public HashMap<String, CourseSchedule> getCourseWiseSchedule()
	{
		return this.courseSchedule;
	}

	public final void setFitnessValue(int fitness)
	{
		this.fitness = fitness;
	}

	public void setUnfitnessValue(int unfitness)
	{
		this.unfitness = unfitness;
	}

	public int getFitnessValue()
	{
		return this.fitness;
	}

	public int getUnfitnessValue()
	{
		return this.unfitness;
	}
	
	public int getTotal()
	{
		return this.fitness+this.unfitness;
	}
}
