package excel_reports;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import exam_input_data_classes.Course;
import exam_input_data_classes.DateHelper;
import exam_input_data_classes.ProblemData;
import exam_input_data_classes.Student;
import ga_classes.CourseSchedule;
import ga_classes.Day;


public class MidExamReport extends ExamReport {

	ArrayList<CourseWiseSummary> ThreeExamsOnTheSameDayCourseSummary;
	ArrayList<CommonStudentsInCoursePair> commonStudentsInEachCoursesPair;

	private void findCommonStudentsInEachCoursePair()
	{
		ArrayList<Course>coursesArray = new ArrayList<Course>();
		coursesArray.addAll(this.problemData.getAllCourses().values());

		Collections.sort(coursesArray, new Comparator<Course>() {

			@Override
				public int compare(Course arg0, Course arg1) {

				Course this_ = (Course)arg0;
				Course that = (Course)arg1;


				return this_.getCourseName().compareTo(that.getCourseName());


			}
		}

		);

		for (int i = 0; i < coursesArray.size() - 1; i++)
		{
			if (coursesArray.get(i).getToBeScheduled() == false)
				continue;

			for (int j = 0; j < coursesArray.size() - 1; j++)
			{
				if (coursesArray.get(j).getToBeScheduled() == false)
					continue;

				if (coursesArray.get(i).getCourseId().equals(coursesArray.get(j).getCourseId()))
					continue;

				Set<String> s1 = new HashSet<String>(coursesArray.get(i).getStudentRollNumbers());

				Set<String> s2 = new HashSet<String>(coursesArray.get(j).getStudentRollNumbers());

				s1.retainAll(s2);

				if (s1.size() > 0)
				{
					CommonStudentsInCoursePair c = new CommonStudentsInCoursePair();
					c.count = s1.size();
					c.course1 = coursesArray.get(i).getCourseName() + " (" + coursesArray.get(i).getCourseId() + ")";
					c.course2 = coursesArray.get(j).getCourseName() + " (" + coursesArray.get(j).getCourseId() + ")";
					this.commonStudentsInEachCoursesPair.add(c);
				}
			}
		}


	}


	private class CommonStudentsInCoursePair
	{

		public String course1 = "?";
		public String course2 = "?";
		public int count = -1;
	}


	private class CourseWiseSummary implements Comparable
	{
		public int count = 0;
		public String date = "";
		public String course1 = "";
		public String course2 = "";
		public String course3 = "";
		@Override
			public int compareTo(Object arg) {
			CourseWiseSummary that = (CourseWiseSummary)arg;

			if (this == that)
			{
				return 0;
			}

			if (this.count < that.count)
			{
				return 1;
			}
			if (this.count > that.count)
			{
				return -1;
			}

			if (this.course1.compareTo(that.course1) == 0)
			{
				if (this.course2.compareTo(that.course2) == 0)
				{
					return this.course3.compareTo(that.course3);
				}

				return this.course2.compareTo(that.course2);
			}

			return this.course1.compareTo(that.course1);
		}

	}


	class StudentExamClash //more than 1 exam in the same day and slot.
	{
		String rollNumber = "";

		ArrayList<String> coursesWithSameDayAndSlot = new ArrayList<String>();

		public int day = -1;
		public int slot = -1;

	}
	ArrayList<StudentExamClash> studentsWithSameDayAndSameSlotExamClash = new ArrayList<StudentExamClash>();


	class StudentWithMoreThan2ExamsPerDay //more than 2 exams on the same day.
	{
		String rollNumber = "";

		ArrayList<String> coursesOnSameDay = new ArrayList<String>();
		public int day = -1;

	}

	ArrayList<StudentWithMoreThan2ExamsPerDay> studentsWithMoreThan2ExamsPerDay = new ArrayList<StudentWithMoreThan2ExamsPerDay>();

	private void findStudentsWithMoreThan2ExamsPerDay()
	{
		for (Student student : this.problemData.getAllStudents().values())
		{
			for (int i = 0; i < this.problemData.getTotalDays(); i++)
			{
				StudentWithMoreThan2ExamsPerDay studentWithMoreThan2ExamsPerDay = new StudentWithMoreThan2ExamsPerDay(); //student with more than 2 exams per day.
				studentWithMoreThan2ExamsPerDay.rollNumber = student.getRollNumber();

				for (String registeredCourses : student.getCourses())
				{

					for (int l = 0; l < this.problemData.getTotalSlotsPerDay(); l++)
					{

						if (this.dateSheet[i].getSlots()[l].getCoursesScheduled().contains(registeredCourses))
						{

							studentWithMoreThan2ExamsPerDay.coursesOnSameDay.add(this.problemData.getAllCourses().get(registeredCourses).getCourseName());

						}
					}
				} //end of registered courses loop

				if (studentWithMoreThan2ExamsPerDay.coursesOnSameDay.size() > 2)
				{
					studentWithMoreThan2ExamsPerDay.day = i;
					this.studentsWithMoreThan2ExamsPerDay.add(studentWithMoreThan2ExamsPerDay);

				}

			}//end of i loop
		}//end of student loop
	}

	private void findStudentsWithSameDayAndSameSlotExamClash()
	{


		//student with more than 1 exam in the same slot and day.
		for (Student student : this.problemData.getAllStudents().values())
		{

			for (int i = 0; i < this.problemData.getTotalDays(); i++)
			{
				for (int j = 0; j < this.problemData.getTotalSlotsPerDay(); j++)
				{
					StudentExamClash clashes = new StudentExamClash(); //clashes of a student per day and per slot.
					clashes.rollNumber = student.getRollNumber();

					for (String registeredCourse : student.getCourses())
					{

						if (this.dateSheet[i].getSlots()[j].getCoursesScheduled().contains(registeredCourse))
						{
							clashes.coursesWithSameDayAndSlot.add(this.problemData.getAllCourses().get(registeredCourse).getCourseName());
						}
					}

					if (clashes.coursesWithSameDayAndSlot.size() > 1)
					{
						clashes.day = i;
						clashes.slot = j;
						this.studentsWithSameDayAndSameSlotExamClash.add(clashes);

					}


				}
			}


		}
	}

	private void printStudentsWith3ExamsPerDay(Workbook workbook)
	{
		Sheet studentsMoreThan2ExamsSheet = workbook.createSheet(MidExamReportSheetNames.threeExams);

		int maxColumns = 0;
		int rowNo = 0;
		for (int i = 0; i < this.studentsWithMoreThan2ExamsPerDay.size(); i++)
		{
			if (this.studentsWithMoreThan2ExamsPerDay.get(i).coursesOnSameDay.size() != 3)
				continue;

			if (maxColumns < this.studentsWithMoreThan2ExamsPerDay.get(i).coursesOnSameDay.size())
				maxColumns = this.studentsWithMoreThan2ExamsPerDay.get(i).coursesOnSameDay.size();

			Row row = studentsMoreThan2ExamsSheet.createRow(rowNo++);

			Cell cell = row.createCell(0);
			cell.setCellValue(this.studentsWithMoreThan2ExamsPerDay.get(i).rollNumber);


			cell = row.createCell(1);
			cell.setCellValue(this.problemData.getAllStudents().get(this.studentsWithMoreThan2ExamsPerDay.get(i).rollNumber).getName());
			// studentExamClashSheet.autoSizeColumn(1);

			cell = row.createCell(2);
			cell.setCellValue(this.problemData.getAllStudents().get(this.studentsWithMoreThan2ExamsPerDay.get(i).rollNumber).getDegree());
			// studentExamClashSheet.autoSizeColumn(2);

			cell = row.createCell(3);
			cell.setCellValue(this.problemData.getExamDates().get(this.studentsWithMoreThan2ExamsPerDay.get(i).day));
			// studentExamClashSheet.autoSizeColumn(3);




			for (int j = 0; j < this.studentsWithMoreThan2ExamsPerDay.get(i).coursesOnSameDay.size(); j++)
			{
				cell = row.createCell(j + 4);
				cell.setCellValue(this.studentsWithMoreThan2ExamsPerDay.get(i).coursesOnSameDay.get(j));
				// studentExamClashSheet.autoSizeColumn(j+5);


			}

		}

		for (int i = 0; i < maxColumns + 4; i++)
		{
			studentsMoreThan2ExamsSheet.autoSizeColumn(i);
		}
	}

	private void printStudentsWithMoreThan4ExamsPerDay(Workbook workbook)
	{
		Sheet studentsMoreThan2ExamsSheet = workbook.createSheet(MidExamReportSheetNames.moreThan4Exams);

		int maxColumns = 0;
		int rowNo = 0;
		for (int i = 0; i < this.studentsWithMoreThan2ExamsPerDay.size(); i++)
		{
			if (this.studentsWithMoreThan2ExamsPerDay.get(i).coursesOnSameDay.size() <= 4)
				continue;

			if (maxColumns < this.studentsWithMoreThan2ExamsPerDay.get(i).coursesOnSameDay.size())
				maxColumns = this.studentsWithMoreThan2ExamsPerDay.get(i).coursesOnSameDay.size();

			Row row = studentsMoreThan2ExamsSheet.createRow(rowNo++);

			Cell cell = row.createCell(0);
			cell.setCellValue(this.studentsWithMoreThan2ExamsPerDay.get(i).rollNumber);


			cell = row.createCell(1);
			cell.setCellValue(this.problemData.getAllStudents().get(this.studentsWithMoreThan2ExamsPerDay.get(i).rollNumber).getName());
			// studentExamClashSheet.autoSizeColumn(1);

			cell = row.createCell(2);
			cell.setCellValue(this.problemData.getAllStudents().get(this.studentsWithMoreThan2ExamsPerDay.get(i).rollNumber).getDegree());
			// studentExamClashSheet.autoSizeColumn(2);

			cell = row.createCell(3);

			cell.setCellValue(this.problemData.getExamDates().get(this.studentsWithMoreThan2ExamsPerDay.get(i).day));
			// studentExamClashSheet.autoSizeColumn(3);




			for (int j = 0; j < this.studentsWithMoreThan2ExamsPerDay.get(i).coursesOnSameDay.size(); j++)
			{
				cell = row.createCell(j + 4);
				cell.setCellValue(this.studentsWithMoreThan2ExamsPerDay.get(i).coursesOnSameDay.get(j));
				// studentExamClashSheet.autoSizeColumn(j+5);


			}

		}

		for (int i = 0; i < maxColumns + 4; i++)
		{
			studentsMoreThan2ExamsSheet.autoSizeColumn(i);
		}

	}


	private void printStudentsWith4ExamsPerDay(Workbook workbook)
	{
		Sheet studentsMoreThan2ExamsSheet = workbook.createSheet(MidExamReportSheetNames.fourExams);

		int maxColumns = 0;
		int rowNo = 0;
		for (int i = 0; i < this.studentsWithMoreThan2ExamsPerDay.size(); i++)
		{
			if (this.studentsWithMoreThan2ExamsPerDay.get(i).coursesOnSameDay.size() != 4)
				continue;

			if (maxColumns < this.studentsWithMoreThan2ExamsPerDay.get(i).coursesOnSameDay.size())
				maxColumns = this.studentsWithMoreThan2ExamsPerDay.get(i).coursesOnSameDay.size();

			Row row = studentsMoreThan2ExamsSheet.createRow(rowNo++);

			Cell cell = row.createCell(0);
			cell.setCellValue(this.studentsWithMoreThan2ExamsPerDay.get(i).rollNumber);


			cell = row.createCell(1);
			cell.setCellValue(this.problemData.getAllStudents().get(this.studentsWithMoreThan2ExamsPerDay.get(i).rollNumber).getName());
			// studentExamClashSheet.autoSizeColumn(1);

			cell = row.createCell(2);
			cell.setCellValue(this.problemData.getAllStudents().get(this.studentsWithMoreThan2ExamsPerDay.get(i).rollNumber).getDegree());
			// studentExamClashSheet.autoSizeColumn(2);

			cell = row.createCell(3);
			cell.setCellValue(this.problemData.getExamDates().get(this.studentsWithMoreThan2ExamsPerDay.get(i).day));
			// studentExamClashSheet.autoSizeColumn(3);




			for (int j = 0; j < this.studentsWithMoreThan2ExamsPerDay.get(i).coursesOnSameDay.size(); j++)
			{
				cell = row.createCell(j + 4);
				cell.setCellValue(this.studentsWithMoreThan2ExamsPerDay.get(i).coursesOnSameDay.get(j));
				// studentExamClashSheet.autoSizeColumn(j+5);


			}

		}

		for (int i = 0; i < maxColumns + 4; i++)
		{
			studentsMoreThan2ExamsSheet.autoSizeColumn(i);
		}
	}



	void printStudentsWithExamClash(Workbook workbook)
	{
		//Student Wise Exam Clash (more than 1 exam in the same slot)
		Sheet studentExamClashSheet = workbook.createSheet(MidExamReportSheetNames.studentExamClashes);

		int maxColumns = 0;
		for (int i = 0; i < this.studentsWithSameDayAndSameSlotExamClash.size(); i++)
		{
			if (maxColumns < this.studentsWithSameDayAndSameSlotExamClash.get(i).coursesWithSameDayAndSlot.size())
				maxColumns = this.studentsWithSameDayAndSameSlotExamClash.get(i).coursesWithSameDayAndSlot.size();

			Row row = studentExamClashSheet.createRow(i);

			Cell cell = row.createCell(0);
			cell.setCellValue(this.studentsWithSameDayAndSameSlotExamClash.get(i).rollNumber);


			cell = row.createCell(1);
			cell.setCellValue(this.problemData.getAllStudents().get(this.studentsWithSameDayAndSameSlotExamClash.get(i).rollNumber).getName());
			// studentExamClashSheet.autoSizeColumn(1);

			cell = row.createCell(2);
			cell.setCellValue(this.problemData.getAllStudents().get(this.studentsWithSameDayAndSameSlotExamClash.get(i).rollNumber).getDegree());
			// studentExamClashSheet.autoSizeColumn(2);

			cell = row.createCell(3);
			cell.setCellValue(this.problemData.getExamDates().get(this.studentsWithSameDayAndSameSlotExamClash.get(i).day));
			// studentExamClashSheet.autoSizeColumn(3);

			cell = row.createCell(4);
			cell.setCellValue(this.problemData.getSlotTimings().get(this.studentsWithSameDayAndSameSlotExamClash.get(i).slot));
			// studentExamClashSheet.autoSizeColumn(4);


			for (int j = 0; j < this.studentsWithSameDayAndSameSlotExamClash.get(i).coursesWithSameDayAndSlot.size(); j++)
			{
				cell = row.createCell(j + 5);
				cell.setCellValue(this.studentsWithSameDayAndSameSlotExamClash.get(i).coursesWithSameDayAndSlot.get(j));
				// studentExamClashSheet.autoSizeColumn(j+5);


			}

		}

		for (int i = 0; i < maxColumns + 5; i++)
		{
			studentExamClashSheet.autoSizeColumn(i);
		}
	}

	public void printStudentCountInEachCourseToBeScheduled(Workbook workbook)
	{
		Sheet studentCountInCoursesToScheduleSheet = workbook.createSheet(MidExamReportSheetNames.studentCountInCoursesToSchedule);

		int rowNo = 0;
		for (Course c : this.problemData.getAllCourses().values())
		{
			if (c.getToBeScheduled() == false)
				continue;

			Row row = studentCountInCoursesToScheduleSheet.createRow(rowNo++);
			row.createCell(0).setCellValue(c.getCourseId());
			row.createCell(1).setCellValue(c.getCourseName());
			row.createCell(2).setCellValue(c.getStudentRollNumbers().size());

		}
		studentCountInCoursesToScheduleSheet.autoSizeColumn(0);
		studentCountInCoursesToScheduleSheet.autoSizeColumn(1);
		studentCountInCoursesToScheduleSheet.autoSizeColumn(2);
	}


	public void printCommonStudentsInEachCoursePair(Workbook workbook)
	{

		Sheet coursePairWiseCommonStudentsSheet = workbook.createSheet(MidExamReportSheetNames.commonStudentsInEachCoursePair);
		int rowNo = 0;
		for (int i = 0; i < this.commonStudentsInEachCoursesPair.size(); i++)
		{

			Row row = coursePairWiseCommonStudentsSheet.createRow(rowNo);
			row.createCell(0).setCellValue(this.commonStudentsInEachCoursesPair.get(i).course1);
			row.createCell(1).setCellValue(this.commonStudentsInEachCoursesPair.get(i).course2);
			row.createCell(2).setCellValue(this.commonStudentsInEachCoursesPair.get(i).count);
			rowNo++;

		}
		coursePairWiseCommonStudentsSheet.autoSizeColumn(0);
		coursePairWiseCommonStudentsSheet.autoSizeColumn(1);
		coursePairWiseCommonStudentsSheet.autoSizeColumn(2);
		coursePairWiseCommonStudentsSheet.autoSizeColumn(3);
	}

	public void printAllCourses(Workbook workbook)
	{
		////////////////////all courses///////
		Sheet allCoursesSheet = workbook.createSheet(MidExamReportSheetNames.allCourses);
		int rowNo = 0;
		for (Course course : this.problemData.getAllCourses().values())
		{

			Row row = allCoursesSheet.createRow(rowNo);
			row.createCell(0).setCellValue(course.getCourseId());
			row.createCell(1).setCellValue(course.getCourseName());
			rowNo++;

		}
		allCoursesSheet.autoSizeColumn(0);
		allCoursesSheet.autoSizeColumn(1);
	}


	public void printCoursesToSchedule(Workbook workbook)
	{
		//courses to Be scheduled
		Sheet toBeScheduledCoursesSheet = workbook.createSheet("Courses to Schedule");
		int rowNo = 0;
		for (Course course : this.problemData.getAllCourses().values())
		{
			if (course.getToBeScheduled() == false)
				continue;
			Row row = toBeScheduledCoursesSheet.createRow(rowNo);
			row.createCell(0).setCellValue(course.getCourseId());
			row.createCell(1).setCellValue(course.getCourseName());
			rowNo++;

		}
		toBeScheduledCoursesSheet.autoSizeColumn(0);
		toBeScheduledCoursesSheet.autoSizeColumn(1);
	}

	private void printUnscheduledCourses(Workbook workbook)
	{
		//unscheduled courses sheet
		Sheet unscheduledCoursesSheet = workbook.createSheet(MidExamReportSheetNames.unscheduledCourses);
		int rowNo = 0;
		for (Course course : this.problemData.getAllCourses().values())
		{
			if (course.getToBeScheduled() == true)
				continue;
			Row row = unscheduledCoursesSheet.createRow(rowNo);
			row.createCell(0).setCellValue(course.getCourseId());
			row.createCell(1).setCellValue(course.getCourseName());
			rowNo++;

		}
		unscheduledCoursesSheet.autoSizeColumn(0);
		unscheduledCoursesSheet.autoSizeColumn(1);
	}

	public void printTotalStudentsScheduledInEachSlot(Workbook workbook)
	{
		/////////////////////total Students Scheduled in each slot//////////////////////
		Sheet studentCountPerDayPerSlotSheet = workbook.createSheet(MidExamReportSheetNames.studentCountPerDayPerSlot);

		int rowNo = 0;

		Font font = workbook.createFont();
		font.setBold(true);
		font.setFontName("Calibri");
		font.setFontHeightInPoints((short)10);
		font.setColor(IndexedColors.BLACK.getIndex());

		Row headerRow = studentCountPerDayPerSlotSheet.createRow(rowNo++);
		headerRow.setHeightInPoints((float)32.5);
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setFont(font);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);

		Cell cell = headerRow.createCell(0);
		cell.setCellValue("Day");
		cell.setCellStyle(cellStyle);
		studentCountPerDayPerSlotSheet.autoSizeColumn(0);

		cell = headerRow.createCell(1);
		cell.setCellValue("Date");
		cell.setCellStyle(cellStyle);
		studentCountPerDayPerSlotSheet.autoSizeColumn(1);

		for (int i = 0; i < this.problemData.getTotalSlotsPerDay(); i++)
		{
			cell = headerRow.createCell(i + 2);
			cell.setCellValue(this.problemData.getSlotTimings().get(i));
			cell.setCellStyle(cellStyle);


		}

		for (int i = 0; i < this.problemData.getTotalDays(); i++)
		{
			Row row = studentCountPerDayPerSlotSheet.createRow(rowNo++);
			cell = row.createCell(0);
			cell.setCellStyle(cellStyle);
			cell.setCellValue(DateHelper.getDayName(this.problemData.getExamDates().get(i)));

			cell = row.createCell(1);
			cell.setCellStyle(cellStyle);
			cell.setCellValue(this.problemData.getExamDates().get(i));


			for (int j = 0; j < this.problemData.getTotalSlotsPerDay(); j++)
			{
				cell = row.createCell(j + 2);

				cell.setCellStyle(cellStyle);
				int count = 0;
				//finding the count of student scheduled in thecurrent slot.
				for (int k = 0; k < this.problemData.getCoursesToBeScheduled().size(); k++)
				{
					CourseSchedule current = this.courseSchedule.get(this.problemData.getCoursesToBeScheduled().get(k));
					if (current.getDay() == i && current.getSlot() == j)
					{
						count += this.problemData.getAllCourses().get(current.getCourseId()).getStudentRollNumbers().size();
					}
				}
				cell.setCellValue(count);
			}
		}


		studentCountPerDayPerSlotSheet.autoSizeColumn(0);
		studentCountPerDayPerSlotSheet.autoSizeColumn(1);
		for (int i = 0; i < this.problemData.getTotalSlotsPerDay(); i++)
		{
			studentCountPerDayPerSlotSheet.autoSizeColumn(i + 2);

		}
	}

	public void print3CoursesOnTheSameDaySummary(Workbook workbook)
	{
		Sheet courseWiseSummary = workbook.createSheet(MidExamReportSheetNames.courseWiseSummary);

		int rowNo = 0;
		for (int i = 0; i < this.ThreeExamsOnTheSameDayCourseSummary.size(); i++)
		{
			Row row = courseWiseSummary.createRow(rowNo++);

			Cell cell = row.createCell(0);
			cell.setCellValue(this.ThreeExamsOnTheSameDayCourseSummary.get(i).date);


			cell = row.createCell(1);
			cell.setCellValue(this.ThreeExamsOnTheSameDayCourseSummary.get(i).course1);
			// studentExamClashSheet.autoSizeColumn(1);

			cell = row.createCell(2);
			cell.setCellValue(this.ThreeExamsOnTheSameDayCourseSummary.get(i).course2);
			// studentExamClashSheet.autoSizeColumn(2);

			cell = row.createCell(3);
			cell.setCellValue(this.ThreeExamsOnTheSameDayCourseSummary.get(i).course3);
			// studentExamClashSheet.autoSizeColumn(3);

			cell = row.createCell(4);
			cell.setCellValue(this.ThreeExamsOnTheSameDayCourseSummary.get(i).count);




		}

		for (int i = 0; i < 4; i++)
		{
			courseWiseSummary.autoSizeColumn(i);
		}




	}

	public void printScheduledCourses(Workbook workbook)
	{

		//unscheduled courses sheet
		Sheet unscheduledCoursesSheet = workbook.createSheet(MidExamReportSheetNames.scheduledCourses);
		int rowNo = 0;
		for (Course course : this.problemData.getAllCourses().values())
		{
			if (course.getToBeScheduled() == false)
				continue;

			Row row = unscheduledCoursesSheet.createRow(rowNo);
			row.createCell(0).setCellValue(course.getCourseId());
			row.createCell(1).setCellValue(course.getCourseName());
			rowNo++;

		}
		unscheduledCoursesSheet.autoSizeColumn(0);
		unscheduledCoursesSheet.autoSizeColumn(1);

	}

	public String exportToExcel()
	{
		try {
			Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file
			FileOutputStream fileOut = new FileOutputStream(this.outputDirectoryPath);

			this.printStudentsWithExamClash(workbook);
			this.printStudentsWith3ExamsPerDay(workbook);
			this.printStudentsWith4ExamsPerDay(workbook);
			this.printStudentsWithMoreThan4ExamsPerDay(workbook);
			this.print3CoursesOnTheSameDaySummary(workbook);

			this.printScheduledCourses(workbook);
			this.printUnscheduledCourses(workbook);
			//this.printExcludedCourses(workbook);
			this.printAllCourses(workbook);
			this.printTotalStudentsScheduledInEachSlot(workbook);
			this.printDateSheetList(workbook);
			this.printCommonStudentsInEachCoursePair(workbook);
			this.printStudentCountInEachCourseToBeScheduled(workbook);



			workbook.write(fileOut);
			workbook.close();
			fileOut.close();

			return null;

		}
		catch (Exception e) {

			e.printStackTrace();
			return "An error occurred while exporting the mid term report to excel.";
		}

	}


	public void find3CourseOnTheSameDayCourseWiseSummary()
	{

		for (int i = 0; i < this.problemData.getTotalDays(); i++)
		{
			for (int j = 0; j < this.problemData.getTotalSlotsPerDay(); j++)
			{

				for (String c1 : this.dateSheet[i].getSlots()[j].getCoursesScheduled())
				{
					for (int k = j; k < this.problemData.getTotalSlotsPerDay(); k++)
					{
						for (String c2 : this.dateSheet[i].getSlots()[k].getCoursesScheduled())
						{
							if (c1.equals(c2))
							{
								continue;
							}

							for (int l = k; l < this.problemData.getTotalSlotsPerDay(); l++)
							{
								for (String c3 : this.dateSheet[i].getSlots()[l].getCoursesScheduled())
								{
									if (c3.equals(c1) || c3.equals(c2))
									{
										continue;
									}

									Set<String> s1 = new HashSet<String>(this.problemData.getAllCourses().get(c1).getStudentRollNumbers());

									Set<String> s2 = new HashSet<String>(this.problemData.getAllCourses().get(c2).getStudentRollNumbers());

									s1.retainAll(s2);
									s2 = new HashSet<String>(this.problemData.getAllCourses().get(c3).getStudentRollNumbers());
									s1.retainAll(s2);

									if (s1.size() == 0)
										continue;

									CourseWiseSummary s = new CourseWiseSummary();

									s.date = this.problemData.getExamDates().get(i);
									s.count = 0;
									s.course1 = this.problemData.getAllCourses().get(c1).getCourseName();
									s.course2 = this.problemData.getAllCourses().get(c2).getCourseName();
									s.course3 = this.problemData.getAllCourses().get(c3).getCourseName();
									s.count = s1.size();
									this.ThreeExamsOnTheSameDayCourseSummary.add(s);


								}

							}

						}

					}

				}


			}


		}

		Collections.sort(this.ThreeExamsOnTheSameDayCourseSummary);
	}


	public MidExamReport(Day[]dateSheet, HashMap<String, CourseSchedule> courseSchedule, ProblemData problemData, String outputDirectoryPath)
	{
		this.dateSheet = dateSheet;
		this.outputDirectoryPath = outputDirectoryPath;
		this.problemData = problemData;
		this.dateSheet = dateSheet;
		this.courseSchedule = courseSchedule;

		this.ThreeExamsOnTheSameDayCourseSummary = new ArrayList<CourseWiseSummary>();
		this.commonStudentsInEachCoursesPair = new ArrayList<CommonStudentsInCoursePair>();

		this.findStudentsWithSameDayAndSameSlotExamClash();
		this.findStudentsWithMoreThan2ExamsPerDay();
		this.find3CourseOnTheSameDayCourseWiseSummary();
		this.findCommonStudentsInEachCoursePair();

	}

}
