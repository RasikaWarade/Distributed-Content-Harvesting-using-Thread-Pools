package cs455.harvester;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Thread{

	private static int dest_Port = 0;
	private static String dest_host = null;
	private static String message=null;
	
	public Client(String dest, String outGoingUrl) {
		// TODO Auto-generated constructor stub
		String delims=":";
		String tokens[]=dest.split(delims);
		dest_host=tokens[0].trim()+"cs.colostate.edu";
		dest_Port=Integer.parseInt(tokens[1].trim());
		message=outGoingUrl;
	}

	public void run() {
		// Declare the variables
		Socket clientSocket = null;
		DataOutputStream out = null;
		DataInputStream in = null;

		try {
			// create socket at client
			clientSocket = new Socket(dest_host, dest_Port);
			//System.out.println("get Port:" + clientSocket.getPort());
			//System.out.println("Local Port:" + clientSocket.getLocalPort());
			System.out.println("Connection on host:" + dest_host + " and Port: "
					+ dest_Port);
			// initialize input and output streams over the socket to perform
			// read and write with the server
			out = new DataOutputStream(clientSocket.getOutputStream());
			out.flush();
			in = new DataInputStream(clientSocket.getInputStream());
			
			byte[] data = new byte[message.getBytes().length];
			data = message.getBytes();
			int datalength = data.length;
			System.out.println("Client>:" + data.toString());
			out.writeInt(datalength);
			out.write(data, 0, datalength);
			out.flush();
		} catch (UnknownHostException e) {
			System.err.println("Unknown host: " + dest_host);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: "
					+ dest_host);
			e.printStackTrace();
			System.exit(1);
		}
		finally{
		
		

		// close socket connection when command is executed
		// close input and output streams
		try {
			clientSocket.close();
			in.close();
			out.close();
			System.out.println("Exiting CrawlerClient!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		}
	}
}
