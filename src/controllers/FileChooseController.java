package controllers;

import java.io.File;
import java.io.IOException;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;
import exam_input_data_classes.*;
import excel_reports.ExamExporter;
import excel_reports.ExamReport;
import excel_reports.FinalExamExporter;
import excel_reports.FinalExamReport;
import excel_reports.MidExamExporter;
import excel_reports.MidExamReport;
import schedule.ExamScheduler;

public class FileChooseController {

	public Label inputFilePathLabel;
	public Label outputDirectoryPathLabel;
	public Button inputFilePathButton;
	public Button outputDirectoryPathButton;
	Button generateButton;
	String inputFilePath = null;
	String outputDirectoryPath = null;

	public TableView coursesSchdeduledTable;
	public TableColumn courseCodeColumnForScheduledTable;
	public TableColumn courseNameColumnForScheduledTable;

	public TableView coursesNotScheduledTable;
	public TableColumn courseCodeColumnForUnscheduledTable;
	public TableColumn courseNameColumnForUnscheduledTable;
	public Button scheduleButton;
	public Button notToscheduleButton;
	private String examType;
	ExamScheduler examScheduler;

	public ExamScheduler getExamScheduler()
	{
		return this.examScheduler;
	}

	public void toScheduleButtonHandler()// user will select courses from not to schedule courses table to add them to schedule courses table.
	{
		this.coursesSchdeduledTable.getSelectionModel().clearSelection();

		ObservableList<Course> selectedCourses = this.coursesNotScheduledTable.getSelectionModel().getSelectedItems();
		ObservableList<Course> tableCourses = this.coursesNotScheduledTable.getItems();

		this.coursesSchdeduledTable.getItems().addAll(selectedCourses);

		for (int i = 0; i < selectedCourses.size(); i++) {
			selectedCourses.get(i).setToBeScheduled(true);
			this.examScheduler.getProblemData().getCoursesToBeScheduled().add(selectedCourses.get(i).getCourseId());
		}

		tableCourses.removeAll(selectedCourses);
		this.coursesNotScheduledTable.getSelectionModel().clearSelection();

	}

	public void notToScheduleButtonHandler() {
		this.coursesNotScheduledTable.getSelectionModel().clearSelection();
		ObservableList<Course> selectedCourses = this.coursesSchdeduledTable.getSelectionModel().getSelectedItems();
		ObservableList<Course> tableCourses = this.coursesSchdeduledTable.getItems();

		this.coursesNotScheduledTable.getItems().addAll(selectedCourses);

		for (int i = 0; i < selectedCourses.size(); i++) {
			selectedCourses.get(i).setToBeScheduled(false);
			this.examScheduler.getProblemData().getCoursesToBeScheduled().remove(selectedCourses.get(i).getCourseId());
		}
		tableCourses.removeAll(selectedCourses);
		this.coursesSchdeduledTable.getSelectionModel().clearSelection();
	}

	public void onInputFilePathButtonClick() {

		examScheduler = null;
		examScheduler = new ExamScheduler(this.examType);

		this.coursesSchdeduledTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.coursesNotScheduledTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().addAll(new ExtensionFilter("Microsoft Excel Documents", "*.xlsx"));
		fc.setInitialDirectory(new File(System.getProperty("user.dir")));
		File selectedFile = fc.showOpenDialog(null);

		if (selectedFile != null) {
			AlertBox.displayProgress("Reading", "Please Wait the input file is being read!");

			this.coursesSchdeduledTable.getItems().clear();
			this.coursesNotScheduledTable.getItems().clear();

			// reading input file
			examScheduler.setInputFilePath(selectedFile.getAbsolutePath());

			String message = examScheduler.readInputFile();

			if (message != null) {
				AlertBox.stopProgress();
				AlertBox.display("Error", message, "ok");
				return;
			}
			this.inputFilePathLabel.setText(selectedFile.getAbsolutePath());
			Tooltip tp = new Tooltip(selectedFile.getAbsolutePath());
			inputFilePath = selectedFile.getAbsolutePath();
			this.inputFilePathLabel.setTooltip(tp);

			ObservableList<Course> toBeScheduledCourses = FXCollections.observableArrayList();
			ObservableList<Course> notToBeScheduledCourses = FXCollections.observableArrayList();
			for (Course c : examScheduler.getProblemData().getAllCourses().values()) {
				if (c.getToBeScheduled() == true)
				{
					toBeScheduledCourses.add(c);

				}

				else {
					notToBeScheduledCourses.add(c);
				}
			}

			this.courseCodeColumnForScheduledTable.setCellValueFactory(new PropertyValueFactory("courseId"));
			this.courseNameColumnForScheduledTable.setCellValueFactory(new PropertyValueFactory("courseName"));
			this.courseCodeColumnForScheduledTable.setCellFactory(WRAPPING_CELL_FACTORY);
			this.courseNameColumnForScheduledTable.setCellFactory(WRAPPING_CELL_FACTORY);

			this.courseCodeColumnForUnscheduledTable.setCellValueFactory(new PropertyValueFactory("courseId"));
			this.courseNameColumnForUnscheduledTable.setCellValueFactory(new PropertyValueFactory("courseName"));
			this.courseCodeColumnForUnscheduledTable.setCellFactory(WRAPPING_CELL_FACTORY);
			this.courseNameColumnForUnscheduledTable.setCellFactory(WRAPPING_CELL_FACTORY);

			this.coursesSchdeduledTable.getItems().addAll(toBeScheduledCourses);

			this.coursesNotScheduledTable.getItems().addAll(notToBeScheduledCourses);

			AlertBox.stopProgress();

		}

	}

	public void setExamType(String examType)
	{
		this.examType = examType;
	}

	public String getExamType(String examType)
	{
		return this.examType;
	}

	public void onOutputFilePathButtonClick() {
		DirectoryChooser dc = new DirectoryChooser();
		dc.setInitialDirectory(new File(System.getProperty("user.dir")));
		File selectedDirectory = dc.showDialog(null);

		if (selectedDirectory != null) {
			this.outputDirectoryPathLabel.setText(selectedDirectory.getAbsolutePath());

			Tooltip tp = new Tooltip(selectedDirectory.getAbsolutePath());
			this.outputDirectoryPathLabel.setTooltip(tp);

			outputDirectoryPath = selectedDirectory.getAbsolutePath();
			examScheduler.setOutputDirectoryPath(outputDirectoryPath);

		}

	}

	public void onGenerateButtonClick() {
		if (this.inputFilePath == null || this.outputDirectoryPath == null) {
			AlertBox.display("Error", "Please give all input/output paths first!", "ok");
			return;
		}

		/*
		 * if (message.equals("") == false) { AlertBox.stopProgress();
		 * AlertBox.display("Error", message); return; }
		 */

		StatsMessageBoxController messageBoxController = null;
		Stage window = new Stage();
		Parent root;
		try {

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/StatsMessageBox.fxml"));
			root = loader.load();
			window.setTitle("Creating " + (this.examType.equals("final") ? "Final" : "Mid") + " Datesheet");
			window.setScene(new Scene(root));
			window.initStyle(StageStyle.DECORATED);
			window.initModality(Modality.APPLICATION_MODAL);
			window.setMaxHeight(600);
			window.setMaxWidth(800);
			messageBoxController = loader.getController();
			messageBoxController.init("Please wait while the " + this.examType + " exam datesheet and the report are being created. Press the stop button to stop.",
				"Initializing...");
			//window.setMaxHeight(800);
			//window.setMaxWidth(800);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		examScheduler.setStatsMessageBoxController(messageBoxController);
		examScheduler.createThread();
		examScheduler.getThread().start();
		window.showAndWait();

		examScheduler.getThread().terminate();
		try {
			examScheduler.getThread().join();
		}
		catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		AlertBox.displayProgress("", "Please wait while the " + this.examType + " exam schedule and the report are being exported.");
		ExamExporter exporter = null;
		ExamReport report = null;

		if (this.examType.equals("final"))
		{
			exporter = new FinalExamExporter(this.examScheduler.getProblemData(), this.examScheduler.getBestSchedule(), this.outputDirectoryPath + "\\final date sheet.xlsx");
			report = new FinalExamReport(examScheduler.getDayWiseSchedule(), examScheduler.getCourseWiseSchedule(), examScheduler.getProblemData(), this.outputDirectoryPath + "\\final exam report.xlsx");

		}
		else
		{
			exporter = new MidExamExporter(this.examScheduler.getProblemData(), this.examScheduler.getBestSchedule(), this.outputDirectoryPath + "\\mid date sheet.xlsx");
			report = new MidExamReport(examScheduler.getDayWiseSchedule(), examScheduler.getCourseWiseSchedule(), examScheduler.getProblemData(), this.outputDirectoryPath + "\\mid exam report.xlsx");

		}

		String message = exporter.exportDateSheetToExcel();


		if (message != null) {
			AlertBox.stopProgress();
			AlertBox.display("Error", message, "ok");
			return;

		}


		message = report.exportToExcel();

		if (message != null)
		{
			AlertBox.stopProgress();
			AlertBox.display("Error", message, "ok");
		}

		else {
			AlertBox.stopProgress();
			AlertBox.display("Success", "Exam Schedule and Report exported Succesfully!", "ok");
		}



	}

	// to make text inside the tables wrappable.
	public static final Callback<TableColumn<Course, String>, TableCell<Course, String>> WRAPPING_CELL_FACTORY = new Callback<TableColumn<Course, String>, TableCell<Course, String>>() {

		@Override
			public TableCell<Course, String> call(TableColumn<Course, String> param) {
			TableCell<Course, String> tableCell = new TableCell<Course, String>() {
				@Override
					protected void updateItem(String item, boolean empty) {
					if (item == getItem())
						return;

					super.updateItem(item, empty);

					if (item == null) {
						super.setText(null);
						super.setGraphic(null);
					}
					else {
						super.setText(null);
						Label l = new Label(item);
						l.setWrapText(true);
						VBox box = new VBox(l);
						l.heightProperty().addListener((observable, oldValue, newValue) -> {
							box.setPrefHeight(newValue.doubleValue() + 7);
							Platform.runLater(() -> this.getTableRow().requestLayout());
						});
						super.setGraphic(box);
					}
				}
			};
			return tableCell;
		}
	};
}
