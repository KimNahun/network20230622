
import java.io.*;
import java.net.ServerSocket;
import java.util.*;

public class Message {

	static final String endMessage = ".";
	static String messageName= "";
	static String messageportNum= "";
	static String messagehostName= "";   
	public static void main(String[] args) {
		InputStreamReader is = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(is);
		try {
			System.out.println("Welcome to the Echo client.\n" +
					"What is the name of the server host?");
			String hostName = br.readLine();
			if (hostName.length() == 0)
				hostName = "localhost";  
			System.out.println("What is the port number of the server host?");
			String portNum = br.readLine();
			if (portNum.length() == 0)
				portNum = "7";         
			EchoClientHelper1 helper = 
					new EchoClientHelper1(hostName, portNum);
			boolean done = false;
			String message, echo;

			System.out.println("Write your Servername");   
			messageName = br.readLine();
			System.out.println("Write your hostName");
			messagehostName = br.readLine();
			System.out.println("Write your portNum");   
			messageportNum = br.readLine();


			String serverInfo = messageName + " " + messagehostName + " " + messageportNum;
			echo = helper.getEcho(serverInfo);

			System.out.println("1: if you want perform your task's duties \n  if you want yo exit, press 0");
			int num=Integer.parseInt(br.readLine());
			if (num==1) {
				try {

					ServerSocket myConnectionSocket = 
							new ServerSocket(Integer.parseInt(args[0])); 
					System.out.println("Echo server ready.");  
					while (true) {  

						System.out.println("Waiting for a connection.");
						MyStreamSocket myDataSocket = new MyStreamSocket
								(myConnectionSocket.accept());
						System.out.println("connection accepted");

						EchoServerThread myServerRunnable = new EchoServerThread(myDataSocket, 1);  
Thread myServerThread = new Thread(myServerRunnable);
myServerThread.start();

					} 
				} 
				catch (Exception ex) {
					ex.printStackTrace( );
				} 
			}
			else {
				System.out.println("Exit");
			}


		} 
		catch (Exception ex) {
			ex.printStackTrace( );
		} 
	} 
} 