package exam_input_data_classes;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;



public class ProblemData {

	private String inputFilePath;
	private String semesterName;

	private HashMap<String, Course> allCourses; //contains a list of all courses
	private HashMap<String, Student> allStudents;
	private ArrayList<String> coursesToBeScheduled; //contains course id of courses that are to be scheduled

	private HashMap<String, String> holidays;
	private ArrayList<String> examDates;

	private ArrayList<String> slotTimings;
	private int totalDays;
	private int totalSlotsPerDay;
	private int seatingCapacityPerSlot;
	private String examStartDate;
	private boolean saturdayOff;
	private String examType;
	private HashMap<String, PreferenceDaySlot> preferences;

	public class PreferenceDaySlot
	{
		String courseId;
		int day;
		int slot;

		public PreferenceDaySlot(String courseId, int day, int slot)
		{
			this.courseId = courseId;
			this.day = day;
			this.slot = slot;
		}

		public String getCourseId() {
			return courseId;
		}
		public void setCourseId(String courseId) {
			this.courseId = courseId;
		}
		public int getDay() {
			return day;
		}
		public void setDay(int day) {
			this.day = day;
		}
		public int getSlot() {
			return slot;
		}
		public void setSlot(int slot) {
			this.slot = slot;
		}

	}
	public ProblemData(String inputFilePath, String examType)
	{
		this.inputFilePath = inputFilePath;
		this.preferences = new HashMap<String, PreferenceDaySlot>();

		this.allCourses = new HashMap<String, Course>();
		this.allStudents = new HashMap<String, Student>();
		this.coursesToBeScheduled = new ArrayList<String>();
		this.holidays = new HashMap<String, String>();
		this.examDates = new ArrayList<String>();
		this.slotTimings = new ArrayList<String>();
		this.examType = examType;

	}


	public ArrayList<String> getSlotTimings()
	{
		return this.slotTimings;
	}

	protected String readSlotTimings(Workbook workbook)
	{
		XSSFSheet slotTimingsSheet = (XSSFSheet)workbook.getSheet("SlotTimings");

		if (slotTimingsSheet == null)
			return "SlotTimings sheet not found!";

		Iterator<Row> rowIterator = slotTimingsSheet.rowIterator();

		Row row = null;
		short i = 0;
		try
		{
			while (rowIterator.hasNext()) {

				row = rowIterator.next();

				Iterator<Cell> cellIterator = row.cellIterator();
				Cell cell = cellIterator.next();
				String currslotTiming = cell.getStringCellValue();
				this.slotTimings.add(currslotTiming);
				i++;

			}
		}

		catch (Exception e)
		{
			System.out.println("ERROR");
			return "An error occurred in reading SlotTimings Sheet.";
		}

		if (i < this.totalSlotsPerDay)
			return "Total Slots are " + this.totalSlotsPerDay + ". However, the timings of only " + i + " slots are given.";

		return null;
	}

	protected void populateExamDates()
	{
		this.examDates.add(this.examStartDate);//0th day's date.

		for (short i = 1; i < this.totalDays; i++)
		{
			boolean flag = false;
			String date = null;
			String previousDate = this.examDates.get(i - 1);
			while (flag == false)
			{
				date = DateHelper.getNextDate(previousDate);
				if (this.holidays.containsKey(date) || DateHelper.getDayName(date).toLowerCase().equals("sunday") || (this.saturdayOff && DateHelper.getDayName(date).toLowerCase().equals("saturday")))
				{
					previousDate = date;
					continue;
				}

				flag = true;
			}

			this.examDates.add(date);
		}
	}

	public String readDataFromExcelFile()
	{

		Workbook workbook;
		try {
			workbook = WorkbookFactory.create(new File(inputFilePath));
			DataFormatter dataFormatter = new DataFormatter();

			if (workbook.getSheet("Settings") == null)
			{
				return "Settings sheet not found.";

			}

			if (workbook.getSheetIndex("StudentCourses") == -1)
			{
				return "StudentCourses sheet not found.";

			}

			this.examStartDate = this.readSettingValue(workbook.getSheet("Settings"), "ExamStartDate");

			if (this.examStartDate == null || this.examStartDate.equals(""))
			{
				return "ExamStartDate not found in settings sheet.";

			}

			String capacity = this.readSettingValue(workbook.getSheet("Settings"), "Capacity");
			if (capacity == null || capacity.equals(""))
			{
				return "Capacity not found in settings sheet.";
			}

			try {
				this.seatingCapacityPerSlot = Integer.valueOf(capacity);

			}
			catch (NumberFormatException e)
			{
				return "The value in capacity is in incorrect format.";
			}

			this.semesterName = this.readSettingValue(workbook.getSheet("Settings"), "SemesterName");
			if (this.semesterName == null || this.semesterName.equals(""))
			{
				return "SemesterName not found in settings sheet.";
			}

			String days = this.readSettingValue(workbook.getSheet("Settings"), "Days");

			if (days == null || days.equals(""))
			{
				return "Days not found in settings sheet.";

			}

			try
			{
				this.totalDays = Integer.parseInt(days);
			}
			catch (NumberFormatException e)
			{
				return "the value in days is in incorrect format.";

			}

			String slots = this.readSettingValue(workbook.getSheet("Settings"), "Slots");

			if (slots == null || slots.equals(""))
			{
				return "slots not found in settings sheet.";

			}

			try
			{
				this.totalSlotsPerDay = Integer.parseInt(slots);
			}
			catch (NumberFormatException e)
			{
				return "the value in slots is in incorrect format.";

			}

			String saturdayOff = this.readSettingValue(workbook.getSheet("Settings"), "SaturdayOff");

			if (saturdayOff == null || saturdayOff.equals(""))
			{
				return "saturdayOff not found in settings sheet.";
			}

			if (saturdayOff.toLowerCase().equals("yes"))
			{
				this.saturdayOff = true;
			}
			else if (saturdayOff.toLowerCase().equals("no"))
			{
				this.saturdayOff = false;
			}

			else return "incorrect value of saturdayOff";


			if (this.examType.equals("final"))
			{

				if (readHolidaysForFinalExams(workbook.getSheet("Settings"), "Holiday") == false)
					return "An error occurred in reading holidays.";
			}

			XSSFSheet studentCoursesSheet = (XSSFSheet)workbook.getSheet("StudentCourses");

			Iterator<Row> rowIterator = studentCoursesSheet.rowIterator();

			Row row = rowIterator.next();//ignore header

			while (rowIterator.hasNext()) {
				row = rowIterator.next();
				//System.out.println(i);

				// Now let's iterate over the columns of the current row
				Iterator<Cell> cellIterator = row.cellIterator();

				//cellIterator.next(); //ignore Sr

				//student Roll number
				Cell cell = cellIterator.next();
				String rollNumber = dataFormatter.formatCellValue(cell);
				//System.out.println(rollNumber);\
			            
						//student name
				cell = cellIterator.next();
				String studentName = dataFormatter.formatCellValue(cell);
				//System.out.println(studentName);

			  //CourseId
				cell = cellIterator.next();
				String courseId = dataFormatter.formatCellValue(cell);
				//System.out.println(courseId);

			  //Course Name
				cell = cellIterator.next();
				String courseName = dataFormatter.formatCellValue(cell);
				//System.out.println(courseName);


				cellIterator.next(); //ignore course relation for now.

			  //section
				cell = cellIterator.next();
				String sectionName = dataFormatter.formatCellValue(cell);
				//	System.out.println(section);


				 //student degree
				cell = cellIterator.next();
				String degree = dataFormatter.formatCellValue(cell);
				//System.out.println(degree);


				//Teacher
				cell = cellIterator.next();
				String teacherName = dataFormatter.formatCellValue(cell);

				boolean toBeScheduled = true;
				//do not schedule thesis, project, and lab courses for mid term as well as final exams.
				if (courseName.toLowerCase().contains("lab") || courseName.toLowerCase().contains("thesis") || courseName.toLowerCase().contains("project -") || courseName.toLowerCase().contains("project-i"))
				{

					toBeScheduled = false;
				}

				if (this.examType.equals("mid") && (courseId.toCharArray()[2] >= '5' || courseName.compareToIgnoreCase("Research Methodology") == 0 || courseName.compareToIgnoreCase("Data Mining") == 0))
				{
					toBeScheduled = false;
				}

				Course c = null;
				Student st = null;

				//if the course is already added in the hashmap
				if (this.allCourses.containsKey(courseId))
				{
					c = this.allCourses.get(courseId);
					if (c.toBeScheduled == false)
						c.toBeScheduled = toBeScheduled;

				}
				else //else if course is not already added in the hashmap.
				{
					c = new Course(courseId, courseName, toBeScheduled);
					this.allCourses.put(courseId, c);
					if (toBeScheduled)
					{
						this.coursesToBeScheduled.add(courseId);
					}

				}

				if (this.allStudents.containsKey(rollNumber))
				{
					st = this.allStudents.get(rollNumber);
				}
				else //if student does not exist in the hashmap, add.
				{
					st = new Student(rollNumber, studentName, degree);

					this.allStudents.put(rollNumber, st);
				}


				st.getCourses().add(courseId);
				c.getStudentRollNumbers().add(rollNumber);

			}//end of while loop


			//String returnData = this.readExamPreferences(workbook);
			//if (returnData!=null)
			//	return returnData;

		}
		catch (EncryptedDocumentException e) {
			// TODO Auto-generated catch block
			return "The input file is encrypted!";

		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			return "An Error Occurred while reading Student Course File. The file may be missing or permissions may be required to read it.!";

		}
		catch (Exception e)
		{
			System.out.println(e);
			return "An Error Occurred while reading the Student Course file.!";

		}

		this.populateExamDates();

		String message = this.readSlotTimings(workbook);
		if (message != null)
			return message;
		return this.readPreferences(workbook);
	}

	protected String readPreferences(Workbook workbook)
	{
		XSSFSheet slotTimingsSheet = (XSSFSheet)workbook.getSheet("preferences");

		if (slotTimingsSheet == null)
			return null; //preferences sheet does not exist

		Iterator<Row> rowIterator = slotTimingsSheet.rowIterator();

		Row row = null;
		try
		{
			while (rowIterator.hasNext()) {

				row = rowIterator.next();

				Iterator<Cell> cellIterator = row.cellIterator();
				Cell cell = cellIterator.next();
				String courseId = cell.getStringCellValue();
				if (this.coursesToBeScheduled.contains(courseId) == false)
					return "The course id: " + courseId + " in preferences list is invalid!";

				cell = cellIterator.next();
				int day = Integer.valueOf((int)cell.getNumericCellValue());
				cell = cellIterator.next();

				int slot = Integer.valueOf((int)cell.getNumericCellValue());

				if (day<0 || day>this.getTotalDays())
					return "Invalid day for " + courseId + " in preference list!";

				if (slot<0 || slot>this.getTotalSlotsPerDay())
					return "invalid slot for" + courseId + " in preference list!";

				this.preferences.put(courseId, new PreferenceDaySlot(courseId, day, slot));

			}
		}

		catch (Exception e)
		{
			System.out.println("ERROR");
			System.out.println(e);
			return "An error occurred in reading Preferences Sheet.";
		}

		return null;
	}



	protected boolean readHolidaysForFinalExams(Sheet settingsSheet, String settingName)
	{
		Iterator<Row> settingsIterator = settingsSheet.rowIterator();

		try
		{

			Row headerRow = settingsIterator.next();
			Row columnRow = settingsIterator.next();

			Iterator<Cell> headerIterator = headerRow.cellIterator();
			Iterator<Cell> columnIterator = columnRow.cellIterator();

			while (headerIterator.hasNext())
			{
				Cell headerValue = headerIterator.next();
				Cell value = columnIterator.next();

				if (headerValue.getStringCellValue().toLowerCase().compareTo(settingName.toLowerCase()) == 0)
				{
					DataFormatter dataFormatter = new DataFormatter();
					SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
					Date dateData;

					try {
						dateData = format.parse(dataFormatter.formatCellValue(value));
						holidays.put(format.format(dateData), format.format(dateData));

					}
					catch (ParseException e) {
						e.printStackTrace();
						return false;
					}

				}

			}

			return true; //read successful
		}

		catch (Exception E)
		{
			return false;
		}

	}

	private String readSettingValue(Sheet settingsSheet, String settingName)
	{
		Iterator<Row> settingIterator = settingsSheet.rowIterator();

		try
		{
			Row headerRow = settingIterator.next();
			Row columnRow = settingIterator.next();


			Iterator<Cell> headerIterator = headerRow.cellIterator();
			Iterator<Cell> columnIterator = columnRow.cellIterator();


			while (headerIterator.hasNext())
			{
				Cell headerValue = headerIterator.next();
				Cell value = columnIterator.next();

				if (headerValue.getStringCellValue().toLowerCase().compareTo(settingName.toLowerCase()) == 0)
				{

					DataFormatter dataFormatter = new DataFormatter();


					if (settingName.toLowerCase().equals("examstartdate") == false)
						return dataFormatter.formatCellValue(value);


					SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
					Date dateData;
					try {
						dateData = format.parse(dataFormatter.formatCellValue(value));
						return format.format(dateData);

					}
					catch (ParseException e) {
						e.printStackTrace();
						return null;
					}

				}
			}

		}
		catch (Exception E)
		{
			return null;
		}
		return null;
	}

	public HashMap<String, PreferenceDaySlot> getPreferences()
	{
		return this.preferences;
	}

	public String getSemesterName()
	{
		return this.semesterName;
	}
	public ArrayList<String> getExamDates()
	{
		return this.examDates;
	}
	public HashMap<String, Course> getAllCourses()
	{
		return this.allCourses;
	}

	public ArrayList<String> getCoursesToBeScheduled()
	{
		return this.coursesToBeScheduled;
	}

	public int getTotalDays()
	{
		return this.totalDays;
	}

	public int getTotalSlotsPerDay()
	{
		return this.totalSlotsPerDay;
	}

	public int getSeatingCapacityPerSlot()
	{
		return this.seatingCapacityPerSlot;
	}

	public HashMap<String, Student> getAllStudents() {
		return allStudents;
	}

	public String getExamStartDate()
	{
		return this.examStartDate;
	}

	public String getExamType()
	{
		return this.examType;
	}

}
