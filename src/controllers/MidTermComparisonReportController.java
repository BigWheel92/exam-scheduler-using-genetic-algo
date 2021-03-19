package controllers;

import java.io.File;

import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import schedule.MidDateSheetComparisonReport;

public class MidTermComparisonReportController {

	public Label oldMidTermReportLabel;
	public Label newMidTermReportLabel;
	public Label outputLabel;
	String oldReportPath = null;
	String newReportPath = null;
	String outputPath = null;
	
	public void onOldMidTermReportButtonClick()
	{
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().addAll(new ExtensionFilter("Microsoft Excel Documents", "*.xlsx"));
		fc.setInitialDirectory(new File(System.getProperty("user.dir")));
		File selectedFile = fc.showOpenDialog(null);

		if (selectedFile != null)
		{
			this.oldMidTermReportLabel.setText(selectedFile.getAbsolutePath());

			Tooltip tp = new Tooltip(selectedFile.getAbsolutePath());
			this.oldMidTermReportLabel.setTooltip(tp);

			oldReportPath = selectedFile.getAbsolutePath();


		}

	}

	public void onNewMidTermReportButtonClick()
	{
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().addAll(new ExtensionFilter("Microsoft Excel Documents", "*.xlsx"));
		fc.setInitialDirectory(new File(System.getProperty("user.dir")));
		File selectedFile = fc.showOpenDialog(null);

		if (selectedFile != null)
		{
			this.newMidTermReportLabel.setText(selectedFile.getAbsolutePath());

			Tooltip tp = new Tooltip(selectedFile.getAbsolutePath());
			this.newMidTermReportLabel.setTooltip(tp);

			newReportPath = selectedFile.getAbsolutePath();


		}


	}

	public void onOutputPathButtonClick()
	{
		DirectoryChooser dc = new DirectoryChooser();
		dc.setInitialDirectory(new File(System.getProperty("user.dir")));
		File selectedDirectory = dc.showDialog(null);

		if (selectedDirectory != null)
		{
			this.outputLabel.setText(selectedDirectory.getAbsolutePath());

			Tooltip tp = new Tooltip(selectedDirectory.getAbsolutePath());
			this.outputLabel.setTooltip(tp);

			outputPath = selectedDirectory.getAbsolutePath();


		}
	}
	
	public void onGenerateButtonClick()
	{
		if (this.outputPath == null || this.oldReportPath == null || this.newReportPath == null)
		{
			AlertBox.display("Error", "Please enter all paths.", "ok");
			return;
		}

		MidDateSheetComparisonReport cr = new MidDateSheetComparisonReport(this.oldReportPath, this.newReportPath, this.outputPath);
		String message = cr.readMidReportsAndGenerateComparisonReport();
		if (!message.equals(""))
		{
			AlertBox.display("Error", message, "ok");
			return;
		}
	}
}
