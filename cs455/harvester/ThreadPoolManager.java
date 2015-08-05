package cs455.harvester;

public class ThreadPoolManager {
	Worker[] workers = null;
	private static int no=0; //no of threads to start
	
	public ThreadPoolManager(int noOfThreads){
		//Initialization of preconfigured worker threads
		//set of threads initialized and kept alive till process termination
		
		workers=new Worker[noOfThreads]; //initialized the worker threads
		//System.out.println("No of Threads:"+noOfThreads);
		no=noOfThreads;
	}
	void init()
	{
		for(int i = 0; i < no; i++)
		{
			workers[i] = new Worker(i);
			Thread t = new Thread(workers[i]);
			t.start();
			//System.out.println("Thread:"+workers[i]+" started!");
			//only one task at a time
		}
	}
}
