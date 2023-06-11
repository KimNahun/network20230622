import java.io.*;
import java.util.HashMap;

class ServerInfo {
    private String hostName;
    private int port;

    public ServerInfo(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    public String getHostName() {
        return this.hostName;
    }

    public int getPort() {
        return this.port;
    }
}

public class Register {
    private static HashMap<String, ServerInfo> serverMap = new HashMap<>();

    public static void main(String[] args) {
        int serverPort = 7;
        if (args.length == 1 )
            serverPort = Integer.parseInt(args[0]);
        try {
            MyServerDatagramSocket mySocket = new MyServerDatagramSocket(serverPort);
            System.out.println("Echo server ready.");
            while (true) {
                DatagramMessage request = mySocket.receiveMessageAndSender();
                String message = request.getMessage();

String[] arr = message.split(" ");

// Check if message has all the required parts

if (arr.length >= 3) {
    String serverName = arr[0].trim();  // trim here
    String hostName = arr[1].trim();    // and here
    String portString = arr[2].trim();  // and here
    

    try {
        int port = Integer.parseInt(portString);  // convert to int

        // Store server information
        serverMap.put(serverName, new ServerInfo(hostName, port));
	System.out.println(serverMap);
    } catch (NumberFormatException e) {
        System.out.println("Invalid port number: " + portString);
    }

}
                // send back the server info requested by the customer
System.out.println(message+"sdds");
                if (serverMap.containsKey(message.trim())) {
                    ServerInfo serverInfo = serverMap.get(message.trim());
                    String serverDetails = serverInfo.getHostName() + "\n" + serverInfo.getPort();
                    mySocket.sendMessage(request.getAddress(), request.getPort(), serverDetails);
                } else {
                    mySocket.sendMessage(request.getAddress(), request.getPort(), "Server not found");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
