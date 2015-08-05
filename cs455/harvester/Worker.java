package cs455.harvester;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Worker extends Crawler implements Runnable {

	// Each worker is assigned a id
	private int workId;
	Task newTask;

	public Worker() {

	}

	// set worker id
	public Worker(int setId) {
		this.workId = setId + 1;
	}

	// get worker id

	public int getWorkId() {
		return workId;
	}

	// run the current worker
	// *** override?
	// @Override
	public void run() {

		System.out.println("Thread[" + workId + "] is waiting for work !!");
		while (true) {
			synchronized (taskQueue) {
				if (taskQueue.isEmpty()) {
					try {

						// System.out.println("No job....waiting!");
						//Thread.sleep(1000);//sleep Niceness***$$$
						taskQueue.wait();
						if(finishedJob){
							System.out.println("Thread[" + workId + "] finished processing tasks!");
							incr++;
							if(incr==thread_pool_size-1){
								System.out.println("Broken Links Size:"+brokenLinks.size());
								System.out.println("Outkeys Size:"+outKeys.size());

								int start=root_url.indexOf("www");
								int end=root_url.indexOf("edu");

								String fileName="tmp/cs455-rsw/"+root_url.substring(start, end+3)+"/broken-links.txt";

								File file = new File(fileName);
								// if file doesnt exists, then create it
								if (!file.exists()) {
									file.createNewFile();
								}
								PrintStream out = new PrintStream(new FileOutputStream(fileName));
								Iterator hashSetIterator = brokenLinks.iterator();
								while(hashSetIterator.hasNext()){
									out.println(hashSetIterator.next());
								}
								out.close();


								////Create the directory structure

								System.out.println("-------------------Out Keys------------------");
								// Get a set of the entries 
								Set set = outKeys.entrySet(); 
								// Get an iterator 
								Iterator i = set.iterator(); 
								// Display elements 
								int j=0;
								while(i.hasNext()){ //&& j<2) { 
									Map.Entry me = (Map.Entry)i.next(); 
									//System.out.println(me.getKey()); 
									String key=me.getKey().toString();
									String dir=null;
									
									
									
									if(key.compareToIgnoreCase(root_url)==0){
										//dir=String.valueOf(key.replace(root_url, ""));
										System.out.println(key);
										dir=key.replace("http://", "");
										System.out.println("DIR:"+dir);
									}
									else{
										dir=String.valueOf(key.replace(root_url, ""));
										dir=dir.replace("/", "-");
									}
									
									String fileN="tmp/cs455-rsw/"+root_url.substring(start, end+3)+"/nodes/"+dir+"/";
									File theDir = new File(fileN);

									// if the directory does not exist, create it
									if (!theDir.exists()) {

										boolean result = false;

										try{
											theDir.mkdir();
											//System.out.println("Created Directory:");
										} catch(SecurityException se){
											//handle it
										}       
									}
									String fileN2="tmp/cs455-rsw/"+root_url.substring(start, end+3)+"/nodes/"+dir+"/out.txt";

									//System.out.println("key:"+key+" val:"+me.getValue()); 
									//System.out.println(" ------------ ");
									File file4 = new File(fileN2);
									// if file doesnt exists, then create it
									if (!file4.exists()) {
										file4.createNewFile();
									}
									PrintStream out2 = new PrintStream(new FileOutputStream(fileN2));

									out2.println(me.getValue().toString());

									out2.close();
									//j++;
								} 

								

								System.out.println("Inkeys Size:"+outKeys.size());
								System.out.println("-------------------In Keys------------------");
								// Get a set of the entries 
								Set set2 = inKeys.entrySet(); 
								// Get an iterator 
								Iterator i2 = set2.iterator(); 
								// Display elements 
								int j2=0;
								while(i2.hasNext()){// && j2<2) { 
									Map.Entry me2 = (Map.Entry)i2.next(); 
									//System.out.println(me2.getKey()); 
									String key=me2.getKey().toString();
									String dir=null;
									if(key.compareToIgnoreCase(root_url)==0){
										//dir=String.valueOf(key.replace(root_url, ""));
										System.out.println(key);
										dir=key.replace("http://", "");
										System.out.println("DIR:"+dir);
									}
									else{
										dir=String.valueOf(key.replace(root_url, ""));
										dir=dir.replace("/", "-");
									}
									
									String fileN="tmp/cs455-rsw/"+root_url.substring(start, end+3)+"/nodes/"+dir+"/";
									File theDir = new File(fileN);

									// if the directory does not exist, create it
									if (!theDir.exists()) {

										boolean result = false;

										try{
											theDir.mkdir();
											
										} catch(SecurityException se){
											//handle it
										}       
									}
									String fileN2="tmp/cs455-rsw/"+root_url.substring(start, end+3)+"/nodes/"+dir+"/in.txt";

									//System.out.println("key:"+key+" val:"+me2.getValue()); 
									//System.out.println(" ------------ ");
									File file4 = new File(fileN2);
									// if file doesnt exists, then create it
									if (!file4.exists()) {
										file4.createNewFile();
									}
									PrintStream out2 = new PrintStream(new FileOutputStream(fileN2));

									out2.println(me2.getValue().toString());

									out2.close();
									//j2++;
								} 
								/*
								Set set2 = inKeys.entrySet(); 
								// Get an iterator 
								Iterator i2 = set2.iterator(); 
								// Display elements 
								int j2=0;
								while(i2.hasNext() && j2<2) { 
									Map.Entry me2 = (Map.Entry)i2.next(); 
									System.out.println(me2.getKey()); 
									String key2=me2.getKey().toString();

									System.out.println("key:"+key2+" val:"+me2.getValue()); 
									j2++;
								} 
								*/

								////
								Thread.sleep(10000);
								System.out.println("Exiting....!");
								System.exit(0);
							}

						}

					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					System.out.println("Jobqueue is not empty");
					newTask = taskQueue.remove(0);
					try {
						Thread.sleep(1000);
						newTask.ToString();
						newTask.execute();
						//maintainListOfCompletedTasks(newTask);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}//waiting for 1sec
				}
			}

		}
	}
}
