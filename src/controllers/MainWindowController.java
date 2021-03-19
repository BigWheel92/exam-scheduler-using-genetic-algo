package controllers;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainWindowController {

	Button midTermExamScheduleButton;
	Button midTermReportGenerateButton;

	Button finalExamScheduleButton;
	Button finalExamReportGenerateButton;

	public void handleMidTermExamsSheduleButtonClick()
	{
		Stage window = new Stage();
		Parent root;
		try {
			FXMLLoader load = new FXMLLoader(getClass().getClassLoader().getResource("views/FileChoose.fxml"));
			root = load.load();
			window.setTitle("FAST-NU Date Sheet Scheduler");
			window.setScene(new Scene(root));
			window.initStyle(StageStyle.DECORATED);
			window.initModality(Modality.APPLICATION_MODAL);
			FileChooseController controller = load.getController();
			controller.setExamType("mid");
			//window.setMaxHeight(800);
			//window.setMaxWidth(800);
			window.showAndWait();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void handleFinalExamsSheduleButtonClick()
	{
		Stage window = new Stage();
		Parent root;
		try {
			FXMLLoader load = new FXMLLoader(getClass().getClassLoader().getResource("views/FileChoose.fxml"));
			root = load.load();
			FileChooseController controller = load.getController();
			controller.setExamType("final");
			window.setTitle("FAST-NU Date Sheet Scheduler");
			window.setScene(new Scene(root));
			window.initStyle(StageStyle.DECORATED);
			window.initModality(Modality.APPLICATION_MODAL);

			//window.setMaxHeight(800);
			//window.setMaxWidth(800);
			window.showAndWait();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void handleGenerateMidComparisonReport()
	{
		Stage window = new Stage();
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getClassLoader().getResource("views/MidTermComparisonReport.fxml"));
			window.setTitle("FAST-NU Date Sheet Scheduler");
			window.setScene(new Scene(root));
			window.initStyle(StageStyle.DECORATED);
			window.initModality(Modality.APPLICATION_MODAL);

			window.setMaxHeight(800);
			window.setMaxWidth(800);
			window.showAndWait();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void handleReadFinalExamScheduleAndGenerateReportButton()
	{
		Stage window = new Stage();
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getClassLoader().getResource("views/ReadFinalExamAndGenerateReportView.fxml"));
			window.setTitle("FAST-NU Date Sheet Scheduler");
			window.setScene(new Scene(root));
			window.initStyle(StageStyle.DECORATED);
			window.initModality(Modality.APPLICATION_MODAL);

			window.setMaxHeight(800);
			window.setMaxWidth(800);
			window.showAndWait();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void handleReadMidTermScheduleAndGenerateReportButton()
	{
		Stage window = new Stage();
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getClassLoader().getResource("views/ReadMidTermAndGenerateReportView.fxml"));
			window.setTitle("FAST-NU Date Sheet Scheduler");
			window.setScene(new Scene(root));
			window.initStyle(StageStyle.DECORATED);
			window.initModality(Modality.APPLICATION_MODAL);

			window.setMaxHeight(800);
			window.setMaxWidth(800);
			window.showAndWait();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
