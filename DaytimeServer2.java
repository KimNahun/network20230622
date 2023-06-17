import java.io.*;
import java.net.*;
import java.util.Date;   


public class DaytimeServer2 {
   public static void main(String[] args) {
      int serverPort = 13;    // default port
      if (args.length == 1 )
         serverPort = Integer.parseInt(args[0]);       
      try {
    
   	   ServerSocket myConnectionSocket = 
            new ServerSocket(serverPort); 
         System.out.println("Daytime server ready.");  
         while (true) {  
    
       System.out.println("Waiting for a connection.");
            MyStreamSocket myDataSocket = new MyStreamSocket
                (myConnectionSocket.accept( ));
         
/**/        System.out.println("A client has made connection.");
            Date timestamp = new Date ();
/**/        System.out.println("timestamp sent: "+ timestamp.toString());
          
            myDataSocket.sendMessage(timestamp.toString( ));
    
		   } 
       } 
       catch (Exception ex) {
          ex.printStackTrace( );
       } 
   } 
} 
