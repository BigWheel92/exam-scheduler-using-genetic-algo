package ga_classes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import exam_input_data_classes.*;


public abstract class GeneticAlgorithm {

	protected ArrayList<Chromosome> population;
	protected ProblemData problemData;
	protected Random random;
	protected HashSet<String> hashOfChromosomesInPopulation;
	protected int totalPopulationFitness;
	protected int totalPopulationUnfitness;

	public abstract void evaluateChromosome(Chromosome c);

	public GeneticAlgorithm(ProblemData problemData)
	{
		this.problemData = problemData;
		this.random = new Random(System.currentTimeMillis());
		this.hashOfChromosomesInPopulation = new HashSet<String>();
		this.totalPopulationFitness = 0;
		this.totalPopulationUnfitness = 0;
	}


	public int getTotalPopulationFitness() {
		return totalPopulationFitness;
	}

	public int getTotalPopulationUnfitness() {
		return totalPopulationUnfitness;
	}

	public void increasePopulationSize(int incrementCount)
	{
		for (int i = 0; i < incrementCount; i++)
		{
			Chromosome c = null;
			String hash = null;

			do
			{
				c = new Chromosome(random, problemData); //calling the constructor that randomly creates a schedule
				hash = c.toString(this.problemData.getCoursesToBeScheduled());

			} while (this.hashOfChromosomesInPopulation.contains(hash));

			this.evaluateChromosome(c);
			this.totalPopulationFitness += c.getFitnessValue();
			this.totalPopulationUnfitness += c.getUnfitnessValue();
			this.population.add(c);
			this.hashOfChromosomesInPopulation.add(hash);
		}
		this.population.sort(ChromosomeComparator.obj);
	}

	public void decreasePopulationSize(int decrementCount)
	{
		for (int i = 0; i < decrementCount; i++)
		{
			String hash = this.population.get(this.population.size() - 1).toString(this.problemData.getCoursesToBeScheduled());
			this.totalPopulationFitness -= this.population.get(this.population.size() - 1).getFitnessValue();
			this.totalPopulationUnfitness -= this.population.get(this.population.size() - 1).getUnfitnessValue();
			this.population.remove(this.population.size() - 1);

			this.hashOfChromosomesInPopulation.remove(hash);
		}
	}

	public void initPopulation(int populationSize)
	{
		this.population = new ArrayList<Chromosome>(populationSize);
		for (int i = 0; i < populationSize; i++)
		{
			Chromosome c = null;
			String hash = null;

			do
			{
				c = new Chromosome(random, problemData); //calling the constructor that randomly creates a schedule
				hash = c.toString(this.problemData.getCoursesToBeScheduled());

			} while (this.hashOfChromosomesInPopulation.contains(hash));


			this.population.add(c);
			this.hashOfChromosomesInPopulation.add(hash);
		}

		this.evaluatePopulation();
		this.sortPopulationAccordingToFitness();
	}

	public Chromosome doCrossOver(int tournamentSize)
	{
		Chromosome p1 = tournament(tournamentSize);
		Chromosome p2 = tournament(tournamentSize);

		while (p2 == p1)
			p2 = tournament(tournamentSize);

		return new Chromosome(p1, p2, random, problemData); //calling the crossover constructor

	}

	public Chromosome doCrossOver(Chromosome p1, Chromosome p2)
	{
		return new Chromosome(p1, p2, random, problemData); //calling the crossover constructor
	}

	public void doMutation(Chromosome child, double mutationRate)
	{
		child.mutate(problemData, random, mutationRate);
	}

	public void insertNewChromsomesInPopulationIfTheyAreFitter(ArrayList<Chromosome> array)
	{
		int i = 0;
		int j = this.population.size() - 1;
		while (j >= 0 && i < array.size())
		{
			String currentChildHash = array.get(i).toString(this.problemData.getCoursesToBeScheduled());
			boolean isCurrentChildDuplicate = this.hashOfChromosomesInPopulation.contains(currentChildHash);

			if (isCurrentChildDuplicate == false && ChromosomeComparator.obj.compare(array.get(i), this.population.get(j)) <= 0)
			{
				this.totalPopulationFitness -= this.population.get(j).getFitnessValue();
				this.totalPopulationUnfitness -= this.population.get(j).getUnfitnessValue();

				this.hashOfChromosomesInPopulation.remove(this.population.get(j).toString(this.problemData.getCoursesToBeScheduled()));
				this.hashOfChromosomesInPopulation.add(currentChildHash);
				this.population.set(j, array.get(i));

				this.totalPopulationFitness += this.population.get(j).getFitnessValue();
				this.totalPopulationUnfitness += this.population.get(j).getUnfitnessValue();

				j--;
				i++;
			}

			if (this.hashOfChromosomesInPopulation.contains(currentChildHash) == false)
				//remove else when uncommenting if..add else when commenting if
				/*else*/ break; //the currentChild is worse than current chromosome in population and does
								//not already exist, so remaining children are also worse.

			i++; //try next child because current child already exists in the population.
		}

		if (j != this.population.size() - 1) //if some new children have been added to population
			this.sortPopulationAccordingToFitness();
	}

	public void sortAccordingToFitness(ArrayList<Chromosome>array)
	{
		array.sort(ChromosomeComparator.obj);
	}

	public Chromosome worst()
	{
		return population.get(population.size() - 1);
	}

	/* returns the best genotype of the current population */
	public Chromosome best()
	{
		return population.get(0);
	}

	public Chromosome tournament(int tournamentSize)
	{
		Chromosome currentBest = this.population.get(this.random.nextInt(this.population.size()));
		for (int j = 1; j < tournamentSize; j++)
		{
			int randomIndex = this.random.nextInt(this.population.size());
			Chromosome nextProspectiveParent = this.population.get(randomIndex);

			if (ChromosomeComparator.obj.compare(nextProspectiveParent, currentBest) < 0)
				currentBest = nextProspectiveParent;
		}
		return currentBest;
	}

	public void evaluatePopulation()
	{
		this.totalPopulationFitness = this.totalPopulationUnfitness = 0;
		for (int i = 0; i < this.population.size(); i++)
		{
			this.evaluateChromosome(this.population.get(i));
			this.totalPopulationFitness += this.population.get(i).getFitnessValue();
			this.totalPopulationUnfitness += this.population.get(i).getUnfitnessValue();
		}
	}

	public void sortPopulationAccordingToFitness()
	{
		population.sort(ChromosomeComparator.obj);
	}

}
