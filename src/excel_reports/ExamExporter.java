package excel_reports;
import java.util.HashSet;
import exam_input_data_classes.ProblemData;
import ga_classes.Chromosome;

public abstract class ExamExporter {
	public abstract String exportDateSheetToExcel();

	protected String outputDirectoryPath;
	protected ProblemData problemData;
	protected Chromosome bestSchedule;

	public class ExamsScheduledInSlot
	{
		public HashSet<String> csExamsScheduled;
		public HashSet<String> eeExamsScheduled;
		public HashSet<String> cvExamsScheduled;
		public HashSet<String> mgExamsScheduled;

		public ExamsScheduledInSlot()
		{
			this.csExamsScheduled = new HashSet<String>();
			this.eeExamsScheduled = new HashSet<String>();
			this.cvExamsScheduled = new HashSet<String>();
			this.mgExamsScheduled = new HashSet<String>();
		}

	}
	

	
	public class ExamsScheduledDays
	{
		public ExamsScheduledInSlot[]slots;
		public ExamsScheduledDays(int totalSlots)
		{
			this.slots = new ExamsScheduledInSlot[totalSlots];
			for (int i = 0; i < totalSlots; i++)
			{
				this.slots[i] = new ExamsScheduledInSlot();
			}
		}
	}

	public ExamsScheduledDays[]dateSheet;

	public ExamExporter(ProblemData problemData, Chromosome bestSchedule, String outputFileName)
	{
		this.outputDirectoryPath = outputFileName;
		this.problemData = problemData;
		this.bestSchedule = bestSchedule;

		this.dateSheet = new ExamsScheduledDays[problemData.getTotalDays()];
		for (int i = 0; i < problemData.getTotalDays(); i++)
		{
			this.dateSheet[i] = new ExamsScheduledDays(problemData.getTotalSlotsPerDay());
		}
	}

	public int findMaximumNumberOfExamsScheduledInAnySlot(int day)
	{

		int max = this.bestSchedule.getDayWiseSchedule()[day].getSlots()[0].getCoursesScheduled().size();
		for (int i = 1; i < this.bestSchedule.getDayWiseSchedule()[day].getSlots().length; i++)
		{
			if (max < this.bestSchedule.getDayWiseSchedule()[day].getSlots()[i].getCoursesScheduled().size())
				max = this.bestSchedule.getDayWiseSchedule()[day].getSlots()[i].getCoursesScheduled().size();

		}

		return max;
	}

	void checkDepartmentCourseOffer()
	{
		for (String courseId : this.problemData.getCoursesToBeScheduled())
		{
			int day = this.bestSchedule.getCourseWiseSchedule().get(courseId).getDay();
			int slot = this.bestSchedule.getCourseWiseSchedule().get(courseId).getSlot();

			for (String studentRollNumber : this.problemData.getAllCourses().get(courseId).getStudentRollNumbers())
			{
				if (this.problemData.getAllStudents().get(studentRollNumber).getDegree().contains("CS") || this.problemData.getAllStudents().get(studentRollNumber).getDegree().contains("DS") || this.problemData.getAllStudents().get(studentRollNumber).getDegree().contains("SPM") || this.problemData.getAllStudents().get(studentRollNumber).getDegree().contains("SE"))
				{
					this.dateSheet[day].slots[slot].csExamsScheduled.add(courseId);

				}

				if (this.problemData.getAllStudents().get(studentRollNumber).getDegree().contains("EE"))
				{
					this.dateSheet[day].slots[slot].eeExamsScheduled.add(courseId);

				}

				if (this.problemData.getAllStudents().get(studentRollNumber).getDegree().contains("CV"))
				{
					this.dateSheet[day].slots[slot].cvExamsScheduled.add(courseId);

				}

				if (this.problemData.getAllStudents().get(studentRollNumber).getDegree().contains("AF") || this.problemData.getAllStudents().get(studentRollNumber).getDegree().contains("BBA") || this.problemData.getAllStudents().get(studentRollNumber).getDegree().contains("MBA"))
				{
					this.dateSheet[day].slots[slot].mgExamsScheduled.add(courseId);

				}

			}
		}

	}

	public int findMaximumNumberOfCVExamsScheduledInAnySlot(int day)
	{
		int max = this.dateSheet[day].slots[0].cvExamsScheduled.size();
		for (int i = 1; i < this.dateSheet[day].slots.length; i++)
		{
			if (max < this.dateSheet[day].slots[i].cvExamsScheduled.size())
				max = this.dateSheet[day].slots[i].cvExamsScheduled.size();

		}

		return max;
	}

	public int findMaximumNumberOfCSExamsScheduledInAnySlot(int day)
	{

		int max = this.dateSheet[day].slots[0].csExamsScheduled.size();
		for (int i = 1; i < this.dateSheet[day].slots.length; i++)
		{
			if (max < this.dateSheet[day].slots[i].csExamsScheduled.size())
				max = this.dateSheet[day].slots[i].csExamsScheduled.size();

		}

		return max;
	}

	public int findMaximumNumberOfEEExamsScheduledInAnySlot(int day)
	{

		int max = this.dateSheet[day].slots[0].eeExamsScheduled.size();
		for (int i = 1; i < this.dateSheet[day].slots.length; i++)
		{
			if (max < this.dateSheet[day].slots[i].eeExamsScheduled.size())
				max = this.dateSheet[day].slots[i].eeExamsScheduled.size();

		}

		return max;
	}


	public int findMaximumNumberOfMGExamsScheduledInAnySlot(int day)
	{
		int max = this.dateSheet[day].slots[0].mgExamsScheduled.size();
		for (int i = 1; i < this.dateSheet[day].slots.length; i++)
		{
			if (max < this.dateSheet[day].slots[i].mgExamsScheduled.size())
				max = this.dateSheet[day].slots[i].mgExamsScheduled.size();

		}

		return max;
	}

}


