package schedule;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import excel_reports.MidExamReportSheetNames;

public class MidDateSheetComparisonReport {

	String oldMidReportPath;
	String newMidReportPath;
	String outputPath;
	ArrayList<StudentExamClash> cr_StudentExamClashes;
	ArrayList<StudentWithMoreThan2ExamsPerDay> cr_threeExamsInOneDayList;
	ArrayList<StudentWithMoreThan2ExamsPerDay> cr_fourExamsInOneDayList;
	ArrayList<CourseWiseSummary> courseWiseSummaryList;

	static final int DARKRED = 0;
	static final int RED = 1;
	static final int LIGHTRED = 2;
	static final int YELLOW = 3;
	static final int GREEN = 4;


	private class CourseWiseSummary implements Comparable
	{
		public int count = 0;
		public String date = "";

		public ArrayList<String> courses = new ArrayList<String>();
		public int color;
		public int compareTo(Object arg) {
			CourseWiseSummary that = (CourseWiseSummary)arg;

			if (this == that)
			{
				return 0;
			}

			if (this.color < that.color)
			{
				return -1;
			}

			if (this.color > that.color)
			{
				return 1;
			}

			if (this.count > that.count)
			{
				return -1;
			}

			if (this.count < that.count)
			{
				return 1;
			}

			if (this.courses.get(0).compareTo(that.courses.get(0)) == 0)
			{
				if (this.courses.get(1).compareTo(that.courses.get(1)) == 0)
				{
					return this.courses.get(2).compareTo(that.courses.get(2));
				}

				return this.courses.get(1).compareTo(that.courses.get(1));
			}

			return this.courses.get(0).compareTo(that.courses.get(0));
		}

	}


	private class 	StudentWithMoreThan2ExamsPerDay implements Comparable //more than 2 exams on the same day.
	{
		public String rollNumber;
		public String name;
		public String degree;
		public String date;


		public ArrayList<String> coursesOnSameDay = new ArrayList<String>();
		public int color;


		@Override
		public int compareTo(Object arg0) {

			StudentWithMoreThan2ExamsPerDay that = (StudentWithMoreThan2ExamsPerDay)arg0;

			if (this == that)
			{
				return 0;
			}

			if (this.color < that.color)
				return -1;

			else if (this.color > that.color)
				return 1;

			else return this.rollNumber.compareTo(that.rollNumber);
		}


	}


	private class StudentExamClash implements Comparable
	{
		public String rollNumber;
		public String name;
		public String degree;
		public String date;
		public String time;

		ArrayList<String> coursesWithSameDayAndSlot = new ArrayList<String>();

		public int color = -1;

		@Override
		public int compareTo(Object arg0) {

			StudentExamClash that = (StudentExamClash)arg0;

			if (this == that)
			{
				return 0;
			}

			if (this.color < that.color)
				return -1;

			else if (this.color > that.color)
				return 1;

			else return this.rollNumber.compareTo(that.rollNumber);
		}

	}

	public MidDateSheetComparisonReport(String oldMidReportPath, String newMidReportPath, String outputPath)
	{
		this.cr_StudentExamClashes = new ArrayList<StudentExamClash>();
		this.cr_threeExamsInOneDayList = new ArrayList<StudentWithMoreThan2ExamsPerDay>();
		this.cr_fourExamsInOneDayList = new ArrayList<StudentWithMoreThan2ExamsPerDay>();
		this.courseWiseSummaryList = new ArrayList<CourseWiseSummary>();

		this.oldMidReportPath = oldMidReportPath;
		this.newMidReportPath = newMidReportPath;
		this.outputPath = outputPath;
	}


	private void generateCourseWiseSummaryComparisonReport(ArrayList<CourseWiseSummary> oldReport, ArrayList<CourseWiseSummary> newReport)
	{
		for (int i = 0; i < oldReport.size(); i++)
		{
			CourseWiseSummary oldClash = oldReport.get(i);
			boolean flag = false;
			for (int j = 0; j < newReport.size(); j++)
			{

				CourseWiseSummary newClash = newReport.get(j);

				if (oldClash.date.equals(newClash.date) && oldClash.courses.containsAll(newClash.courses) && newClash.courses.containsAll(oldClash.courses))
				{
					oldClash.color = YELLOW;
					flag = true;
					System.out.println("SAAD");
					break;
				}
			}

			if (flag == false)
				oldClash.color = GREEN;

			this.courseWiseSummaryList.add(oldClash);
		}


		for (int i = 0; i < newReport.size(); i++)
		{
			CourseWiseSummary newClash = newReport.get(i);
			boolean flag = false;
			for (int j = 0; j < oldReport.size(); j++)
			{

				CourseWiseSummary oldClash = oldReport.get(j);

				if (oldClash.date.equals(newClash.date) && oldClash.courses.containsAll(newClash.courses) && newClash.courses.containsAll(oldClash.courses))
				{

					flag = true;
					break;
				}
			}

			if (flag == false)
			{
				newClash.color = RED;

				this.courseWiseSummaryList.add(newClash);

			}

		}
	}

	//both 3 day and 4 day comparisons will make use of this function
	private void generateMoreThan2ExamsInOneDayComparisonReport(ArrayList<StudentWithMoreThan2ExamsPerDay> oldReport, ArrayList<StudentWithMoreThan2ExamsPerDay> newReport, ArrayList<StudentWithMoreThan2ExamsPerDay> comparisonList)
	{
		for (int i = 0; i < oldReport.size(); i++)
		{
			StudentWithMoreThan2ExamsPerDay oldClash = oldReport.get(i);
			boolean flag = false;
			for (int j = 0; j < newReport.size(); j++)
			{

				StudentWithMoreThan2ExamsPerDay newClash = newReport.get(j);

				if (oldClash.rollNumber.equals(newClash.rollNumber) && oldClash.date.equals(newClash.date) && oldClash.coursesOnSameDay.containsAll(newClash.coursesOnSameDay) && newClash.coursesOnSameDay.containsAll(oldClash.coursesOnSameDay))
				{
					oldClash.color = YELLOW;
					flag = true;
					break;
				}
			}

			if (flag == false)
				oldClash.color = GREEN;

			comparisonList.add(oldClash);
		}


		for (int i = 0; i < newReport.size(); i++)
		{
			StudentWithMoreThan2ExamsPerDay newClash = newReport.get(i);
			boolean flag = false;
			for (int j = 0; j < oldReport.size(); j++)
			{

				StudentWithMoreThan2ExamsPerDay oldClash = oldReport.get(j);

				if (oldClash.rollNumber.equals(newClash.rollNumber) && oldClash.date.equals(newClash.date) && oldClash.coursesOnSameDay.containsAll(newClash.coursesOnSameDay) && newClash.coursesOnSameDay.containsAll(oldClash.coursesOnSameDay))
				{

					flag = true;
					break;
				}
			}

			if (flag == false)
			{
				newClash.color = RED;

				comparisonList.add(newClash);

			}

		}

	}

	private void generateStudentExamClashComparisonReport(ArrayList<StudentExamClash> oldClashReport, ArrayList<StudentExamClash> newClashReport)
	{

		for (int i = 0; i < oldClashReport.size(); i++)
		{
			StudentExamClash oldClash = oldClashReport.get(i);
			boolean flag = false;
			for (int j = 0; j < newClashReport.size(); j++)
			{

				StudentExamClash newClash = newClashReport.get(j);

				if (oldClash.rollNumber.equals(newClash.rollNumber) && oldClash.date.equals(newClash.date) && oldClash.coursesWithSameDayAndSlot.containsAll(newClash.coursesWithSameDayAndSlot) && newClash.coursesWithSameDayAndSlot.containsAll(oldClash.coursesWithSameDayAndSlot))
				{
					oldClash.color = YELLOW;
					flag = true;
					break;
				}
			}

			if (flag == false)
				oldClash.color = GREEN;

			this.cr_StudentExamClashes.add(oldClash);
		}

		//System.out.println(cr_StudentExamClashes.size());
		for (int i = 0; i < newClashReport.size(); i++)
		{
			StudentExamClash newClash = newClashReport.get(i);
			boolean flag = false;
			for (int j = 0; j < oldClashReport.size(); j++)
			{

				StudentExamClash oldClash = oldClashReport.get(j);

				if (oldClash.rollNumber.equals(newClash.rollNumber) && oldClash.date.equals(newClash.date) && oldClash.coursesWithSameDayAndSlot.containsAll(newClash.coursesWithSameDayAndSlot) && newClash.coursesWithSameDayAndSlot.containsAll(oldClash.coursesWithSameDayAndSlot))
				{

					flag = true;
					break;
				}
			}

			if (flag == false)
			{
				newClash.color = RED;

				this.cr_StudentExamClashes.add(newClash);
			}

		}



	}

	public String readMidReportsAndGenerateComparisonReport()
	{

		Workbook oldMidReportWorkBook = null;
		try {
			System.out.println("Old Path " + this.oldMidReportPath);
			oldMidReportWorkBook = WorkbookFactory.create(new File(this.oldMidReportPath));
		}
		catch (Exception e)
		{
			return "An exception occurred in opening old mid report. The report may not exist or you may not have the permission to read it.";
		}


		Workbook newMidReportWorkBook;
		try {
			System.out.println("New Path " + this.newMidReportPath);
			newMidReportWorkBook = WorkbookFactory.create(new File(this.newMidReportPath));
		}
		catch (Exception e)
		{
			return "An exception occurred in opening new mid report. The report may not exist or you may not have the permission to read it.";
		}

		if (oldMidReportWorkBook.getSheet(MidExamReportSheetNames.studentExamClashes) == null)
		{

			return MidExamReportSheetNames.studentExamClashes + " sheet does not exist in old mid term report.";
		}

		if (newMidReportWorkBook.getSheet(MidExamReportSheetNames.studentExamClashes) == null)
		{
			return MidExamReportSheetNames.studentExamClashes + " sheet does not exist in new mid term report.";
		}

		if (oldMidReportWorkBook.getSheet(MidExamReportSheetNames.threeExams) == null)
		{
			return MidExamReportSheetNames.threeExams + " sheet does not exist in old mid term report.";
		}

		if (newMidReportWorkBook.getSheet(MidExamReportSheetNames.threeExams) == null)
		{
			return MidExamReportSheetNames.threeExams + " sheet does not exist in new mid term report.";
		}

		if (oldMidReportWorkBook.getSheet(MidExamReportSheetNames.fourExams) == null)
		{
			return MidExamReportSheetNames.fourExams + " sheet does not exist in old mid term report.";
		}

		if (newMidReportWorkBook.getSheet(MidExamReportSheetNames.fourExams) == null)
		{
			return MidExamReportSheetNames.fourExams + " sheet does not exist in new mid term report.";
		}

		if (oldMidReportWorkBook.getSheet(MidExamReportSheetNames.courseWiseSummary) == null)
		{
			return MidExamReportSheetNames.courseWiseSummary + " sheet does not exist in old mid term report.";
		}

		if (newMidReportWorkBook.getSheet(MidExamReportSheetNames.courseWiseSummary) == null)
		{
			return MidExamReportSheetNames.courseWiseSummary + " sheet does not exist in new mid term report.";
		}

		//reading student Exam clash sheet from both reports and generating comparison report.
		ArrayList<StudentExamClash> oldStudentExamClashes = new ArrayList<StudentExamClash>();
		String message = this.readMidReportStudentExamClashes(oldMidReportWorkBook.getSheet(MidExamReportSheetNames.studentExamClashes), oldStudentExamClashes);

		if (!message.equals(""))
		{
			return message;
		}

		ArrayList<StudentExamClash> newStudentExamClashes = new ArrayList<StudentExamClash>();

		message = this.readMidReportStudentExamClashes(newMidReportWorkBook.getSheet(MidExamReportSheetNames.studentExamClashes), newStudentExamClashes);

		if (!message.equals(""))
		{
			return message;
		}


		this.generateStudentExamClashComparisonReport(oldStudentExamClashes, newStudentExamClashes);



		//reading 3 exams per day sheet from both reports and generating comparison sheet.
		ArrayList<StudentWithMoreThan2ExamsPerDay> old3ExamsSheet = new ArrayList<StudentWithMoreThan2ExamsPerDay>();
		message = this.readMoreThan2ExamsInOneDay(oldMidReportWorkBook.getSheet(MidExamReportSheetNames.threeExams), old3ExamsSheet);
		if (!message.equals(""))
		{
			return message;
		}


		ArrayList<StudentWithMoreThan2ExamsPerDay> new3ExamsSheet = new ArrayList<StudentWithMoreThan2ExamsPerDay>();
		message = this.readMoreThan2ExamsInOneDay(newMidReportWorkBook.getSheet(MidExamReportSheetNames.threeExams), new3ExamsSheet);
		if (!message.equals(""))
		{
			return message;
		}

		this.generateMoreThan2ExamsInOneDayComparisonReport(old3ExamsSheet, new3ExamsSheet, this.cr_threeExamsInOneDayList);



		//reading 4 exams per day sheet from both reports and generating comparison report
		ArrayList<StudentWithMoreThan2ExamsPerDay> old4ExamsSheet = new ArrayList<StudentWithMoreThan2ExamsPerDay>();
		message = this.readMoreThan2ExamsInOneDay(oldMidReportWorkBook.getSheet(MidExamReportSheetNames.fourExams), old4ExamsSheet);
		if (!message.equals(""))
		{
			return message;
		}


		ArrayList<StudentWithMoreThan2ExamsPerDay> new4ExamsSheet = new ArrayList<StudentWithMoreThan2ExamsPerDay>();
		message = this.readMoreThan2ExamsInOneDay(newMidReportWorkBook.getSheet(MidExamReportSheetNames.fourExams), new4ExamsSheet);
		if (!message.equals(""))
		{
			return message;
		}

		this.generateMoreThan2ExamsInOneDayComparisonReport(old4ExamsSheet, new4ExamsSheet, this.cr_fourExamsInOneDayList);


		//reading course wise summary report
		ArrayList<CourseWiseSummary> oldCourseWiseSummaryList = new ArrayList<CourseWiseSummary>();
		message = this.readSummaryReport(oldMidReportWorkBook.getSheet(MidExamReportSheetNames.courseWiseSummary), oldCourseWiseSummaryList);
		if (!message.equals(""))
		{
			return message;
		}

		ArrayList<CourseWiseSummary> newCourseWiseSummaryList = new ArrayList<CourseWiseSummary>();
		message = this.readSummaryReport(newMidReportWorkBook.getSheet(MidExamReportSheetNames.courseWiseSummary), newCourseWiseSummaryList);
		if (!message.equals(""))
		{
			return message;
		}

		this.generateCourseWiseSummaryComparisonReport(oldCourseWiseSummaryList, newCourseWiseSummaryList);

		this.findStudentsWhoWentFrom4ExamsTo3ExamsPerDay(new3ExamsSheet, old4ExamsSheet);
		this.findStudentsWhoWentFrom3ExamsTo4ExamsPerDay(new4ExamsSheet, old3ExamsSheet);
		return this.exportToExcel();
	}


	public void findStudentsWhoWentFrom3ExamsTo4ExamsPerDay(ArrayList<StudentWithMoreThan2ExamsPerDay> new4ExamSheet, ArrayList<StudentWithMoreThan2ExamsPerDay> old3ExamSheet)
	{
		for (int i = 0; i < new4ExamSheet.size(); i++)
		{
			for (int j = 0; j < old3ExamSheet.size(); j++)
			{
				if (new4ExamSheet.get(i).rollNumber.equals(old3ExamSheet.get(j).rollNumber))
				{
					if (new4ExamSheet.get(i).coursesOnSameDay.containsAll(old3ExamSheet.get(j).coursesOnSameDay))
					{
						new4ExamSheet.get(i).color = DARKRED;
						break;
					}

				}
			}
		}
	}


	public void findStudentsWhoWentFrom4ExamsTo3ExamsPerDay(ArrayList<StudentWithMoreThan2ExamsPerDay> new3ExamSheet, ArrayList<StudentWithMoreThan2ExamsPerDay> old4ExamSheet)
	{
		for (int i = 0; i < old4ExamSheet.size(); i++)
		{
			for (int j = 0; j < new3ExamSheet.size(); j++)
			{
				if (old4ExamSheet.get(i).rollNumber.equals(new3ExamSheet.get(j).rollNumber))
				{
					if (old4ExamSheet.get(i).coursesOnSameDay.containsAll(new3ExamSheet.get(j).coursesOnSameDay))
					{
						new3ExamSheet.get(j).color = LIGHTRED;
						break;
					}

				}
			}
		}
	}
	
	public void printStudentsWithExamClash(XSSFWorkbook workbook)
	{

		Collections.sort(this.cr_StudentExamClashes);
		//Student Wise Exam Clash (more than 1 exam in the same slot)
		XSSFSheet studentExamClashSheet = workbook.createSheet(MidExamReportSheetNames.studentExamClashes);


		int maxColumns = 0;
		for (int i = 0; i < this.cr_StudentExamClashes.size(); i++)
		{

			if (maxColumns < this.cr_StudentExamClashes.get(i).coursesWithSameDayAndSlot.size())
				maxColumns = this.cr_StudentExamClashes.get(i).coursesWithSameDayAndSlot.size();

			XSSFRow row = studentExamClashSheet.createRow(i);

			XSSFCellStyle cellStyle = workbook.createCellStyle();

			if (this.cr_StudentExamClashes.get(i).color == GREEN)
				cellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());

			else if (this.cr_StudentExamClashes.get(i).color == RED)
				cellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());


			else if (this.cr_StudentExamClashes.get(i).color == YELLOW)
			{
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
				///cellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 109, 109), new DefaultIndexedColorMap()));
				 //cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			}
			cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			Cell cell = row.createCell(0);
			cell.setCellValue(this.cr_StudentExamClashes.get(i).rollNumber);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(1);
			cell.setCellValue(this.cr_StudentExamClashes.get(i).name);
			// studentExamClashSheet.autoSizeColumn(1);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(2);
			cell.setCellValue(this.cr_StudentExamClashes.get(i).degree);
			// studentExamClashSheet.autoSizeColumn(2);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(3);
			cell.setCellValue(this.cr_StudentExamClashes.get(i).date);
			// studentExamClashSheet.autoSizeColumn(3);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(4);
			cell.setCellValue(this.cr_StudentExamClashes.get(i).time);
			// studentExamClashSheet.autoSizeColumn(4);
			cell.setCellStyle(cellStyle);


			for (int j = 0; j < this.cr_StudentExamClashes.get(i).coursesWithSameDayAndSlot.size(); j++)
			{
				cell = row.createCell(j + 5);
				cell.setCellValue(this.cr_StudentExamClashes.get(i).coursesWithSameDayAndSlot.get(j));
				cell.setCellStyle(cellStyle);
				// studentExamClashSheet.autoSizeColumn(j+5);


			}

		}

		for (int i = 0; i < maxColumns + 5; i++)
		{
			studentExamClashSheet.autoSizeColumn(i);
		}

	}

	private void printStudentsWith4ExamsPerDay(XSSFWorkbook workbook)
	{
		Collections.sort(this.cr_fourExamsInOneDayList);

		XSSFSheet studentsMoreThan2ExamsSheet = workbook.createSheet(MidExamReportSheetNames.fourExams);

		int maxColumns = 0;
		int rowNo = 0;
		for (int i = 0; i < this.cr_fourExamsInOneDayList.size(); i++)
		{
			if (this.cr_fourExamsInOneDayList.get(i).coursesOnSameDay.size() != 4)
				continue;

			if (maxColumns < this.cr_fourExamsInOneDayList.get(i).coursesOnSameDay.size())
				maxColumns = this.cr_fourExamsInOneDayList.get(i).coursesOnSameDay.size();

			XSSFRow row = studentsMoreThan2ExamsSheet.createRow(rowNo++);


			XSSFCellStyle cellStyle = workbook.createCellStyle();

			if (this.cr_fourExamsInOneDayList.get(i).color == GREEN)
				cellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());

			else if (this.cr_fourExamsInOneDayList.get(i).color == RED)
				cellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());

			else if (this.cr_fourExamsInOneDayList.get(i).color == DARKRED)
			{
				cellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(153, 0, 0), new DefaultIndexedColorMap()));

			}
			else if (this.cr_fourExamsInOneDayList.get(i).color == YELLOW)
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());

			cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);


			Cell cell = row.createCell(0);
			cell.setCellValue(this.cr_fourExamsInOneDayList.get(i).rollNumber);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(1);
			cell.setCellValue(this.cr_fourExamsInOneDayList.get(i).name);
			// studentExamClashSheet.autoSizeColumn(1);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(2);
			cell.setCellValue(this.cr_fourExamsInOneDayList.get(i).degree);
			// studentExamClashSheet.autoSizeColumn(2);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(3);
			cell.setCellValue(this.cr_fourExamsInOneDayList.get(i).date);
			// studentExamClashSheet.autoSizeColumn(3);
			cell.setCellStyle(cellStyle);




			for (int j = 0; j < this.cr_fourExamsInOneDayList.get(i).coursesOnSameDay.size(); j++)
			{
				cell = row.createCell(j + 4);
				cell.setCellValue(this.cr_fourExamsInOneDayList.get(i).coursesOnSameDay.get(j));
				// studentExamClashSheet.autoSizeColumn(j+5);
				cell.setCellStyle(cellStyle);

			}

		}

		for (int i = 0; i < maxColumns + 4; i++)
		{
			studentsMoreThan2ExamsSheet.autoSizeColumn(i);
		}



	}

	private void printStudentsWith3ExamsPerDay(XSSFWorkbook workbook)
	{
		Collections.sort(this.cr_threeExamsInOneDayList);
		XSSFSheet studentsMoreThan2ExamsSheet = workbook.createSheet(MidExamReportSheetNames.threeExams);

		int maxColumns = 0;
		int rowNo = 0;
		for (int i = 0; i < this.cr_threeExamsInOneDayList.size(); i++)
		{
			if (this.cr_threeExamsInOneDayList.get(i).coursesOnSameDay.size() != 3)
				continue;

			if (maxColumns < this.cr_threeExamsInOneDayList.get(i).coursesOnSameDay.size())
				maxColumns = this.cr_threeExamsInOneDayList.get(i).coursesOnSameDay.size();

			XSSFRow row = studentsMoreThan2ExamsSheet.createRow(rowNo++);


			XSSFCellStyle cellStyle = workbook.createCellStyle();

			if (this.cr_threeExamsInOneDayList.get(i).color == GREEN)
				cellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());

			else if (this.cr_threeExamsInOneDayList.get(i).color == RED)
				cellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());

			else if (this.cr_threeExamsInOneDayList.get(i).color == LIGHTRED)
			{
				cellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 204, 204), new DefaultIndexedColorMap()));
			}

			else if (this.cr_threeExamsInOneDayList.get(i).color == YELLOW)
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());

			cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);


			XSSFCell cell = row.createCell(0);
			cell.setCellValue(this.cr_threeExamsInOneDayList.get(i).rollNumber);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(1);
			cell.setCellValue(this.cr_threeExamsInOneDayList.get(i).name);
			// studentExamClashSheet.autoSizeColumn(1);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(2);
			cell.setCellValue(this.cr_threeExamsInOneDayList.get(i).degree);
			// studentExamClashSheet.autoSizeColumn(2);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(3);
			cell.setCellValue(this.cr_threeExamsInOneDayList.get(i).date);
			// studentExamClashSheet.autoSizeColumn(3);
			cell.setCellStyle(cellStyle);




			for (int j = 0; j < this.cr_threeExamsInOneDayList.get(i).coursesOnSameDay.size(); j++)
			{
				cell = row.createCell(j + 4);
				cell.setCellValue(this.cr_threeExamsInOneDayList.get(i).coursesOnSameDay.get(j));
				// studentExamClashSheet.autoSizeColumn(j+5);
				cell.setCellStyle(cellStyle);

			}

		}

		for (int i = 0; i < maxColumns + 4; i++)
		{
			studentsMoreThan2ExamsSheet.autoSizeColumn(i);
		}


	}


	private void print3CoursesOnTheSameDaySummary(Workbook workbook)
	{
		Collections.sort(this.courseWiseSummaryList);
		Sheet courseWiseSummary = workbook.createSheet(MidExamReportSheetNames.courseWiseSummary);

		int rowNo = 0;
		for (int i = 0; i < this.courseWiseSummaryList.size(); i++)
		{


			Row row = courseWiseSummary.createRow(rowNo++);

			CellStyle cellStyle = workbook.createCellStyle();

			if (this.courseWiseSummaryList.get(i).color == GREEN)
				cellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());

			else if (this.courseWiseSummaryList.get(i).color == RED)
				cellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());


			else if (this.courseWiseSummaryList.get(i).color == YELLOW)
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());

			cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			Cell cell = row.createCell(0);
			cell.setCellValue(this.courseWiseSummaryList.get(i).date);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(1);
			cell.setCellValue(this.courseWiseSummaryList.get(i).courses.get(0));
			// studentExamClashSheet.autoSizeColumn(1);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(2);
			cell.setCellValue(this.courseWiseSummaryList.get(i).courses.get(1));
			// studentExamClashSheet.autoSizeColumn(2);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(3);
			cell.setCellValue(this.courseWiseSummaryList.get(i).courses.get(2));
			// studentExamClashSheet.autoSizeColumn(3);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(4);
			cell.setCellValue(this.courseWiseSummaryList.get(i).count);
			cell.setCellStyle(cellStyle);



		}

		for (int i = 0; i < 4; i++)
		{
			courseWiseSummary.autoSizeColumn(i);
		}

	}
	
	public String exportToExcel()
	{
		try {
			XSSFWorkbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file
			FileOutputStream fileOut = new FileOutputStream(this.outputPath + "\\Mid Term Comparison Report.xlsx");


			this.printStudentsWithExamClash(workbook);
			this.printStudentsWith3ExamsPerDay(workbook);
			this.printStudentsWith4ExamsPerDay(workbook);
			//this.printStudentsWithMoreThan4ExamsPerDay(workbook);
			this.print3CoursesOnTheSameDaySummary(workbook);

			//this.printAllCourses(workbook);
			//this.printScheduledCourses(workbook);
			//this.printUnscheduledCourses(workbook);
			//this.printExcludedCourses(workbook);
			//this.printTotalStudentsScheduledInEachSlot(workbook);



			workbook.write(fileOut);
			workbook.close();
			fileOut.close();

			return "";

		}
		catch (Exception e) {

			return "An error occurred while exporting the mid term comparison report to excel.";
		}

	}

	public String readSummaryReport(Sheet sheet, ArrayList<CourseWiseSummary> list)
	{
		try
		{
			XSSFSheet summarySheet = (XSSFSheet)sheet;

			Iterator<Row> rowIterator = summarySheet.rowIterator();
			while (rowIterator.hasNext())
			{
				Row row = rowIterator.next();

				CourseWiseSummary clash = new CourseWiseSummary();
				// Now let's iterate over the columns of the current row

				Iterator<Cell> cellIterator = row.cellIterator();
				Cell cell = cellIterator.next(); //date
				clash.date = cell.getStringCellValue();

				cell = cellIterator.next();
				clash.courses.add(cell.getStringCellValue()); //course1

				cell = cellIterator.next();
				clash.courses.add(cell.getStringCellValue()); //course2

				cell = cellIterator.next();
				clash.courses.add(cell.getStringCellValue()); //course3


				cell = cellIterator.next(); //count
				clash.count = (int)(cell.getNumericCellValue());



				list.add(clash);
			}


		}
		catch (Exception e)
		{
			return "An error occurred in reading " + sheet.getSheetName() + " sheet.";
		}

		return "";


	}


	//both 3 exam and 4 exam sheets will make use of this function
	public String readMoreThan2ExamsInOneDay(Sheet sheet, ArrayList<StudentWithMoreThan2ExamsPerDay> list)
	{
		try
		{
			XSSFSheet studentExamClashesSheet = (XSSFSheet)sheet;

			Iterator<Row> rowIterator = studentExamClashesSheet.rowIterator();
			while (rowIterator.hasNext())
			{
				Row row = rowIterator.next();

				StudentWithMoreThan2ExamsPerDay clash = new StudentWithMoreThan2ExamsPerDay();
				// Now let's iterate over the columns of the current row

				Iterator<Cell> cellIterator = row.cellIterator();
				Cell cell = cellIterator.next(); //rollNumber
				clash.rollNumber = cell.getStringCellValue();

				cell = cellIterator.next();
				clash.name = cell.getStringCellValue(); //name

				cell = cellIterator.next();
				clash.degree = cell.getStringCellValue(); //degree

				cell = cellIterator.next(); //date
				clash.date = cell.getStringCellValue();

				while (cellIterator.hasNext())
				{
					cell = cellIterator.next(); //1st course
					clash.coursesOnSameDay.add(cell.getStringCellValue());
				}

				list.add(clash);
			}

		}
		catch (Exception e)
		{
			return "An error occurred in reading " + sheet.getSheetName() + " sheet.";
		}

		return "";

	}


	public String readMidReportStudentExamClashes(Sheet sheet, ArrayList<StudentExamClash> studentExamClashList)
	{


		try
		{
			XSSFSheet studentExamClashesSheet = (XSSFSheet)sheet;

			Iterator<Row> rowIterator = studentExamClashesSheet.rowIterator();
			while (rowIterator.hasNext())
			{
				Row row = rowIterator.next();

				StudentExamClash clash = new StudentExamClash();
				// Now let's iterate over the columns of the current row

				Iterator<Cell> cellIterator = row.cellIterator();
				Cell cell = cellIterator.next(); //rollNumber
				clash.rollNumber = cell.getStringCellValue();

				cell = cellIterator.next();
				clash.name = cell.getStringCellValue(); //name

				cell = cellIterator.next();
				clash.degree = cell.getStringCellValue(); //degree

				cell = cellIterator.next(); //date
				clash.date = cell.getStringCellValue();

				cell = cellIterator.next(); //time
				clash.time = cell.getStringCellValue();


				while (cellIterator.hasNext())
				{
					cell = cellIterator.next();
					clash.coursesWithSameDayAndSlot.add(cell.getStringCellValue());
				}


				studentExamClashList.add(clash);

			}



		}
		catch (Exception e)
		{
			return "An error occurred in reading " + MidExamReportSheetNames.studentExamClashes + " sheet.";
		}

		return "";
	}

}
