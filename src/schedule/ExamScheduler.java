package schedule;
import java.util.ArrayList;
import java.util.HashMap;
import controllers.StatsMessageBoxController;
import exam_input_data_classes.*;
import ga_classes.*;
import javafx.application.Platform;


public class ExamScheduler {

	GeneticAlgorithm ga;
	private GThread thread;
	public class GThread extends Thread
	{

		@Override
			public void run() {
			System.out.println("Exam Scheduler Thread started.");
			runScheduler();
			System.out.println("Exam Scheduler Thread stopped.");
		}


		public void terminate()
		{
			terminate = true;
		}

	}

	private int itrCounter;
	private boolean terminate = false;


	ProblemData problemData;

	public String inputFilePath;
	public String outputDirectoryPath;
	public String examType;
	Chromosome bestSchedule;

	StatsMessageBoxController statsBoxController;
	public ExamScheduler(String examType)
	{
		ga = null;
		this.inputFilePath = null;
		this.outputDirectoryPath = null;
		this.thread = null;
		this.bestSchedule = null;
		this.terminate = false;
		this.statsBoxController = null;
		this.examType = examType;
	}

	public void createThread()
	{
		this.thread = new GThread();
	}

	public GThread getThread()
	{
		return this.thread;
	}

	public void setOutputDirectoryPath(String outputDirectoryPath)
	{
		this.outputDirectoryPath = outputDirectoryPath;
	}

	public void setInputFilePath(String inputFilePath)
	{
		this.inputFilePath = inputFilePath;
	}

	public String readInputFile()
	{
		this.problemData = new ProblemData(this.inputFilePath, examType);
		String status = problemData.readDataFromExcelFile();

		return status;
	}

	public void runScheduler()
	{

		ga = null;

		if (this.examType.equals("final"))
		{
			ga = new GeneticAlgorithmForFinalExams(this.problemData);
		}

		else if (this.examType.equals("mid"))
		{
			ga = new GeneticAlgorithmForMidExams(this.problemData);
		}

		ga.initPopulation(GA_HParameters.populationSize);
		int populationSize = GA_HParameters.populationSize;
		int childrenPerItr = GA_HParameters.childrenPerIteration;
		int tournamentSize = GA_HParameters.tournamentSize;
		double mutationRate = GA_HParameters.mutationRate;


		itrCounter = 0;
		this.terminate = false;

		this.statsBoxController.enableAllInputs();
		ArrayList<Chromosome> newChildren = new ArrayList<Chromosome>();

		do
		{
			GA_HParameters.mutex.lock();
			if (GA_HParameters.parameterValuesChanged == true)
			{
				childrenPerItr = GA_HParameters.childrenPerIteration;
				tournamentSize = GA_HParameters.tournamentSize;
				mutationRate = GA_HParameters.mutationRate;

				if (populationSize < GA_HParameters.populationSize)
				{
					ga.increasePopulationSize(GA_HParameters.populationSize - populationSize);
					populationSize = GA_HParameters.populationSize;
					System.out.println("New Population Size is :" + GA_HParameters.populationSize);
				}
				else if (populationSize > GA_HParameters.populationSize)
				{
					ga.decreasePopulationSize(populationSize - GA_HParameters.populationSize);
					populationSize = GA_HParameters.populationSize;
					System.out.println("New Population Size is :" + GA_HParameters.populationSize);
				}

				this.statsBoxController.enableAllInputs();
				Platform.runLater(() -> {
					this.statsBoxController.setMessageLabelText("Please wait while the " + this.examType + " exam and the report are being created. Press the stop button to stop.");
				});

				GA_HParameters.parameterValuesChanged = false;
			}

			GA_HParameters.mutex.unlock();

			newChildren.clear();

			for (int j = 0; j < childrenPerItr; j++)
			{
				Chromosome p1 = ga.tournament(tournamentSize);
				Chromosome p2 = null;
				do
				{
					p2 = ga.tournament(tournamentSize);

				} while (p2 == p1);

				newChildren.add(ga.doCrossOver(p1, p2));
				ga.doMutation(newChildren.get(j), mutationRate);
				ga.evaluateChromosome(newChildren.get(j));
			}

			ga.sortAccordingToFitness(newChildren);
			ga.insertNewChromsomesInPopulationIfTheyAreFitter(newChildren);

			itrCounter++;

			GA_HParameters.exportScheduleMutex.lock();
			this.bestSchedule = ga.best();
			GA_HParameters.exportScheduleMutex.unlock();
			Platform.runLater(() -> {
				this.statsBoxController.setStats("Iteration: " + itrCounter + "\nPopulation Fitness: " + ga.getTotalPopulationFitness() + "\nPopulationUnFitness: " + ga.getTotalPopulationUnfitness() + "\nPopulation Total: " + (ga.getTotalPopulationFitness() + ga.getTotalPopulationUnfitness()) + "\nBest Individual Fitness: " + this.bestSchedule.getFitnessValue() + "\nBest Individual Unfitness: " + this.bestSchedule.getUnfitnessValue() + "\nBest Individual Total: " + (this.bestSchedule.getFitnessValue() + this.bestSchedule.getUnfitnessValue()));
			});
			//System.out.println(" !"+ga.worst().getFitnessValue()+"   "+ga.worst().getUnfitnessValue());
		} while (terminate == false);

	}

	public String getExamType()
	{
		return this.examType;
	}

	public Chromosome getBestSchedule()
	{
		return this.bestSchedule;
	}

	public void setStatsMessageBoxController(StatsMessageBoxController controller)
	{
		this.statsBoxController = controller;
	}

	public ProblemData getProblemData()
	{
		return this.problemData;
	}


	public Day[] getDayWiseSchedule()
	{
		return this.bestSchedule.getDayWiseSchedule();
	}

	public HashMap<String, CourseSchedule> getCourseWiseSchedule()
	{
		return this.bestSchedule.getCourseWiseSchedule();
	}

}