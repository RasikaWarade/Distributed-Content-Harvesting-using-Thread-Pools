package cs455.harvester;

import net.htmlparser.jericho.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Crawler {
	//Socket connections
	// Declare the variables
	static ServerSocket serv = null;
	static Socket connection = null;
	String clientIP = null;
	static int currPort = 0;
	static private DataOutputStream out = null;
	static private DataInputStream in = null;

	static String config_file;
	static  HashMap<String,String> config=new HashMap<String,String>();
	
	static HashMap<String, Integer> tasks = new HashMap<String, Integer>();
	static HashSet<String> completedTasks = new HashSet<String>();
	static HashSet<String> brokenLinks = new HashSet<String>();
	static Map<String, HashMap<String, String>> outerMap = new HashMap<String, HashMap<String, String>>();
	 
	static HashMap<String, String> outKeys = new HashMap<String, String>();
	static HashMap<String, String> inKeys = new HashMap<String, String>();
	
	private static ThreadPoolManager TPM = null;// declare the threadpool
												// manager
	static List<Task> taskQueue = Collections
			.synchronizedList(new LinkedList<Task>());
	public static String root_url = "";
	public static int thread_pool_size = 0;
	static boolean finishedJob = false;
	static int incr=0;

	public static void listen(){
		try {

			serv = new ServerSocket(currPort, 3000000); // create new server
			// socket
			
			System.out.println("Serv Local Port:"+serv.getLocalPort());
			System.out.println("ClientServerSocket Created");
			System.out.println("Waiting for connection on port no : "
					+ currPort);
			// Accepting the connections
			connection = serv.accept();
			System.out.println("Connection received from "
					+ connection.getInetAddress().getHostName());

			// output and input stream
			out = new DataOutputStream(connection.getOutputStream());
			out.flush();
			in = new DataInputStream(connection.getInputStream());

			// Listen on the socket for the reply		
			//recvdTasks
			int datalength2 = 0;
			if ((datalength2 = in.readInt()) != 0) {
				byte[] data2 = new byte[datalength2];
				in.readFully(data2, 0, datalength2);
				String recv = new String(data2);
				System.out.println("Request>" + recv);
			}

		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {

			try {
				in.close();
				out.close();
				connection.close();
				serv.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}
	public void run(){
		System.out.println("Started Thread for CrawlerServer!");
		while(true){
			listen();
		}
	}
	/**
	 * @param args
	 * @throws InterruptedException
	 * 
	 */
	public static void addToCompletedTask(Task t) {

		// checking if the task is duplicated ###
		if (completedTasks.contains(t.getURL())) {
			System.out.println("Duplicate!");
		} else {
			completedTasks.add(t.getURL());
		}
	}

	public static void main(String[] args) throws InterruptedException, IOException {

		int portnum = Integer.parseInt(args[0]);
		currPort=portnum;
		String path_config = args[3];
		config_file=path_config;
		// Above variables used for hand-off between crawlers

		thread_pool_size = Integer.parseInt(args[1]);
		root_url = args[2].trim();
		System.out.println(" REQUEST: " + args[0] + " " + args[1] + " "
				+ args[2] + " " + args[3]);

		TPM = new ThreadPoolManager(thread_pool_size);
		System.out.println("Thread pool manager constructor called!");
		TPM.init();
		System.out.println("TPM init!");
		
		
		//**********Store the domains in the hasmap
		//String filename = "filenames.txt";
		FileReader rstream = new FileReader(path_config);
		BufferedReader br = new BufferedReader(rstream);
		int i = 0;
		String line;
		String delims=",";
		while ((line = br.readLine()) != null) {
			String tokens[]=line.split(delims);
			config.put(tokens[0].trim(), tokens[1].trim());
			i++;
		}

		br.close();
		//***********
		 Set<String> setOfKeys = config.keySet();
		 Iterator iterator = setOfKeys.iterator();
		while (iterator.hasNext()) {
		String key = (String) iterator.next();
		 String value = (String)config.get(key);
		 
		//System.out.println("Key: "+ key+", Value: "+ value);
		 
		 }
		 
		
		//*********** create the directory structure
		int start=root_url.indexOf("www");
		int end=root_url.indexOf("edu");
		String fileName="tmp/cs455-rsw/"+root_url.substring(start, end+3)+"/nodes";
		File theDir = new File(fileName);

		  // if the directory does not exist, create it
		  if (!theDir.exists()) {
		    System.out.println("creating directory: " + "tmp");
		    boolean result = false;

		    try{
		        theDir.mkdirs();
		     } catch(SecurityException se){
		        //handle it
		     }       
		  }
		//**********
		 //Crawler server = new Crawler();  
		//server.listen();$$$
		  
		startTask(root_url);

		// how to end
		// how to check if crawler has finished recurr depth
		// *****************************
		//System.out.println("All Completed Tasks at this crawler:");
		Iterator iterator2 = completedTasks.iterator();
		while (iterator2.hasNext())
			System.out.println(iterator2.next());
		// *****************************

		// before exiting write into the files

		// system.exit()...as it will be an active server
	}

	private static void startTask(String root_url) throws InterruptedException {

		// System.out.println("Synchronized in crawler!");
		synchronized (taskQueue) {
			System.out.println("Waiting 10 sec before crawling......");
			Thread.sleep(10000);// w8 10 seconds before crawling ***
			System.out
					.println("------------------------------Start Crawling---------------------------------");
			taskQueue.add(new Task(root_url, 5));
			System.out.println("Job added to queue!");
			taskQueue.notify();
			//System.out.println("Notified!");
		}
		// System.out.println("Out of Syncronized crawler block!");

	}

	public static void maintainListOfCompletedTasks(Task t) {
		// Store the list***
		System.out.println("Given task completed:" + t.getURL() + "  "
				+ t.getRecurr_level());
		//addToCompletedTask(t);
	}

}
