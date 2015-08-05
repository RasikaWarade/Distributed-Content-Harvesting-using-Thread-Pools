package cs455.harvester;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.htmlparser.jericho.Config;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.LoggerProvider;
import net.htmlparser.jericho.Source;

public class Task extends Crawler {

	private String URL = "";
	private int recurr_level = 5;

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	public int getRecurr_level() {
		return recurr_level;
	}

	public void setRecurr_level(int recurrLevel) {
		recurr_level = recurrLevel;
	}

	public Task(String url, int depth) {
		this.URL = url;
		this.recurr_level = depth;
	}

	public void ToString() {
		System.out.println("Task URL:" + URL + " DEPTH:" + this.recurr_level);
	}

	public static boolean checkDomain(String pageURL, String rootUrl) {
		try {
			return new URL(pageURL).getHost()
					.equals(new URL(rootUrl).getHost());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			if (e.toString().contains("unknown protocol")) {
				// System.out.println("Broken Link!-----"+pageURL); //*** broken
				// links
				// brokenLinks.add(pageURL); //no support for the protocols
			} else if (e.toString().contains("no protocol")) {
				// System.out.println("Broken Link!-----"+pageURL); //*** broken
				// links
				// brokenLinks.add(pageURL);
			} else {
				System.out.println(pageURL);
				e.printStackTrace();
			}
		}
		// handle pschycology with custom logic ***
		return false;
	}
	
	public static String resolveRedirects(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection)(new URL(url).openConnection());
        con.setInstanceFollowRedirects(false);
        con.connect();
        int responseCode = con.getResponseCode();
        if(responseCode == 301){
            return con.getHeaderField( "Location" );
        } else {
            return url;
        }
    }
	

	public static String resolvedURL(String outGoingUrl, String pageUrl) {

		// Check if it is relative urls
		try {
			if (!new URI(outGoingUrl).isAbsolute()) {
				URI resolvedUrl = new URI(pageUrl).resolve(outGoingUrl);
				// System.out.println("Resolved URL:" + resolvedUrl.toString());
				outGoingUrl = resolvedUrl.toString();
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			if (e.toString().contains("URISyntaxException")) {
				System.out.println("Broken Link!-----" + outGoingUrl);// ***
																		// broken
				brokenLinks.add(outGoingUrl);
			} else {
				System.out.println("ERROR IN URL:'" + outGoingUrl + "'");
				e.printStackTrace();
			}

		}
		return outGoingUrl;
	}

	public static boolean checkFormat(String outGoingUrl, String pageURL) {
		// only support HTTP
		if (!outGoingUrl.contains("http://")) {

			// check if it is http supported

			if (outGoingUrl.contains("ftp://")
					|| outGoingUrl.contains("https://")
					|| outGoingUrl.contains("mailto")
					|| outGoingUrl.contains("javascript:")
					|| outGoingUrl.contains("/?")) {
				// System.out.println("Not Supported!");
				/*
				 * HttpURLConnection connection = null; URL u = new
				 * URL(outGoingUrl); connection = (HttpURLConnection)
				 * u.openConnection(); connection.setRequestMethod("HEAD"); int
				 * code = connection.getResponseCode(); if (code == 200)
				 * System.out.println("HTTP PROTOCOL! " + code); // You can
				 * determine on HTTP return code received. 200 is // success.
				 */
				return false;
			}
		} else if (outGoingUrl.contains(".doc") || outGoingUrl.contains(".pdf")
				|| outGoingUrl.contains(".exe") || outGoingUrl.contains(".tar")
				|| outGoingUrl.contains(".gz") || outGoingUrl.contains(".zip")
				|| outGoingUrl.contains("ps.Z")|| outGoingUrl.contains("/?") ){
			// check if the url if not a doc or pdf
			// if it is jus store in the files *** no need to crawl
			// System.out.println("---------DOC/PDF----------------");
			if (outKeys.containsKey(pageURL)) {
				outKeys.put(pageURL, outKeys.get(pageURL) + "\n" + outGoingUrl);
				
			} else {
				outKeys.put(pageURL, outGoingUrl);
			}
			//completedTasks.add(outGoingUrl);//$$$
			return false;
		}

		return true;
	}

	public void execute() {

		// reduce the level
		// this.recurr_level = this.recurr_level - 1;
		if (this.recurr_level != 1) {// *** should be 1
			System.out.println("Crawling the task given:" + this.URL);
			System.out.println("DEPTH:" + this.recurr_level);

			Config.LoggerProvider = LoggerProvider.DISABLED;
			try {
				// web page that needs to be parsed
				final String pageUrl = this.URL;
				Source source = new Source(new URL(pageUrl));
				// get all 'a' tags
				List<Element> aTags = source.getAllElements(HTMLElementName.A);
				// get the URL ("href" attribute) in each 'a' tag
				for (Element aTag : aTags) {

					// print the url
					// System.out.println("PageCrawled:"
					// + aTag.getAttributeValue("href"));
					if (aTag.getAttributeValue("href") != null) {
						String outGoingUrl = aTag.getAttributeValue("href")
								.toString().trim();

						// check if its relative
						outGoingUrl = resolvedURL(outGoingUrl, pageUrl);
						// check if http and not ftp,mailto,doc,pdf
						if (checkFormat(outGoingUrl, this.URL)) {
							// check if its in the domain
							if (checkDomain(outGoingUrl, pageUrl)) {
								outGoingUrl=resolveRedirects(outGoingUrl); //$$$
								// System.out.println("Its in the domain!");

								// checking if the task is duplicated ###
								if (completedTasks.contains(outGoingUrl)) {
									// System.out.println("Duplicate!");
									/*
									 * System.out.println("Tasks at Crawler!*************"
									 * ); Iterator iterator =
									 * completedTasks.iterator(); while
									 * (iterator.hasNext()) System.out.println(
									 * iterator.next() );System.out.println(
									 * "Tasks at Crawler!*************");
									 */

									if (inKeys.containsKey(outGoingUrl)) {
										inKeys.put(outGoingUrl, inKeys
												.get(this.URL)
												+ "\n" + this.URL);
									} else {
										inKeys.put(outGoingUrl, this.URL);
									}
									
								} else {
								// redirect requests

									// broken lines -403

									// $$$System.out.println("Proceed as Task:"
									// $$$ + outGoingUrl);
									if (outKeys.containsKey(this.URL)) {
										outKeys.put(this.URL, outKeys
												.get(this.URL)
												+ "\n" + outGoingUrl);
									} else {
										outKeys.put(this.URL, outGoingUrl);
									}

									if (inKeys.containsKey(outGoingUrl)) {
										inKeys.put(outGoingUrl, inKeys
												.get(this.URL)
												+ "\n" + this.URL);
									} else {
										inKeys.put(outGoingUrl, this.URL);
									}

									// outReq=outGoingUrl+"\n";
									synchronized (taskQueue) {
										taskQueue.add(new Task(outGoingUrl,
												this.recurr_level - 1));

										// $$$System.out
										// $$$ .println("Job added to queue!");
										taskQueue.notify();
									}
									completedTasks.add(outGoingUrl);

								}

							} else {
								// *** if its not in domain check if its one of
								// the 8 domains
								// *** task hand-off

								Set setOfKeys = config.keySet();
								Iterator iterator = setOfKeys.iterator();
								while (iterator.hasNext()) {
									String key = (String) iterator.next();
									String value = (String) config.get(key);

									// System.out.println("Key: "+
									// key+", Value: "+ value);
									if (checkDomain(outGoingUrl, value)) {
										// System.out.println("###########Other Domain########## "+
										// value);
										// Client client = new
										// Client(key,outGoingUrl); $$$
										// client.start();
									}
								}

							}
						}
					}
				}
				System.out.println("Current URL:" + this.URL);
				// System.out.println("--------------------------");
				// System.out.println(""+outReq);
				// innerMap.put("out",outReq);
				// System.out.println("--------------------------");
				// outerMap.put(this.URL, innerMap);

			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block

				// if broken link found 403 exception ***
				if (e.toString().contains("HTTP response code: 403")
						|| e.toString().contains("HTTP response code: 400")) {
					System.out.println("Broken Link!-----" + this.URL);
					brokenLinks.add(this.URL);
				} else if (e.toString().contains("FileNotFoundException")) {
					System.out.println("Broken Link!-----" + this.URL);
					brokenLinks.add(this.URL);
				} else {
					System.out.println(this.URL);
					e.printStackTrace();
				}
			}

		}else if(this.recurr_level==1){
			System.out.println("Crawling the task given:" + this.URL);
			System.out.println("DEPTH:" + this.recurr_level);

			Config.LoggerProvider = LoggerProvider.DISABLED;
			try {
				// web page that needs to be parsed
				final String pageUrl = this.URL;
				Source source = new Source(new URL(pageUrl));
				// get all 'a' tags
				List<Element> aTags = source.getAllElements(HTMLElementName.A);
				// get the URL ("href" attribute) in each 'a' tag
				for (Element aTag : aTags) {

					// print the url
					// System.out.println("PageCrawled:"
					// + aTag.getAttributeValue("href"));
					if (aTag.getAttributeValue("href") != null) {
						String outGoingUrl = aTag.getAttributeValue("href")
								.toString().trim();

						// check if its relative
						outGoingUrl = resolvedURL(outGoingUrl, pageUrl);
						// check if http and not ftp,mailto,doc,pdf
						if (checkFormat(outGoingUrl, this.URL)) {
							// check if its in the domain
							if (checkDomain(outGoingUrl, pageUrl)) {
								// System.out.println("Its in the domain!");

								// checking if the task is duplicated ###
								if (completedTasks.contains(outGoingUrl)) {
									// System.out.println("Duplicate!");
									/*
									 * System.out.println("Tasks at Crawler!*************"
									 * ); Iterator iterator =
									 * completedTasks.iterator(); while
									 * (iterator.hasNext()) System.out.println(
									 * iterator.next() );System.out.println(
									 * "Tasks at Crawler!*************");
									 */
									if (inKeys.containsKey(outGoingUrl)) {
										inKeys.put(outGoingUrl, inKeys
												.get(this.URL)
												+ "\n" + this.URL);
									} else {
										inKeys.put(outGoingUrl, this.URL);
									}
									
								} else {
								// redirect requests

									// broken lines -403

									// $$$System.out.println("Proceed as Task:"
									// $$$ + outGoingUrl);
									
									///////////////////////////////////////////////
									if (outKeys.containsKey(this.URL)) {
										outKeys.put(this.URL, outKeys
												.get(this.URL)
												+ "\n" + outGoingUrl);
									} else {
										outKeys.put(this.URL, outGoingUrl);
									}

								}

							} else {
								// *** if its not in domain check if its one of
								// the 8 domains
								// *** task hand-off

								Set setOfKeys = config.keySet();
								Iterator iterator = setOfKeys.iterator();
								while (iterator.hasNext()) {
									String key = (String) iterator.next();
									String value = (String) config.get(key);

									// System.out.println("Key: "+
									// key+", Value: "+ value);
									if (checkDomain(outGoingUrl, value)) {
										// System.out.println("###########Other Domain########## "+
										// value);
										// Client client = new
										// Client(key,outGoingUrl); $$$
										// client.start();
									}
								}

							}
						}
					}
				}
				System.out.println("Current URL:" + this.URL);
				// System.out.println("--------------------------");
				// System.out.println(""+outReq);
				// innerMap.put("out",outReq);
				// System.out.println("--------------------------");
				// outerMap.put(this.URL, innerMap);

			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block

				// if broken link found 403 exception ***
				if (e.toString().contains("HTTP response code: 403")
						|| e.toString().contains("HTTP response code: 400")) {
					System.out.println("Broken Link!-----" + this.URL);
					brokenLinks.add(this.URL);
				} else if (e.toString().contains("FileNotFoundException")) {
					System.out.println("Broken Link!-----" + this.URL);
					brokenLinks.add(this.URL);
				} else {
					System.out.println(this.URL);
					e.printStackTrace();
				}
			}
			System.out.println("Crawled the task given:" + this.URL);
			System.out.println("DEPTH:" + String.valueOf(this.recurr_level-1));

				///Thread.sleep
				finishedJob = true;

			}

		}
	}

