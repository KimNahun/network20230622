
import java.io.*;
import java.util.*;

class EchoServerThread implements Runnable {

	static ArrayList<String[]> messageList = new ArrayList<>();
	static ArrayList<String> nameOrder=new ArrayList<>();

	static ArrayList<String> wordList=new ArrayList<>();


	static final String endMessage = ".";
	static int count = 0;

	static Map<String, MyStreamSocket> clientSockets = new HashMap<>();
	BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
	MyStreamSocket myDataSocket;
	int mode;  // New variable to store the mode

	EchoServerThread(MyStreamSocket myDataSocket,int mode) {
		this.myDataSocket = myDataSocket;
		this.mode=mode;
	}

	public void run()  { 
		try {

			
			//만약 모드가 1이면 메시지를 주고받는 기능
			if(mode==1) {
				messageMode();
			}

			//만약 모드가 2이면 게임을 하는 기능
			else if(mode==2) {	
				gameMode();
			}





		}
		catch (IOException e) {
			e.printStackTrace();
		}


	}

	 public void gameMode() throws IOException {


		String message="";
		
		
		//계속해서 요청이 오는 사용자를 체크함.
		while(true) {
			
			message = myDataSocket.receiveMessage();
			
			String temp[] = message.split(";:;");
			String userMessage = temp[0].trim();
			String userName = temp[1];
			String sendTimestamp=temp[2];
			String sendHostName =temp[3];
			String sendPortNum = temp[4];
			
			if(userMessage.equals("/requestMode/")) {	
				//그 닉네임이 존재하지 않는 닉네임이라면
				if(!clientSockets.containsKey(userName)) {
					//채팅방에 입장했다고 출력.
					System.out.println(" ["+userName+"] enter the game server "+sendTimestamp);
					System.out.println("host name : " + sendHostName +" port number : " + sendPortNum);
					//해시맵에 저장
					clientSockets.put(userName, myDataSocket);
					//성공적으로 채팅방에 입장하였다고 클라이언트에게 보냄
					myDataSocket.sendMessage("You connected successfully");
					break;
				}
				//이미 존재하는 닉네임이라면
				else {
					//이미 존재하는 닉네임이라고 클라이언트에게 보냄
					myDataSocket.sendMessage("/duplicated/ The name " + userName+" already exist");
					
				}

			}


		}
		//이때는 아직 사용자가 1명만 들어온 것. 리턴함
		//즉, 사용자가 2명이 되어야 게임이 시작함.
		if(clientSockets.size()==1) return;
		
		
		
		
		//게임의 종류 선택, 1번은 가위바위보, 2번은 끝말잇기, 3번은 2048게임
		myDataSocket.sendMessage("choose the game number (1 : rockScissorsPaper, 2: wordGame, 3 : blockGame)");
		message=myDataSocket.receiveMessage();
		
		String temp [] =message.split(";:;");
		//게임 넘버를 받음.
		int gameNumber = Integer.parseInt(temp[0]);



		try {
			//게임 number에 따라서 각각 다른 게임을 진행
			GameHelper gameHelper = new GameHelper(nameOrder,wordList,clientSockets);
			switch(gameNumber) {
			case 1:
				gameHelper.rockScissorsPaper();
				break;
			case 2: 
				gameHelper.wordGame();
				break;
			case 3:
				gameHelper.blockGame();
				break;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	//메시지를 주고받는 기능
	public void messageMode(){
		boolean done = false;
		MessageHelper messageHelper = new MessageHelper(messageList,clientSockets);
		String message;
		try {
			while (!done) {
				//계속해서 메시지를 받음.
				message = myDataSocket.receiveMessage();
				//구분자 ;:; 를 기준으로 나누었음. 메시지에 띄어쓰기가 있을 수 있기 때문.
				
				String temp[] = message.split(";:;");
				//유저 메시지, 유저 이름, 보낸 시간. 호스트네임, 포트번호
				String userMessage = temp[0].trim();
				String userName = temp[1];
				String sendTimestamp=temp[2];
				String sendHostName = temp[3];
				String sendPortNum = temp[4];
				//사용자의 메시지가 'requestMode'면, 서버에 내 소켓을 등록
				//HashMap 으로 했고, key는 이름 ,value는 소켓
				if(userMessage.equals("/requestMode/")) {
					//그 닉네임이 존재하지 않는 닉네임이라면
					if(!clientSockets.containsKey(userName)) {
						//채팅방에 입장했다고 출력.
						System.out.println(" ["+userName+"] 이 채팅방에 입장하셨습니다. "+sendTimestamp);
						System.out.println("host name : " + sendHostName +" port number : " + sendPortNum);
						//해시맵에 저장
						clientSockets.put(userName, myDataSocket);
						//성공적으로 채팅방에 입장하였다고 클라이언트에게 보냄
						myDataSocket.sendMessage("성공적으로 채팅방에 입장하였습니다.");

					}
					//이미 존재하는 닉네임이라면
					else {
						//이미 존재하는 닉네임이라고 클라이언트에게 보냄
						myDataSocket.sendMessage("/duplicated/ 해당 이름 " + userName+" 는 이미 존재하는 이름입니다.");

					}

					continue;


				}
				//사용자가 "//1"을 입력하면 모든 메시지를 보여줌
				if (userMessage.equals("//1")) {
					messageHelper.showAllMessage(userName);
				}
				//사용자가 "//2"를 입력하면, 어떤 사용자가 있는지 보여줌
				else if (userMessage.equals("//2")) {
					messageHelper.showAllCustomer(userName);
				}
				//사용자가 "//3"을 입력하면, 해당 메시지를 검색함.   
				//입력 양식 :    //3 hello hi
				// --- > "hello hi" 가 보냈던 메시지에 있는지 검색
				else if(userMessage.startsWith("//3")) { 
					String tempArray [] = userMessage.split(" ");
					String searchMessage = "";
					for(int i=1;i<tempArray.length;i++) {
						searchMessage+=tempArray[i]+" ";
					}
					searchMessage=searchMessage.trim();

					messageHelper.searchMessage(userName,searchMessage);
				}
				//사용자가 "//4"를 입력하면, 특정 사용자에게만 메시지를 보냄
				//입력 양식 :     //4 Leon how are you ?
				// ---> Leon 유저에게 "how are you?" 메시지를 보냄
				else if(userMessage.startsWith("//4")) {

					String tempArray [] = userMessage.split(" ");
					String sendUser=tempArray[1];
					String sendMessage="";
					for(int i=2;i<tempArray.length;i++) {
						sendMessage+= tempArray[i]+" ";
					}
					sendMessage=sendMessage.trim();

					messageHelper.sendPrivateMessage(userName,sendUser,sendMessage,sendTimestamp); 

				}
				//사용자가 "//5"를 입력하면, 특정 메시지를 지움.
				//입력 양식 :    //5 abc     or    //5 .
				// ---> 해당 사용자가 입력했던 채팅 중에서 "abc" 와 정확히 일치하는 채팅을 지움
				// ---> .을 치면 해당 사용자가 입력한 모든 메시지를 지움

				else if(userMessage.startsWith("//5")) {

					String tempArray [] = userMessage.split(" ");
					String deleteWord = "";
					for(int i=1;i<tempArray.length;i++) {
						deleteWord+=tempArray[i]+" ";
					}
					deleteWord=deleteWord.trim();

					messageHelper.deleteMessage(userName,deleteWord);

				}

				//.을 치면, 대화방에서 나감.
				//나갔다고 출력해줌.
				else if (userMessage.equals(endMessage)) {
					clientSockets.remove(userName);
					System.out.println("["+userName+"] left this room");
				}
				//이외의 경우는 명령어가 아닌 것. 
				//해시맵에 등록된 다른 모든 사용자들에게, 메시지를 보냄( 나 자신은 제외)
				//그리고 내가 보낸 메시지를 리스트에 저장함. 어떤 메시지인지, 누가 보냈는지, 몇시에 보냈는지.
				else {
					messageList.add(new String[]{userMessage,userName,sendTimestamp});
					System.out.println(userName+": " + userMessage+" "+sendTimestamp);
					for (String otherUser : clientSockets.keySet()) {
						if (!otherUser.equals(userName)) {
							clientSockets.get(otherUser).sendMessage(userName + ": " + userMessage+" "+sendTimestamp);
						}
					}

				}
			}
		} catch (Exception ex) {
			System.out.println("Exception caught in thread: " + ex);
		}

	}

} 