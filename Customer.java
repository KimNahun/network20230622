
import java.io.*;
import java.time.LocalTime;

public class Customer {
	static final String endMessage = ".";

	public static void main(String[] args) {
		InputStreamReader is = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(is);
		try {
			System.out.println("Welcome to the Echo client.\n" +
					"What is the name of the server host?");
			String initialHostName = br.readLine();
			if (initialHostName.length() == 0)
				initialHostName = "localhost";
			System.out.println("What is the port number of the server host?");
			String portNum = br.readLine();
			if (portNum.length() == 0)
				portNum = "7";
			EchoClientHelper1 helper = new EchoClientHelper1(initialHostName, portNum);
			boolean done = false;
			String echo;
		
while (!done) {
    System.out.println("Which direction do you want ?  (0: exit 1: request , 2: connect) ");
    int num = Integer.parseInt(br.readLine());
    if(num == 1) {
	 System.out.println("What server do you want?");

  String serverName = br.readLine();
                String serverInfo = helper.getEcho(serverName); // Ask Register for server information
                System.out.println("Server Info: " + serverInfo);
    }
    else if(num == 2) {
        System.out.println("What is the name of the server host?");
        String connectectionHostName = br.readLine();
        System.out.println("What is the port number of the server host?");
        String connectionPortNum = br.readLine();
        System.out.println("What is your name?");
        String myName = br.readLine();

        EchoClientHelper2 helper2 = new EchoClientHelper2(connectectionHostName, connectionPortNum);
        LocalTime now = LocalTime.now();
        String nowTime = ""+now.getHour()+":"+now.getMinute()+":"+now.getSecond();
	     helper2.sendEcho("/requestMode/"+";:;"+myName+";:;"+nowTime);
        while(true) {
            String message = br.readLine();
            now = LocalTime.now();
            nowTime = ""+now.getHour()+":"+now.getMinute()+":"+now.getSecond();
            helper2.sendEcho(message + ";:;" + myName+";:;"+nowTime); 
        }
    }
    else if(num == 0) {
        System.out.println("program exit.");
        break;
    }
    else {
        System.out.println("Wrong direction.");
    }
}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}