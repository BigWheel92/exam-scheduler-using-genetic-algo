import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Main extends Application {

	public static void main(String args[])
	{
		launch(args);
	}

	@Override
		public void start(Stage mainWindow) throws Exception {

		Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("views/MainWindow.fxml"));
		mainWindow.setTitle("FAST-NU Exam Scheduler Scheduler");
		mainWindow.setScene(new Scene(root));
		mainWindow.initStyle(StageStyle.DECORATED);
		mainWindow.setMaxHeight(850);
		mainWindow.setMaxWidth(800);
		mainWindow.show();

	}
}