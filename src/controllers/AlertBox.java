package controllers;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AlertBox {
	static Stage window = null;

	public static void displayProgress(String title, String message)
	{
		window = new Stage();

		Label label = new Label();
		label.setText(message);
		label.setWrapText(true);
		label.setTextAlignment(TextAlignment.CENTER);

		VBox layout = new VBox(10);
		layout.setAlignment(Pos.CENTER);
		layout.getChildren().addAll(label);
		window.setMaxHeight(300);
		window.setMaxWidth(500);
		window.setMinHeight(300);
		window.setMinWidth(500);

		Scene scene = new Scene(layout, 300, 100);

		window.setTitle(title);
		window.setScene(scene);
		window.initModality(Modality.APPLICATION_MODAL);
		window.initStyle(StageStyle.UTILITY);
		window.show();



	}

	public static void stopProgress()
	{
		if (window != null)
		{
			window.close();
			window = null;
		}

	}

	public static void display(String title, String message, String buttonLabel)
	{
		window = new Stage();
		Button closeButton = new Button();
		closeButton.setMinWidth(140);
		closeButton.setText(buttonLabel);
		closeButton.setOnAction(new EventHandler<ActionEvent>()
		{

			@Override
				public void handle(ActionEvent arg0) {
				window.close();

			}

		});

		Label label = new Label();
		label.setText(message);
		label.setWrapText(true);
		label.setTextAlignment(TextAlignment.CENTER);

		VBox layout = new VBox(10);
		layout.setAlignment(Pos.CENTER);
		layout.getChildren().addAll(label, closeButton);
		window.setMaxHeight(300);
		window.setMaxWidth(500);
		window.setMinHeight(300);
		window.setMinWidth(500);

		Scene scene = new Scene(layout, 300, 150);

		window.setTitle(title);
		window.setScene(scene);
		window.initModality(Modality.APPLICATION_MODAL);
		window.initStyle(StageStyle.UTILITY);
		window.showAndWait();

		window = null;
	}

}