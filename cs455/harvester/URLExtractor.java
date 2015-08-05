package cs455.harvester;

import net.htmlparser.jericho.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;

public class URLExtractor {

	public static boolean checkDomain(String pageURL, String rootUrl) {
		try {
			return new URL(pageURL).getHost()
					.equals(new URL(rootUrl).getHost());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// handle pschycology with custom logic ***
		return false;
	}

	public static String resolvedURL(String outGoingUrl, String pageUrl) {

		// Check if it is relative urls
		try {
			if (!new URI(outGoingUrl).isAbsolute()) {
				URI resolvedUrl = new URI(pageUrl).resolve(outGoingUrl);
				System.out.println("Resolved URL:" + resolvedUrl.toString());
				outGoingUrl = resolvedUrl.toString();
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outGoingUrl;
	}

	public static boolean checkFormat(String outGoingUrl) {
		// only support HTTP
		if (!outGoingUrl.contains("http://")) {

			// check if it is http supported

			if (outGoingUrl.contains("ftp://")
					|| outGoingUrl.contains("https://")
					|| outGoingUrl.contains("mailto")) {
				System.out.println("Not Supported!");
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
		} else if (outGoingUrl.contains(".doc") || outGoingUrl.contains(".pdf")) {
			// check if the url if not a doc or pdf
			//if it is jus store in the files *** no need to crawl
			return false;
		}

		return true;
	}

	public static void main(String[] args) throws MalformedURLException {
		// disable verbose log statements
		HashSet<String> hs = new HashSet<String>();
		Config.LoggerProvider = LoggerProvider.DISABLED;
		try {
			// web page that needs to be parsed
			final String pageUrl = "http://www.cs.colostate.edu/cstop/index.html";
			Source source = new Source(new URL(pageUrl));
			// get all 'a' tags
			List<Element> aTags = source.getAllElements(HTMLElementName.A);
			// get the URL ("href" attribute) in each 'a' tag
			for (Element aTag : aTags) {

				// print the url
				System.out.println("PageCrawled:"
						+ aTag.getAttributeValue("href"));
				String outGoingUrl = aTag.getAttributeValue("href").toString();

				// check if its relative
				outGoingUrl = resolvedURL(outGoingUrl, pageUrl);
				// check if http and not ftp,mailto,doc,pdf
				if (checkFormat(outGoingUrl)) {
				// check if its in the domain
				if (checkDomain(outGoingUrl, pageUrl)) {
					System.out.println("Its in the domain!");
					
						// redirect requests

						// broken lines -403

						System.out.println("Proceed as Task:" + outGoingUrl);
						
						//checking if the task is duplicated ###
						if(hs.contains(outGoingUrl)){
							System.out.println("Duplicate!");
						}else{
							hs.add(outGoingUrl);
						}
						
						
					}
				else{
					//*** if its not in domain check if its one of the 8 domains
					//*** task hand-off
				}
				}

			}

		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
