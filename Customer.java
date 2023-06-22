
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
				//어떤 서버를 요구하는지 입력받음.
				//해당 서버가 존재하는지 확인
				if(num == 1) {
					System.out.println("What server do you want?");
					String serverName = br.readLine();
					String serverInfo = helper.getEcho(serverName); // 
					System.out.println("Server Info: " + serverInfo);
				}
				//해당 서버가 존재하는지 확인하였으면,
				//이제 해당 서버로 연결함.
				else if(num == 2) {
					System.out.println("What is the name of the server host?");
					String connectectionHostName = br.readLine();
					System.out.println("What is the port number of the server host?");
					String connectionPortNum = br.readLine();

		
					String myName="";

					LocalTime now = LocalTime.now();
					String nowTime = ""+now.getHour()+":"+now.getMinute()+":"+now.getSecond();
					Check check=new Check(true);
					
					EchoClientHelper2 helper2 = new EchoClientHelper2(connectectionHostName, connectionPortNum,check);
					//닉네임이 중복되어있나 체크.
					while(check.check) {
						//이름을 물어보고
						System.out.println("What is your name?");
						myName = br.readLine();
						nowTime = ""+now.getHour()+":"+now.getMinute()+":"+now.getSecond();
						//helper에 닉네임, 현재 시간, host name, 포트 넘버를 보냄
					    helper2.sendEcho("/requestMode/"+";:;"+myName+";:;"+nowTime+";:;"+initialHostName+";:;"+args[0]);
					    //1초 지연. 이게 없으면 check.check가 false 로 바뀌기 전에 What is your name을 다시 물어봄.
					    try {
					        Thread.sleep(1000);  
					    } catch (InterruptedException e) {
					        e.printStackTrace();
					    }
					    
					}
					
					//여기서부터는 서버에 성공적으로 접속한 것.
					
					while(true) {
						//보낼 메시지를 입력받음.
						String message = br.readLine();
						now = LocalTime.now();
						nowTime = ""+now.getHour()+":"+now.getMinute()+":"+now.getSecond();
						//helper2에 메시지, 이름, 현재시각, hostname, 포트넘버를 보냄
						helper2.sendEcho(message + ";:;" + myName+";:;"+nowTime+";:;"+initialHostName+";:;"+portNum);
						//메시지가 .이라면 채팅방을 나감.
						if(message.trim().equals(".")) {
							System.out.println("you leave the chatting");
							break;
						}
					}

				}
				//num이 0이면 프로그램 종료
				else if(num == 0) {
					System.out.println("program exit.");
					break;
				}
				//제시된 숫자 이외의 숫자가 입력되었을 때
				else {
					System.out.println("Wrong direction.");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}