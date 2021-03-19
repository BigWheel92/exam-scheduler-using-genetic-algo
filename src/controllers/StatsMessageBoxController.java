package controllers;
import ga_classes.GA_HParameters;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;



public class StatsMessageBoxController {
	public Label messageLabel;
	public String message;
	public Label statsLabel;

	public TextField populationSizeTextField;
	public TextField tournamentSizeTextField;
	public TextField childrenPerGenTextField;
	public TextField mutationRateTextField;


	int previousPopulationSize;
	int previousTournamentSize;
	int PreviousChildrenPerItr;
	double previousMutationRate;

	public Button stopButton;
	public Button updateParametersButton;

	public void setStats(String statMessage)
	{
		this.statsLabel.setText(statMessage);
	}


	public void onStopButtonClicked()
	{
		Stage stage = (Stage)this.stopButton.getScene().getWindow();
		stage.close();
	}

	public void setMessageLabelText(String message)
	{

		this.messageLabel.setText(message);

	}

	public void onUpdateParametersButtonClicked()
	{
		this.messageLabel.setText("Please Wait while the algorithm's parameters are being updated!");

		this.disableAllInputs();
		GA_HParameters.mutex.lock();
		GA_HParameters.parameterValuesChanged = true;

		try
		{
			GA_HParameters.populationSize = Integer.parseInt(this.populationSizeTextField.getText());
			if (GA_HParameters.populationSize < 2)
				throw new Exception();

			this.previousPopulationSize = GA_HParameters.populationSize;
		}
		catch (Exception e)
		{
			this.populationSizeTextField.setText(String.valueOf(this.previousPopulationSize));
			GA_HParameters.populationSize = this.previousPopulationSize;
		}

		try
		{
			GA_HParameters.tournamentSize = Integer.parseInt(this.tournamentSizeTextField.getText());
			if (GA_HParameters.tournamentSize<1 || GA_HParameters.tournamentSize>GA_HParameters.populationSize)
				throw new Exception();

			this.previousTournamentSize = GA_HParameters.tournamentSize;
		}
		catch (Exception e)
		{
			this.tournamentSizeTextField.setText(String.valueOf(this.previousTournamentSize));
			GA_HParameters.tournamentSize = this.previousTournamentSize;
		}

		try
		{
			GA_HParameters.childrenPerIteration = Integer.parseInt(this.childrenPerGenTextField.getText());
			if (GA_HParameters.childrenPerIteration<1 || GA_HParameters.childrenPerIteration>GA_HParameters.populationSize)
				throw new Exception();

			this.PreviousChildrenPerItr = GA_HParameters.childrenPerIteration;
		}
		catch (Exception e)
		{
			this.childrenPerGenTextField.setText(String.valueOf(this.PreviousChildrenPerItr));
			GA_HParameters.childrenPerIteration = this.PreviousChildrenPerItr;
		}


		try
		{
			GA_HParameters.mutationRate = Double.parseDouble(this.mutationRateTextField.getText());
			if (GA_HParameters.mutationRate < 0 || GA_HParameters.mutationRate>1)
				throw new Exception();

			this.previousMutationRate = GA_HParameters.mutationRate;
		}
		catch (Exception e)
		{
			this.mutationRateTextField.setText(String.valueOf(this.previousMutationRate));
			GA_HParameters.mutationRate = this.previousMutationRate;
		}

		GA_HParameters.mutex.unlock();

	}

	@FXML
		public void initialize()
	{
		this.previousMutationRate = GA_HParameters.mutationRate;
		this.previousPopulationSize = GA_HParameters.populationSize;
		this.previousTournamentSize = GA_HParameters.tournamentSize;
		this.PreviousChildrenPerItr = GA_HParameters.childrenPerIteration;

		this.tournamentSizeTextField.setText(String.valueOf(this.previousTournamentSize));
		this.populationSizeTextField.setText(String.valueOf(this.previousPopulationSize));
		this.mutationRateTextField.setText(String.valueOf(this.previousMutationRate));
		this.childrenPerGenTextField.setText(String.valueOf(this.PreviousChildrenPerItr));
		this.disableAllInputs();

		//window.initModality(Modality.APPLICATION_MODAL);
		//window.initStyle(StageStyle.UTILITY);
	}

	public void disableAllInputs()
	{

		this.updateParametersButton.setDisable(true);

		this.stopButton.setDisable(true);

		this.childrenPerGenTextField.setDisable(true);
		this.tournamentSizeTextField.setDisable(true);
		this.populationSizeTextField.setDisable(true);
		this.mutationRateTextField.setDisable(true);

	}
	public void init(String message, String initialStat)
	{
		this.message = message;
		this.messageLabel.setText(message);
		this.statsLabel.setText(initialStat);
	}

	public void enableAllInputs()
	{
		this.updateParametersButton.setDisable(false);
		this.stopButton.setDisable(false);

		this.childrenPerGenTextField.setDisable(false);
		this.tournamentSizeTextField.setDisable(false);
		this.mutationRateTextField.setDisable(false);
		this.populationSizeTextField.setDisable(false);

	}
}