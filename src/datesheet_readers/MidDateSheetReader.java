package datesheet_readers;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import exam_input_data_classes.DateHelper;
import exam_input_data_classes.ProblemData;
import excel_reports.MidExamReport;
import ga_classes.CourseSchedule;
import ga_classes.Day;
import ga_classes.Slot;

public class MidDateSheetReader {

	private Day[]dateSheet;
	private ProblemData problemData;

	private HashMap<String, CourseSchedule> courseSchedule;
	String inputStudentCourseFilePath;
	String inputMidDateSheetPath;
	String outputDirectoryPath;
	HashSet<String> courseIdOfScheduledCourses;

	public MidDateSheetReader(String inputStudentCoursefilePath, String inputMidDateSheetPath, String outputDirectoryPath)
	{
		this.inputStudentCourseFilePath = inputStudentCoursefilePath;
		this.inputMidDateSheetPath = inputMidDateSheetPath;
		this.outputDirectoryPath = outputDirectoryPath;

	}

	public String readStudentCourseFileAndMidTermDateSheetAndGenerateReport()
	{

		this.problemData = new ProblemData(this.inputStudentCourseFilePath, "mid");
		String status = this.problemData.readDataFromExcelFile();

		if (status != null)
			return status; //an error has occurred while reading data from excel file


		this.dateSheet = new Day[this.problemData.getTotalDays()];
		courseSchedule = new HashMap<String, CourseSchedule>();
		this.courseIdOfScheduledCourses = new HashSet<String>();


		for (int i = 0; i < this.problemData.getTotalDays(); i++)
		{
			dateSheet[i] = new Day(this.problemData.getTotalSlotsPerDay());
			for (int j = 0; j < this.problemData.getTotalSlotsPerDay(); j++)
				dateSheet[i].getSlots()[j] = new Slot();

		}

		status = inputMidDateSheetReader();//read datesheet
		if (status.equals("") == false)
		{
			return status;
		}

		MidExamReport report = new MidExamReport(this.dateSheet, this.courseSchedule, this.problemData, this.outputDirectoryPath + "\\Mid Exam Report.xlsx");
		return report.exportToExcel();

	}

	protected String inputMidDateSheetReader()
	{
		try {
			Workbook workbook = WorkbookFactory.create(new File(this.inputMidDateSheetPath));
			if (workbook.getSheet("Complete") == null)
			{
				return "The complete sheet of datesheet is missing!";

			}

			XSSFSheet completeDateSheet = (XSSFSheet)workbook.getSheet("Complete");



			Iterator<Row> rowIterator = completeDateSheet.rowIterator();

			rowIterator.next();//ignore headers
			rowIterator.next();
			rowIterator.next();
			int day = 0;

			while (rowIterator.hasNext()) {

				Row row = rowIterator.next();

				// Now let's iterate over the columns of the current row

				Iterator<Cell> cellIterator = row.cellIterator();
				Cell cell = cellIterator.next(); //day


				while (cell.getStringCellValue().equals("") == false)
				{

					if (cell.getStringCellValue().toLowerCase().equals(DateHelper.getDayName(this.problemData.getExamDates().get(day)).toLowerCase()) == false)
					{
						return "Incorrect Day Name in date sheet detected!";

					}
					cell = cellIterator.next(); //date


					DataFormatter dataFormatter = new DataFormatter();
					String date = dataFormatter.formatCellValue(cell);
					SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
					Date dateData = format.parse(date.toString());
					date = format.format(dateData);



					if (date.toLowerCase().equals(this.problemData.getExamDates().get(day).toLowerCase()) == false)
					{
						return "Incorrect date in date sheet detected!";

					}

					for (int slot = 0; slot < this.problemData.getTotalSlotsPerDay(); slot++)
					{
						cell = cellIterator.next(); //courseCode
						String courseCode = cell.getStringCellValue();

						cell = cellIterator.next(); //course Name
						String courseName = cell.getStringCellValue();

						if (courseCode.equals(""))
						{
							continue;
						}

						else
						{
							//it has been commented out, otherwise MS level courses are put in the false courses list.
							//if ( this.courses.containsKey(courseCode)==false || (this.courses.get(courseCode).toBeScheduled==false) )
							//{
								//this.falseCourses.put(courseCode, new FalseCourse(courseCode, courseName));
							//}

							if (this.courseIdOfScheduledCourses.contains(courseCode))
							{
								return "The course: " + this.problemData.getAllCourses().get(courseCode).getCourseName() + "(" + courseCode + ") has been scheduled twice. Please Remove this error!";

							}

							else
							{
								this.courseIdOfScheduledCourses.add(courseCode);
								this.problemData.getAllCourses().get(courseCode).setToBeScheduled(true); //this line has been added otherwise MS level courses cause problems
								CourseSchedule cs = new CourseSchedule(courseCode, slot, day);
								this.courseSchedule.put(courseCode, cs);
								this.dateSheet[day].getSlots()[slot].getCoursesScheduled().add(courseCode);
							}

						}
					}
					row = rowIterator.next();
					cellIterator = row.cellIterator();
					cell = cellIterator.next(); //day
				}

				day++;


			}


			return "";

		}
		catch (EncryptedDocumentException e) {
			// TODO Auto-generated catch block
			return "The date sheet is encrypted.";
		}
		catch (IOException e) {
			return "An error occurred while reading date sheet. The date sheet file may be missing, or permissions may be required to read it.";

		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			return "An error occurred while reading date sheet. (Parse Error)";
		}
		catch (Exception e)
		{
			return "Date Sheet Format is Incorrect. Please correct the format!"; //the exception can occur when the file is there but some cell has incorrect format.
		}
	}
}
