package ga_classes;
import java.util.concurrent.locks.ReentrantLock;

public class GA_HParameters {
	
	public static int populationSize=100;
	public static double mutationRate=0.05;
	//public static int totalIterations=-1;
	public static int tournamentSize=5;
	public static int childrenPerIteration=1;
	
	
	public static ReentrantLock mutex = new ReentrantLock();
	public static ReentrantLock exportScheduleMutex= new ReentrantLock();
	public static boolean parameterValuesChanged=false;

}