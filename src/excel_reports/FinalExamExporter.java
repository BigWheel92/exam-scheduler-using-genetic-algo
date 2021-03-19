package excel_reports;
import java.io.FileOutputStream;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import exam_input_data_classes.DateHelper;
import exam_input_data_classes.ProblemData;
import ga_classes.Chromosome;

public class FinalExamExporter extends ExamExporter {

	public FinalExamExporter(ProblemData problemData, Chromosome bestSchedule, String outputFileName)
	{
		super(problemData, bestSchedule, outputFileName);
	}

	public String exportDateSheetToExcel()
	{
		try {

			Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file
			FileOutputStream fileOut = new FileOutputStream(this.outputDirectoryPath);

			Sheet CSsheet = workbook.createSheet("CS");
			Sheet MGsheet = workbook.createSheet("MG");
			Sheet CVsheet = workbook.createSheet("CV");
			Sheet EEsheet = workbook.createSheet("EE");
			Sheet completeSheet = workbook.createSheet("Complete");


			setDatesheetNameHeader(CSsheet, "DATE SHEET FOR FINAL EXAMINATION (CS DEPT)");
			setDatesheetNameHeader(MGsheet, "DATE SHEET FOR FINAL EXAMINATION (MG DEPT)");
			setDatesheetNameHeader(EEsheet, "DATE SHEET FOR FINAL EXAMINATION (EE DEPT)");
			setDatesheetNameHeader(CVsheet, "DATE SHEET FOR FINAL EXAMINATION (CV DEPT)");


			setDatesheetNameHeader(completeSheet, "COMPLETE DATE SHEET");

			setDatesheetSemester(CSsheet, this.problemData.getSemesterName() + " Semester");
			setDatesheetSemester(MGsheet, this.problemData.getSemesterName() + " Semester");
			setDatesheetSemester(EEsheet, this.problemData.getSemesterName() + " Semester");
			setDatesheetSemester(CVsheet, this.problemData.getSemesterName() + " Semester");

			setDatesheetSemester(completeSheet, this.problemData.getSemesterName() + " Semester");

			this.setFinalDateSheetDayDateHeader(CSsheet);
			this.setFinalDateSheetDayDateHeader(MGsheet);
			this.setFinalDateSheetDayDateHeader(EEsheet);
			this.setFinalDateSheetDayDateHeader(CVsheet);
			this.setFinalDateSheetDayDateHeader(completeSheet);

			Font font = workbook.createFont();
			font.setBold(true);
			font.setFontName("Calibri");
			font.setFontHeightInPoints((short)10);
			font.setColor(IndexedColors.BLACK.getIndex());

			for (int i = 0; i < this.problemData.getTotalSlotsPerDay(); i++)
			{

				// Create a CellStyle with the font
				CellStyle headerCellStyle = workbook.createCellStyle();
				headerCellStyle.setFont(font);

				headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
				headerCellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);

				// headerCellStyle.setFillForegroundColor((IndexedColors.LIGHT_BLUE.getIndex()));
			   //   headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				  //create a row
				  //finding max number of exams perslot

			}

			this.printDateSheet(completeSheet, "complete");
			this.checkDepartmentCourseOffer();
			this.printDateSheet(CSsheet, "CS");
			this.printDateSheet(CVsheet, "CV");
			this.printDateSheet(EEsheet, "EE");
			this.printDateSheet(MGsheet, "MG");

			workbook.write(fileOut);
			workbook.close();
			fileOut.close();

			// Closing the workbook

		}
		catch (Exception e) {
			return "An error occurred while exporting the date sheet to excel file.";

		}

		return null;
	}


	public void setDatesheetNameHeader(Sheet sheet, String text)
	{
		// Create a Font for styling header cells
		Workbook workbook = sheet.getWorkbook();

		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setItalic(true);
		headerFont.setFontName("Arial");
		headerFont.setFontHeightInPoints((short)14);
		headerFont.setColor(IndexedColors.RED.getIndex());

		// Create a CellStyle with the font
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

		// headerCellStyle.setFillForegroundColor((IndexedColors.LIGHT_BLUE.getIndex()));
	   //   headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		  //create a row
		Row headerRow = sheet.createRow(0);

		// Create cells

		for (int i = 0; i < 8; i++) {
			Cell cell = headerRow.createCell(i);
			if (i == 0)
				cell.setCellValue(text);
			cell.setCellStyle(headerCellStyle);

		}


		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
	}

	public void printDateSheet(Sheet completeSheet, String dept)
	{
		int currentRowNumber = 3;

		Workbook workbook = completeSheet.getWorkbook();
		Font font = workbook.createFont();
		font.setBold(true);
		font.setFontName("Calibri");
		font.setFontHeightInPoints((short)10);
		font.setColor(IndexedColors.BLACK.getIndex());


		//the following loop creates rows without writing data about exam schedule.
		for (int i = 0; i < this.problemData.getTotalDays(); i++)
		{
			// Create a CellStyle with the font
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(font);

			headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
			headerCellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);

			headerCellStyle.setBorderBottom(BorderStyle.THIN);
			headerCellStyle.setBorderLeft(BorderStyle.THIN);
			headerCellStyle.setBorderTop(BorderStyle.THIN);
			headerCellStyle.setBorderRight(BorderStyle.THIN);
			headerCellStyle.setWrapText(true);

			int maximumExamsScheduledInAnySlot = 0;

			if (dept.equals("CV"))
			{
				maximumExamsScheduledInAnySlot = this.findMaximumNumberOfCVExamsScheduledInAnySlot(i);

			}
			else if (dept.equals("CS"))
			{
				maximumExamsScheduledInAnySlot = this.findMaximumNumberOfCSExamsScheduledInAnySlot(i);
			}
			else if (dept.equals("EE"))
			{
				maximumExamsScheduledInAnySlot = this.findMaximumNumberOfEEExamsScheduledInAnySlot(i);
			}
			else if (dept.equals("MG"))
			{
				maximumExamsScheduledInAnySlot = this.findMaximumNumberOfMGExamsScheduledInAnySlot(i);
			}
			else if (dept.equals("complete"))
			{
				maximumExamsScheduledInAnySlot = this.findMaximumNumberOfExamsScheduledInAnySlot(i);
			}

			if (maximumExamsScheduledInAnySlot == 0)
				maximumExamsScheduledInAnySlot = 1;

			//creating rows for each day
			for (int j = currentRowNumber; j < currentRowNumber + maximumExamsScheduledInAnySlot; j++)
			{
				Row row = completeSheet.createRow(j);
				row.setHeightInPoints(17);
				Cell cell = row.createCell(0);
				cell.setCellStyle(headerCellStyle);
				cell.setCellValue(DateHelper.getDayName(this.problemData.getExamDates().get(i)));

				cell = row.createCell(1);
				cell.setCellStyle(headerCellStyle);
				cell.setCellValue(this.problemData.getExamDates().get(i));


				int l = 2;

				//creating cells for each row
				for (int k = 0; k < this.problemData.getTotalSlotsPerDay(); k++)
				{
					cell = row.createCell(k + l);
					cell.setCellStyle(headerCellStyle);
					//cell.setCellValue("qwe");
					cell = row.createCell(k + l + 1);
					cell.setCellStyle(headerCellStyle);
					// cell.setCellValue("123");   
					l++;

				}

			}//end of row creation loop

			currentRowNumber += maximumExamsScheduledInAnySlot;
			Row row = completeSheet.createRow(currentRowNumber);
			CellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
			cellStyle.setFillPattern(FillPatternType.THICK_VERT_BANDS);
			for (int z = 0; z < this.problemData.getTotalSlotsPerDay() * 2 + 2; z++)
			{

				row.createCell(z);
				row.getCell(z).setCellStyle(cellStyle);

			}

			currentRowNumber++;

			// headerCellStyle.setFillForegroundColor((IndexedColors.LIGHT_BLUE.getIndex()));
		   //   headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			  //create a row
			  //finding max number of exams perslot

		}

		//writing exam data to created rows
		currentRowNumber = 3;
		for (int i = 0; i < this.problemData.getTotalDays(); i++)
		{
			int maximumExamsScheduledInAnySlot = 0;

			if (dept.equals("CV"))
			{
				maximumExamsScheduledInAnySlot = this.findMaximumNumberOfCVExamsScheduledInAnySlot(i);

			}
			else if (dept.equals("CS"))
			{
				maximumExamsScheduledInAnySlot = this.findMaximumNumberOfCSExamsScheduledInAnySlot(i);
			}
			else if (dept.equals("EE"))
			{
				maximumExamsScheduledInAnySlot = this.findMaximumNumberOfEEExamsScheduledInAnySlot(i);
			}
			else if (dept.equals("MG"))
			{
				maximumExamsScheduledInAnySlot = this.findMaximumNumberOfMGExamsScheduledInAnySlot(i);
			}
			else if (dept.equals("complete"))
			{
				maximumExamsScheduledInAnySlot = this.findMaximumNumberOfExamsScheduledInAnySlot(i);
			}
			if (maximumExamsScheduledInAnySlot == 0)
				maximumExamsScheduledInAnySlot = 1;

			int l = 2;
			for (int j = 0; j < this.problemData.getTotalSlotsPerDay(); j++)
			{


				int k = 0;

				if (dept.equals("complete"))
				{
					for (String courseCode : this.bestSchedule.getDayWiseSchedule()[i].getSlots()[j].getCoursesScheduled())
					{
						completeSheet.getRow(currentRowNumber + k).getCell(j + l).setCellValue(courseCode);

						completeSheet.getRow(currentRowNumber + k).getCell(j + l + 1).setCellValue(this.problemData.getAllCourses().get(courseCode).getCourseName());

						k++;
					}

				}

				else if (dept.equals("EE"))
				{
					for (String courseCode : this.dateSheet[i].slots[j].eeExamsScheduled)
					{
						completeSheet.getRow(currentRowNumber + k).getCell(j + l).setCellValue(courseCode);

						completeSheet.getRow(currentRowNumber + k).getCell(j + l + 1).setCellValue(this.problemData.getAllCourses().get(courseCode).getCourseName());

						k++;
					}
				}

				else if (dept.equals("CV"))
				{
					for (String courseCode : this.dateSheet[i].slots[j].cvExamsScheduled)
					{
						completeSheet.getRow(currentRowNumber + k).getCell(j + l).setCellValue(courseCode);

						completeSheet.getRow(currentRowNumber + k).getCell(j + l + 1).setCellValue(this.problemData.getAllCourses().get(courseCode).getCourseName());

						k++;
					}
				}

				else if (dept.equals("CS"))
				{
					for (String courseCode : this.dateSheet[i].slots[j].csExamsScheduled)
					{
						completeSheet.getRow(currentRowNumber + k).getCell(j + l).setCellValue(courseCode);

						completeSheet.getRow(currentRowNumber + k).getCell(j + l + 1).setCellValue(this.problemData.getAllCourses().get(courseCode).getCourseName());

						k++;
					}
				}

				else if (dept.equals("MG"))
				{
					for (String courseCode : this.dateSheet[i].slots[j].mgExamsScheduled)
					{
						completeSheet.getRow(currentRowNumber + k).getCell(j + l).setCellValue(courseCode);

						completeSheet.getRow(currentRowNumber + k).getCell(j + l + 1).setCellValue(this.problemData.getAllCourses().get(courseCode).getCourseName());

						k++;
					}
				}

				l++;

			}
			currentRowNumber += (maximumExamsScheduledInAnySlot + 1);

		}



		for (int i = 0; i < this.problemData.getTotalSlotsPerDay() * 2 + 2; i++)
		{
			completeSheet.autoSizeColumn(i);

		}

	}

	public void setDatesheetSemester(Sheet sheet, String text)
	{

		Workbook workbook = sheet.getWorkbook();

		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setItalic(true);
		headerFont.setFontName("Arial");
		headerFont.setFontHeightInPoints((short)14);
		headerFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());

		// Create a CellStyle with the font
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

		// headerCellStyle.setFillForegroundColor((IndexedColors.LIGHT_BLUE.getIndex()));
	   //   headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		  //create a row
		Row headerRow = sheet.createRow(1);

		// Create cells

		for (int i = 0; i < 8; i++) {
			Cell cell = headerRow.createCell(i);
			if (i == 0)
				cell.setCellValue(text);
			cell.setCellStyle(headerCellStyle);

		}


		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 8));
	}

	public void setFinalDateSheetDayDateHeader(Sheet sheet)
	{

		// Create a Font for styling header cells
		Workbook workbook = sheet.getWorkbook();

		Font headerFont = workbook.createFont();
		headerFont.setBold(true);

		headerFont.setFontName("Arial");
		headerFont.setFontHeightInPoints((short)11);
		headerFont.setColor(IndexedColors.BLACK.getIndex());

		// Create a CellStyle with the font
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
		headerCellStyle.setBorderBottom(BorderStyle.THIN);
		headerCellStyle.setBorderLeft(BorderStyle.THIN);
		headerCellStyle.setBorderTop(BorderStyle.THIN);
		headerCellStyle.setBorderRight(BorderStyle.THIN);
		Row headerRow = sheet.createRow(2);

		headerRow.setHeightInPoints((float)18);
		//headerRow.setHeight((short) 32.3);

		Cell cell = headerRow.createCell(0);
		cell.setCellValue("Day");
		cell.setCellStyle(headerCellStyle);
		sheet.autoSizeColumn(0);

		cell = headerRow.createCell(1);
		cell.setCellValue("Date");
		cell.setCellStyle(headerCellStyle);
		sheet.autoSizeColumn(1);

		int j = 2;
		for (int i = 0; i < this.problemData.getTotalSlotsPerDay(); i++)
		{


			cell = headerRow.createCell(i + j);
			cell.setCellValue("Code");
			cell.setCellStyle(headerCellStyle);
			sheet.autoSizeColumn(i + j);

			cell = headerRow.createCell(i + j + 1);
			cell.setCellValue(this.problemData.getSlotTimings().get(i));
			cell.setCellStyle(headerCellStyle);
			sheet.autoSizeColumn(i + j + 1);
			j++;


		}

	}
}
