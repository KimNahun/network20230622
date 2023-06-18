

import java.net.*;
import java.io.*;
class Check {
	boolean check;
	Check(boolean check){
		this.check=check;
	}
}
public class EchoClientHelper2 {
    static final String endMessage = ".";
    private MyStreamSocket mySocket;
    private InetAddress serverHost;
    private int serverPort;
    private Thread receiveThread;
    
    Check check;
    

    EchoClientHelper2(String hostName, String portNum,Check check) throws SocketException, UnknownHostException, IOException {
        this.serverHost = InetAddress.getByName(hostName);
        this.serverPort = Integer.parseInt(portNum);
        this.mySocket = new MyStreamSocket(this.serverHost, this.serverPort);
        this.check=check;



        this.receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String receivedMessage;
                    //스레드에서 보낸 메시지를 받음.
                    
                    while ((receivedMessage = mySocket.receiveMessage()) != null && !receivedMessage.equals(endMessage)) {
                    	//만약 duplicated로 메시지가 시작된다면,
                    	//닉네임이 중복되어 있다는 메시지를 출력함.
                    	if(receivedMessage.startsWith("/duplicated/")) {
                    		
                    		receivedMessage=receivedMessage.replaceAll("/duplicated/","");
                    		
                    		System.out.println(receivedMessage);
                    		
                    	}
                    	//아니라면, 해당 서버에 성공적으로 등록하였다는 메시지를 출력하고
                    	//닉네임이 중복되었는지 확인하는 check를 false 로 바꿔줌.
                    	else {
                    		check.check=false;
                    		System.out.println(receivedMessage);
                    		
                    		
                    	}
             
                        
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
        // does not receive the echo here anymore
    }

    public void done() throws SocketException, IOException {
        mySocket.sendMessage(endMessage);
    }
}
