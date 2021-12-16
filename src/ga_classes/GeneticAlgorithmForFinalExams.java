package ga_classes;
import java.util.HashSet;
import exam_input_data_classes.DateHelper;
import exam_input_data_classes.ProblemData;

public class GeneticAlgorithmForFinalExams extends GeneticAlgorithm {

	public GeneticAlgorithmForFinalExams(ProblemData problemData) {
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

		//finding number of students who have more than 1 exam on same day
		int countOfCoursesToSchedule = this.problemData.getCoursesToBeScheduled().size();

		for (int i = 0; i < countOfCoursesToSchedule - 1; i++) {

			CourseSchedule cs1 = c.getCourseWiseSchedule().get(problemData.getCoursesToBeScheduled().get(i));
			for (int j = i + 1; j < countOfCoursesToSchedule; j++)
			{
				CourseSchedule cs2 = c.getCourseWiseSchedule().get(problemData.getCoursesToBeScheduled().get(j));
				//the following code will be used in finding fitness

				//the following code will be used in finding unfitness
				if (cs1.getDay() == cs2.getDay())
				{
					for (String rollNumber : problemData.getAllCourses().get(cs1.getCourseId()).getStudentRollNumbers())
					{
						if (problemData.getAllCourses().get(cs2.getCourseId()).getStudentRollNumbers().contains(rollNumber))
						{
							if (cs1.getSlot() == cs2.getSlot())
								unfitness += 4; //if the slot is the same too.

							else unfitness += 3; //if the slot is not the same.

						}
					}

				}


				else if (DateHelper.getNextDate(problemData.getExamDates().get(cs1.getDay())).equals(problemData.getExamDates().get(cs2.getDay()))
					|| DateHelper.getNextDate(problemData.getExamDates().get(cs2.getDay())).equals(problemData.getExamDates().get(cs1.getDay())))
				{
					for (String rollNumber : problemData.getAllCourses().get(cs1.getCourseId()).getStudentRollNumbers())
					{
						if (problemData.getAllCourses().get(cs2.getCourseId()).getStudentRollNumbers().contains(rollNumber))
						{
							unfitness+=2;
						}
					}
					//HashSet<String> intersection = new HashSet<String>(problemData.getAllCourses().get(cs1.getCourseId()).getStudentRollNumbers()); // use the copy constructor
					//intersection.retainAll(problemData.getAllCourses().get(cs2.getCourseId()).getStudentRollNumbers());
					//fitness+=intersection.size();
				}

			}
		}

		c.setUnfitnessValue(unfitness);
		c.setFitnessValue(fitness);
	}//end of evaluate function
}
