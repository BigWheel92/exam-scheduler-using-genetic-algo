package ga_classes;
import java.util.HashSet;
import exam_input_data_classes.ProblemData;

public class GeneticAlgorithmForMidExams extends GeneticAlgorithm {

	public GeneticAlgorithmForMidExams(ProblemData problemData) {
		super(problemData);
		// TODO Auto-generated constructor stub
	}

	@Override
		public void evaluateChromosome(Chromosome c)
	{
		Day[] days = c.getDayWiseSchedule();
		int unfitness = 0;
		int fitness = 0;
		//calculating no. of seating capacity violations
		for (int i = 0; i < problemData.getTotalDays(); i++)
		{
			for (int j = 0; j < problemData.getTotalSlotsPerDay(); j++)
			{
				int currentSlotSeatingCount = 0;

				HashSet<String> coursesScheduledInCurrentSlot = days[i].slots[j].getCoursesScheduled();

				for (String courseId : coursesScheduledInCurrentSlot)
				{
					currentSlotSeatingCount += this.problemData.getAllCourses().get(courseId).getStudentRollNumbers().size();

				}

				unfitness += (Math.max(0, currentSlotSeatingCount - problemData.getSeatingCapacityPerSlot()));
				//multiply by 2 and check results in the next scheduling.
			}
		}


		//finding number of students who have more than 2 exam on same day

		for (String studentRollNo : this.problemData.getAllStudents().keySet())
		{
			for (int i = 0; i < this.problemData.getTotalDays(); i++)
			{
				int coursesOnCurrentDay = 0;

				for (int j = 0; j < this.problemData.getTotalSlotsPerDay(); j++)
				{
					int coursesInCurrentSlot = 0;
					for (String courseId : c.getDayWiseSchedule()[i].slots[j].getCoursesScheduled())
					{
						if (this.problemData.getAllCourses().get(courseId).getStudentRollNumbers().contains(studentRollNo))
						{
							coursesOnCurrentDay++;
							coursesInCurrentSlot++;
						}
					}
					unfitness += Math.max(0, coursesInCurrentSlot - 1);
				}
				if (coursesOnCurrentDay > 2)
					unfitness += (coursesOnCurrentDay);

			}
		}
		
		c.setUnfitnessValue(unfitness);
		c.setFitnessValue(fitness);
	}//end of evaluate function
}
