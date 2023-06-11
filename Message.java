import java.io.*;
import java.net.ServerSocket;
import java.util.*;
/**
 * This module contains the presentaton logic of an Echo Client.
 * @author M. L. Liu
 */
public class Message {

	static final String endMessage = ".";
	static String messageName= "";
	static String messageportNum= "";
	static String messagehostName= "";   
	static ArrayList<String[]> list=new ArrayList<>();
	public static void main(String[] args) {
		InputStreamReader is = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(is);
		try {
			System.out.println("Welcome to the Echo client.\n" +
					"What is the name of the server host?");
			String hostName = br.readLine();
			if (hostName.length() == 0) // if user did not enter a name
				hostName = "localhost";  //   use the default host name
			System.out.println("What is the port number of the server host?");
			String portNum = br.readLine();
			if (portNum.length() == 0)
				portNum = "7";          // default port number
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

			// Send server information to Register

			String serverInfo = messageName + " " + messagehostName + " " + messageportNum;
			echo = helper.getEcho(serverInfo);

			System.out.println("1: if you want perform your task's duties \n  if you want yo exit, press 0");
			int num=Integer.parseInt(br.readLine());
			if (num==1) {
				 try {
			         // instantiates a stream socket for accepting
			         //   connections
			   	   ServerSocket myConnectionSocket = 
			            new ServerSocket(Integer.parseInt(args[0])); 
			/**/     System.out.println("Echo server ready.");  
			         while (true) {  // forever loop
			            // wait to accept a connection 
			/**/        System.out.println("Waiting for a connection.");
			            MyStreamSocket myDataSocket = new MyStreamSocket
			                (myConnectionSocket.accept( ));
			/**/        System.out.println("connection accepted");
			            // Start a thread to handle this client's sesson
			            Thread theThread = 
			               new Thread(new EchoServerThread(myDataSocket));
			            theThread.start();
			            // and go on to the next client
			            } //end while forever
			       } // end try
				    catch (Exception ex) {
			          ex.printStackTrace( );
				    } // end catch
			}
			else {
				System.out.println("Exit");
			}


//			while (!done) {
				//            System.out.println("Enter a line to receive an echo back from the server, "
				//                            + "or a single peroid to quit.");
				//            message = br.readLine( );
				//            if ((message.trim()).equals (endMessage)){
				//               done = true;
				//               helper.done( );
				//            }
				//            else {
				//               echo = helper.getEcho(message);
				//               System.out.println(echo);
				//            }
		//	} // end while
		} // end try  
		catch (Exception ex) {
			ex.printStackTrace( );
		} // end catch
	} //end main
} // end class      