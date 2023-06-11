import java.io.*;
import java.util.*;


class EchoServerThread implements Runnable {
	static ArrayList<String[]> messageList = new ArrayList<>();
	static final String endMessage = ".";
	MyStreamSocket myDataSocket;

	EchoServerThread(MyStreamSocket myDataSocket) {
		this.myDataSocket = myDataSocket;
	}

	public void run( ) {
		boolean done = false;
		String message;
		try {
			while (!done) {
				message = myDataSocket.receiveMessage( );
				String temp[]= message.split(" ");
				String userMessage= temp[0];
				String userName = temp[1];
				System.out.println(userName +": "+ userMessage);
				if(userMessage.trim().equals("1")) {
					System.out.println("-----------------");
					System.out.println("Show all Message");
					System.out.println();
					for(int i=0;i<messageList.size();i++){
						System.out.println(messageList.get(i)[1]+":  "+messageList.get(i)[0]);
					}
					myDataSocket.sendMessage(userMessage);
					System.out.println();
					System.out.println("-----------------");
				}
				else if ((userMessage.trim()).equals (endMessage)){

					System.out.println("Session over.");

					done = true;
				} 
				else {


					myDataSocket.sendMessage(userMessage);

					messageList.add(new String[]{userMessage,userName});
				} 
			} 
		}
		catch (Exception ex) {
			System.out.println("Exception caught in thread: " + ex);
		} 
	} 
} 
