import java.net.*;
import java.io.*;

public class EchoClientHelper2 {
    static final String endMessage = ".";
    private MyStreamSocket mySocket;
    private InetAddress serverHost;
    private int serverPort;
    private Thread receiveThread;

    EchoClientHelper2(String hostName, String portNum) throws SocketException, UnknownHostException, IOException {
        this.serverHost = InetAddress.getByName(hostName);
        this.serverPort = Integer.parseInt(portNum);
        this.mySocket = new MyStreamSocket(this.serverHost, this.serverPort);

        System.out.println("Connection request made");

        // Create and start a new thread that listens for incoming messages
        this.receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String receivedMessage;
                    while ((receivedMessage = mySocket.receiveMessage()) != null && !receivedMessage.equals(endMessage)) {
                        System.out.println(receivedMessage);
                    }
                } catch (IOException e) {
                    System.err.println("Error in receive thread: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        receiveThread.start();
    }

    public void sendEcho( String message) throws SocketException, IOException {
        mySocket.sendMessage(message);
      
    }

    public void done() throws SocketException, IOException {
        mySocket.sendMessage(endMessage);
    }
}
