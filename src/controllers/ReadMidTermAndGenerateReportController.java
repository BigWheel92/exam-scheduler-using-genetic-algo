package controllers;

import java.io.File;

import datesheet_readers.MidDateSheetReader;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ReadMidTermAndGenerateReportController {

	public Label studentCourseFilePathLabel;
	public Label midTermDateSheetFilePathLabel;
	public Label outputDirectoryPathLabel;

	String studentCourseFilePath = null;
	String midTermDateSheetFilePath = null;
	String outputDirectoryPath = null;

	public void onStudentCourseFilePathButtonClick()
	{
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().addAll(new ExtensionFilter("Microsoft Excel Documents", "*.xlsx"));
		fc.setInitialDirectory(new File(System.getProperty("user.dir")));
		File selectedFile = fc.showOpenDialog(null);
		if (selectedFile != null)
		{
			this.studentCourseFilePathLabel.setText(selectedFile.getAbsolutePath());
			Tooltip tp = new Tooltip(selectedFile.getAbsolutePath());
			this.studentCourseFilePath = selectedFile.getAbsolutePath();
			this.studentCourseFilePathLabel.setTooltip(tp);

		}
	}

	public void onMidTermDateSheetFilePathButtonClick()
	{
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().addAll(new ExtensionFilter("Microsoft Excel Documents", "*.xlsx"));
		fc.setInitialDirectory(new File(System.getProperty("user.dir")));
		File selectedFile = fc.showOpenDialog(null);
		if (selectedFile != null)
		{
			this.midTermDateSheetFilePathLabel.setText(selectedFile.getAbsolutePath());
			Tooltip tp = new Tooltip(selectedFile.getAbsolutePath());
			this.midTermDateSheetFilePath = selectedFile.getAbsolutePath();
			this.midTermDateSheetFilePathLabel.setTooltip(tp);
		}
	}

	public void onOutputDirectoryPathButtonClick()
	{
		DirectoryChooser dc = new DirectoryChooser();
		dc.setInitialDirectory(new File(System.getProperty("user.dir")));
		File selectedDirectory = dc.showDialog(null);

		if (selectedDirectory != null)
		{
			this.outputDirectoryPathLabel.setText(selectedDirectory.getAbsolutePath());

			Tooltip tp = new Tooltip(selectedDirectory.getAbsolutePath());
			this.outputDirectoryPathLabel.setTooltip(tp);

			outputDirectoryPath = selectedDirectory.getAbsolutePath();


		}
	}

	public void onGenerateMidTermReportButtonClick()
	{
		if (this.outputDirectoryPath == null || this.studentCourseFilePath == null || this.midTermDateSheetFilePath == null)
		{
			AlertBox.display("Error", "Please enter all paths.", "ok");
			return;
		}
		
		AlertBox.displayProgress("Reading", "Please Wait the files are being read and the report is being created!");

		MidDateSheetReader midDateSheetReader = new MidDateSheetReader(this.studentCourseFilePath, this.midTermDateSheetFilePath, this.outputDirectoryPath);
		String message = midDateSheetReader.readStudentCourseFileAndMidTermDateSheetAndGenerateReport();

		AlertBox.stopProgress();
		
		if (message != null)
		{
			AlertBox.display("Error", message, "ok");
		}
		else AlertBox.display("Success", "Mid exam report successfully created!", "ok");
	}
}
