package excel_reports;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
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


public class FinalExamReport extends ExamReport {

	ArrayList<CommonStudentsInCoursePair> commonStudentsInEachCoursesPair = new ArrayList<CommonStudentsInCoursePair>();

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
		});

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

	class StudentDirectExamClash //more than 1 exam in the same day and slot.
	{
		String rollNumber = "";

		ArrayList<String> coursesWithSameDayAndSlot = new ArrayList<String>();

		public int day = -1;
		public int slot = -1;

	}

	ArrayList<StudentDirectExamClash> studentsWithDirectExamClash = new ArrayList<StudentDirectExamClash>();


	class StudentWithMoreThan1ExamOnSameDay
	{
		String rollNumber;
		ArrayList<String> coursesOnSameDay = new ArrayList<String>();
		int day = -1;

	}
	ArrayList<StudentWithMoreThan1ExamOnSameDay> studentsWithMoreThan1ExamOnSameDay = new ArrayList<StudentWithMoreThan1ExamOnSameDay>();


	class StudentWithNoHolidayBetween2Exams
	{
		String rollNumber;
		ArrayList<String> coursesWithNoHoliday = new ArrayList<String>();

	}

	ArrayList<StudentWithNoHolidayBetween2Exams> studentsWithNoHolidayBetween2Exams = new ArrayList<StudentWithNoHolidayBetween2Exams>();

	class StudentWithNoHolidayBetweenExams
	{
		String rollNumber;
		ArrayList<String> coursesWithNoHoliday = new ArrayList<String>();

	}

	ArrayList<StudentWithNoHolidayBetweenExams> studentsWithNoHolidayBetweenExams = new ArrayList<StudentWithNoHolidayBetweenExams>();

	void findStudentsWithNoHolidayBetweenExams()
	{
		for (Student student : this.problemData.getAllStudents().values())
		{

			Object[] objectArray = student.getCourses().toArray();
			String[] studentCourses = Arrays.asList(objectArray)
				.toArray(new String[0]);

			ArrayList<String> list = new ArrayList<String>();

			for (int i = 0; i < this.problemData.getTotalDays(); i++)
			{
				boolean courseFound = false;



				for (int k = 0; k < studentCourses.length; k++)
				{

					for (int l = 0; l < this.problemData.getTotalSlotsPerDay(); l++)
					{
						if (this.dateSheet[i].getSlots()[l].getCoursesScheduled().contains(studentCourses[k]))
						{

							list.add(studentCourses[k]);
							courseFound = true;
						}
					}

				}

				if (i < this.problemData.getTotalDays() - 1 && DateHelper.getNextDate(problemData.getExamDates().get(i)).equals(problemData.getExamDates().get(i + 1)) == false)
				{
					if (list.size() < 2)
					{
						if (list.size() == 0)
							continue;

						else {
							list = new ArrayList<String>();
							continue;
						}

					}

					else
					{
						StudentWithNoHolidayBetweenExams s = new StudentWithNoHolidayBetweenExams();
						s.rollNumber = student.getRollNumber();
						s.coursesWithNoHoliday = list;
						this.studentsWithNoHolidayBetweenExams.add(s);
						list = new ArrayList<String>();
					}
				}


				if (courseFound == false)
				{
					if (list.size() < 2)
					{
						if (list.size() == 0)
							continue;

						else {
							list = new ArrayList<String>();
							continue;
						}

					}

					else
					{
						StudentWithNoHolidayBetweenExams s = new StudentWithNoHolidayBetweenExams();
						s.rollNumber = student.getRollNumber();
						s.coursesWithNoHoliday = list;
						this.studentsWithNoHolidayBetweenExams.add(s);
						list = new ArrayList<String>();
					}
				}

				if (i == this.problemData.getTotalDays() - 1)
				{
					StudentWithNoHolidayBetweenExams s = new StudentWithNoHolidayBetweenExams();
					s.rollNumber = student.getRollNumber();
					s.coursesWithNoHoliday = list;
					this.studentsWithNoHolidayBetweenExams.add(s);
					list = new ArrayList<String>();

				}




			}
		}
	}



	public void findStudentsWithNoHolidayBetween2Exams()
	{

		for (Student student : this.problemData.getAllStudents().values())
		{
			Object[] objectArray = student.getCourses().toArray();
			String[] studentCourses = Arrays.asList(objectArray)
				.toArray(new String[0]);

			for (int i = 0; i < studentCourses.length; i++)
			{
				String course1 = studentCourses[i];
				if (this.problemData.getAllCourses().get(course1).getToBeScheduled() == false)
					continue;

				for (int j = i + 1; j < studentCourses.length; j++)
				{
					String course2 = studentCourses[j];

					if (this.problemData.getAllCourses().get(course2).getToBeScheduled() == false)
						continue;

					if (DateHelper.getNextDate(this.problemData.getExamDates().get(this.courseSchedule.get(course1).getDay())).equals(this.problemData.getExamDates().get(this.courseSchedule.get(course2).getDay())))
					{
						StudentWithNoHolidayBetween2Exams s = new StudentWithNoHolidayBetween2Exams();
						s.rollNumber = student.getRollNumber();
						s.coursesWithNoHoliday.add(course1);
						s.coursesWithNoHoliday.add(course2);
						this.studentsWithNoHolidayBetween2Exams.add(s);
					}

					else if (DateHelper.getNextDate(this.problemData.getExamDates().get(this.courseSchedule.get(course2).getDay())).equals(this.problemData.getExamDates().get(this.courseSchedule.get(course1).getDay())))
					{
						StudentWithNoHolidayBetween2Exams s = new StudentWithNoHolidayBetween2Exams();
						s.rollNumber = student.getRollNumber();
						s.coursesWithNoHoliday.add(course2);
						s.coursesWithNoHoliday.add(course1);
						this.studentsWithNoHolidayBetween2Exams.add(s);
					}


				}

			}
		}
	}

	public void findStudentsWithDirectExamClash()
	{
		this.studentsWithDirectExamClash = new ArrayList<StudentDirectExamClash>();

		//student with more than 1 exam in the same slot and day.
		for (Student student : this.problemData.getAllStudents().values())
		{

			for (int i = 0; i < this.problemData.getTotalDays(); i++)
			{
				for (int j = 0; j < this.problemData.getTotalSlotsPerDay(); j++)
				{
					StudentDirectExamClash clashes = new StudentDirectExamClash(); //clashes of a student per day and per slot.
					clashes.rollNumber = student.getRollNumber();

					for (String registeredCourse : student.getCourses())
						if (this.dateSheet[i].getSlots()[j].getCoursesScheduled().contains(registeredCourse))
						{
							clashes.coursesWithSameDayAndSlot.add(this.problemData.getAllCourses().get(registeredCourse).getCourseName());
						}

					if (clashes.coursesWithSameDayAndSlot.size() > 1)
					{
						clashes.day = i;
						clashes.slot = j;
						this.studentsWithDirectExamClash.add(clashes);

					}


				}
			}

		}

	}


	void printStudentsWith2ConsecutiveExams(Workbook workbook)
	{
		Sheet sheet = workbook.createSheet(FinalExamReportSheetNames.moreThanTwoConsecutiveExams);

		for (int i = 0, j = 0; i < this.studentsWithNoHolidayBetweenExams.size(); i++)
		{

			String rollNumber = this.studentsWithNoHolidayBetweenExams.get(i).rollNumber;
			ArrayList<String> list = this.studentsWithNoHolidayBetweenExams.get(i).coursesWithNoHoliday;

			if (list.size() != 2)
				continue;


			Row row = sheet.createRow(j);
			row.createCell(0).setCellValue(rollNumber);
			row.createCell(1).setCellValue(this.problemData.getAllStudents().get(rollNumber).getName());
			row.createCell(2).setCellValue(this.problemData.getAllStudents().get(rollNumber).getDegree());

			for (int l = 0; l < list.size(); l++)
			{
				row.createCell(l + 3).setCellValue(this.problemData.getAllCourses().get(list.get(l)).getCourseName() + "(" + this.problemData.getExamDates().get(this.courseSchedule.get(list.get(l)).getDay()) + ")");


			}


			j++;

		}

		for (int i = 0; i < 12; i++)
		{
			sheet.autoSizeColumn(i);
		}

	}
	void printStudentsWithMoreThan4ConsecutiveExams(Workbook workbook)
	{
		Sheet sheet = workbook.createSheet(FinalExamReportSheetNames.moreThanFourConsecutiveExams);

		for (int i = 0, j = 0; i < this.studentsWithNoHolidayBetweenExams.size(); i++)
		{

			String rollNumber = this.studentsWithNoHolidayBetweenExams.get(i).rollNumber;
			ArrayList<String> list = this.studentsWithNoHolidayBetweenExams.get(i).coursesWithNoHoliday;

			if (list.size() < 5)
				continue;


			Row row = sheet.createRow(j);
			row.createCell(0).setCellValue(rollNumber);
			row.createCell(1).setCellValue(this.problemData.getAllStudents().get(rollNumber).getName());
			row.createCell(2).setCellValue(this.problemData.getAllStudents().get(rollNumber).getDegree());

			for (int l = 0; l < list.size(); l++)
			{
				row.createCell(l + 3).setCellValue(this.problemData.getAllCourses().get(list.get(l)).getCourseName() + "(" + this.courseSchedule.get(list.get(l)).getDay() + ")");


			}


			j++;

		}

		for (int i = 0; i < 12; i++)
		{
			sheet.autoSizeColumn(i);
		}

	}


	void printStudentsWith2ConsecutiveExamsOnSameDay(Workbook workbook)
	{
		Sheet sheet = workbook.createSheet(FinalExamReportSheetNames.moreThanTwoConsecutiveExamsOnSameDay);

		for (int i = 0, j = 0; i < this.studentsWithNoHolidayBetweenExams.size(); i++)
		{

			String rollNumber = this.studentsWithNoHolidayBetweenExams.get(i).rollNumber;
			ArrayList<String> list = this.studentsWithNoHolidayBetweenExams.get(i).coursesWithNoHoliday;

			if (list.size() != 2 || this.courseSchedule.get(list.get(0)).getDay() != this.courseSchedule.get(list.get(1)).getDay())
				continue;


			Row row = sheet.createRow(j);
			row.createCell(0).setCellValue(rollNumber);
			row.createCell(1).setCellValue(this.problemData.getAllStudents().get(rollNumber).getName());
			row.createCell(2).setCellValue(this.problemData.getAllStudents().get(rollNumber).getDegree());

			for (int l = 0; l < list.size(); l++)
			{
				row.createCell(l + 3).setCellValue(this.problemData.getAllCourses().get(list.get(l)).getCourseName() + "(" + this.problemData.getExamDates().get(this.courseSchedule.get(list.get(l)).getDay()) + ")");


			}


			j++;

		}

		for (int i = 0; i < 12; i++)
		{
			sheet.autoSizeColumn(i);
		}

	}



	void printStudentsWith4ConsecutiveExams(Workbook workbook)
	{
		Sheet sheet = workbook.createSheet(FinalExamReportSheetNames.fourConsecutiveExams);

		for (int i = 0, j = 0; i < this.studentsWithNoHolidayBetweenExams.size(); i++)
		{

			String rollNumber = this.studentsWithNoHolidayBetweenExams.get(i).rollNumber;
			ArrayList<String> list = this.studentsWithNoHolidayBetweenExams.get(i).coursesWithNoHoliday;

			if (list.size() != 4)
				continue;


			Row row = sheet.createRow(j);
			row.createCell(0).setCellValue(rollNumber);
			row.createCell(1).setCellValue(this.problemData.getAllStudents().get(rollNumber).getName());
			row.createCell(2).setCellValue(this.problemData.getAllStudents().get(rollNumber).getDegree());

			for (int l = 0; l < list.size(); l++)
			{
				row.createCell(l + 3).setCellValue(this.problemData.getAllCourses().get(list.get(l)).getCourseName() + "(" + this.problemData.getExamDates().get(this.courseSchedule.get(list.get(l)).getDay()) + ")");

			}


			j++;

		}

		for (int i = 0; i < 7; i++)
		{
			sheet.autoSizeColumn(i);
		}

	}
	void printStudentsWith3ConsecutiveExams(Workbook workbook)
	{
		Sheet sheet = workbook.createSheet(FinalExamReportSheetNames.threeConsecutiveExams);

		for (int i = 0, j = 0; i < this.studentsWithNoHolidayBetweenExams.size(); i++)
		{

			String rollNumber = this.studentsWithNoHolidayBetweenExams.get(i).rollNumber;
			ArrayList<String> list = this.studentsWithNoHolidayBetweenExams.get(i).coursesWithNoHoliday;

			if (list.size() != 3)
				continue;


			Row row = sheet.createRow(j);
			row.createCell(0).setCellValue(rollNumber);
			row.createCell(1).setCellValue(this.problemData.getAllStudents().get(rollNumber).getName());
			row.createCell(2).setCellValue(this.problemData.getAllStudents().get(rollNumber).getDegree());

			for (int l = 0; l < list.size(); l++)
			{
				row.createCell(l + 3).setCellValue(this.problemData.getAllCourses().get(list.get(l)).getCourseName() + "(" + this.problemData.getExamDates().get(this.courseSchedule.get(list.get(l)).getDay()) + ")");


			}


			j++;

		}

		for (int i = 0; i < 9; i++)
		{
			sheet.autoSizeColumn(i);
		}
	}

	void printStudentsWithNoHolidayBetween2Exams(Workbook workbook)
	{

		Sheet sheet = workbook.createSheet(FinalExamReportSheetNames.noHolidayBetween2Exams);

		for (int i = 0; i < this.studentsWithNoHolidayBetween2Exams.size(); i++)
		{
			String rollNumber = this.studentsWithNoHolidayBetween2Exams.get(i).rollNumber;
			String course1 = this.studentsWithNoHolidayBetween2Exams.get(i).coursesWithNoHoliday.get(0);
			String course2 = this.studentsWithNoHolidayBetween2Exams.get(i).coursesWithNoHoliday.get(1);

			Row row = sheet.createRow(i);
			row.createCell(0).setCellValue(rollNumber);
			row.createCell(1).setCellValue(this.problemData.getAllStudents().get(rollNumber).getName());
			row.createCell(2).setCellValue(this.problemData.getAllStudents().get(rollNumber).getDegree());

			row.createCell(3).setCellValue(this.problemData.getAllCourses().get(course1).getCourseName());
			row.createCell(4).setCellValue(this.problemData.getExamDates().get(this.courseSchedule.get(course1).getDay()));
			row.createCell(5).setCellValue(this.problemData.getAllCourses().get(course2).getCourseName());
			row.createCell(6).setCellValue(this.problemData.getExamDates().get(this.courseSchedule.get(course2).getDay()));
		}

		for (int i = 0; i < 7; i++)
		{
			sheet.autoSizeColumn(i);
		}
	}

	void printStudentsWithMoreThan1ExamOnSameDay(Workbook workbook)
	{
		Sheet sheet = workbook.createSheet(FinalExamReportSheetNames.moreThan1ExamOnSameDay);
		int maxColumns = 0;
		for (int i = 0; i < this.studentsWithMoreThan1ExamOnSameDay.size(); i++)
		{
			String rollNumber = this.studentsWithMoreThan1ExamOnSameDay.get(i).rollNumber;

			Row row = sheet.createRow(i);
			row.createCell(0).setCellValue(rollNumber);
			row.createCell(1).setCellValue(this.problemData.getAllStudents().get(rollNumber).getName());
			row.createCell(2).setCellValue(this.problemData.getAllStudents().get(rollNumber).getDegree());
			row.createCell(3).setCellValue(this.problemData.getExamDates().get(this.studentsWithMoreThan1ExamOnSameDay.get(i).day));

			for (int j = 0; j < this.studentsWithMoreThan1ExamOnSameDay.get(i).coursesOnSameDay.size(); j++)
			{
				row.createCell(j + 4).setCellValue(this.studentsWithMoreThan1ExamOnSameDay.get(i).coursesOnSameDay.get(j));

			}

			if (maxColumns < this.studentsWithMoreThan1ExamOnSameDay.get(i).coursesOnSameDay.size())
			{
				maxColumns = this.studentsWithMoreThan1ExamOnSameDay.get(i).coursesOnSameDay.size();
			}


		}

		for (int i = 0; i < maxColumns + 4; i++)
		{
			sheet.autoSizeColumn(i);
		}
	}

	void printUnscheduledExams(Workbook workbook)
	{
		//unscheduled courses sheet
		Sheet unscheduledCoursesSheet = workbook.createSheet(FinalExamReportSheetNames.unscheduledCourses);
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


	void printStudentsWithDirectExamClash(Workbook workbook)
	{
		//Student Wise Exam Clash (more than 1 exam in the same slot)
		Sheet studentExamClashSheet = workbook.createSheet(FinalExamReportSheetNames.directClashes);

		int maxColumns = 0;
		for (int i = 0; i < this.studentsWithDirectExamClash.size(); i++)
		{
			if (maxColumns < this.studentsWithDirectExamClash.get(i).coursesWithSameDayAndSlot.size())
				maxColumns = this.studentsWithDirectExamClash.get(i).coursesWithSameDayAndSlot.size();

			Row row = studentExamClashSheet.createRow(i);

			Cell cell = row.createCell(0);
			cell.setCellValue(this.studentsWithDirectExamClash.get(i).rollNumber);


			cell = row.createCell(1);
			cell.setCellValue(this.problemData.getAllStudents().get(this.studentsWithDirectExamClash.get(i).rollNumber).getName());
			// studentExamClashSheet.autoSizeColumn(1);

			cell = row.createCell(2);
			cell.setCellValue(this.problemData.getAllStudents().get(this.studentsWithDirectExamClash.get(i).rollNumber).getDegree());
			// studentExamClashSheet.autoSizeColumn(2);

			cell = row.createCell(3);
			cell.setCellValue(this.problemData.getExamDates().get(this.studentsWithDirectExamClash.get(i).day));
			// studentExamClashSheet.autoSizeColumn(3);

			cell = row.createCell(4);
			cell.setCellValue(this.problemData.getSlotTimings().get(this.studentsWithDirectExamClash.get(i).slot));
			// studentExamClashSheet.autoSizeColumn(4);


			for (int j = 0; j < this.studentsWithDirectExamClash.get(i).coursesWithSameDayAndSlot.size(); j++)
			{
				cell = row.createCell(j + 5);
				cell.setCellValue(this.studentsWithDirectExamClash.get(i).coursesWithSameDayAndSlot.get(j));
				// studentExamClashSheet.autoSizeColumn(j+5);


			}

		}

		for (int i = 0; i < maxColumns + 5; i++)
		{
			studentExamClashSheet.autoSizeColumn(i);
		}
	}

	public void printTotalStudentsScheduledInEachSlot(Workbook workbook)
	{
		/////////////////////total Students Scheduled in each slot//////////////////////
		Sheet studentCountPerDayPerSlotSheet = workbook.createSheet(FinalExamReportSheetNames.studentCountPerDayPerSlot);

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

	public String exportToExcel()
	{
		try {
			Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file
			FileOutputStream fileOut = new FileOutputStream(this.outputDirectoryPath);



			this.printStudentsWithDirectExamClash(workbook);
			this.printStudentsWithMoreThan1ExamOnSameDay(workbook);
			this.printStudentsWithNoHolidayBetween2Exams(workbook);
			this.printUnscheduledExams(workbook);
			this.printTotalStudentsScheduledInEachSlot(workbook);
			this.printStudentsWith2ConsecutiveExams(workbook);
			this.printStudentsWith3ConsecutiveExams(workbook);
			this.printStudentsWith4ConsecutiveExams(workbook);
			this.printStudentsWithMoreThan4ConsecutiveExams(workbook);
			this.printStudentsWith2ConsecutiveExamsOnSameDay(workbook);
			this.printCommonStudentsInEachCoursePair(workbook);
			this.printDateSheetList(workbook);
			workbook.write(fileOut);
			workbook.close();
			fileOut.close();

			return null;

		}
		catch (Exception e) {

			e.printStackTrace();
			return "An error occurred while exporting the final exam report to excel.";
		}
	}

	void findStudentsWithMoreThan1ExamOnSameDay()
	{
		for (Student student : this.problemData.getAllStudents().values())
		{
			for (int i = 0; i < this.problemData.getTotalDays(); i++)
			{
				StudentWithMoreThan1ExamOnSameDay studentWithMoreThan1ExamOnSameDay = new StudentWithMoreThan1ExamOnSameDay(); //student with more than 2 exams per day.
				studentWithMoreThan1ExamOnSameDay.rollNumber = student.getRollNumber();

				for (String registeredCourses : student.getCourses())
				{
					for (int l = 0; l < this.problemData.getTotalSlotsPerDay(); l++)
					{

						if (this.dateSheet[i].getSlots()[l].getCoursesScheduled().contains(registeredCourses))
						{

							studentWithMoreThan1ExamOnSameDay.coursesOnSameDay.add(this.problemData.getAllCourses().get(registeredCourses).getCourseName());
						}
					}
				}

				if (studentWithMoreThan1ExamOnSameDay.coursesOnSameDay.size() > 1)
				{
					studentWithMoreThan1ExamOnSameDay.day = i;
					this.studentsWithMoreThan1ExamOnSameDay.add(studentWithMoreThan1ExamOnSameDay);

				}
			}
		}

	}

	public void printCommonStudentsInEachCoursePair(Workbook workbook)
	{

		Sheet coursePairWiseCommonStudentsSheet = workbook.createSheet(FinalExamReportSheetNames.commonStudentsInEachCoursePair);
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

	public FinalExamReport(Day[]dateSheet, HashMap<String, CourseSchedule> courseSchedule, ProblemData problemData, String outputDirectoryPath)
	{
		this.dateSheet = dateSheet;
		this.outputDirectoryPath = outputDirectoryPath;
		this.problemData = problemData;
		this.dateSheet = dateSheet;
		this.courseSchedule = courseSchedule;
		this.findStudentsWithDirectExamClash();
		this.findStudentsWithMoreThan1ExamOnSameDay();
		this.findStudentsWithNoHolidayBetween2Exams();
		this.findStudentsWithNoHolidayBetweenExams();

		this.findCommonStudentsInEachCoursePair();


	}
}