

import java.io.IOException;
import java.util.*;

public class GameHelper {
	ArrayList<String> nameOrder;
	ArrayList<String> wordList;
	Map<String, MyStreamSocket> clientSockets;
	MyStreamSocket myDataSocket;
	static final String endMessage = ".";


	GameHelper(ArrayList<String> nameOrder,ArrayList<String> wordList,Map<String,MyStreamSocket> clientSockets,
			MyStreamSocket myDataSocket){
		this.nameOrder=nameOrder;
		this.wordList=wordList;
		this.clientSockets=clientSockets;
		this.myDataSocket=myDataSocket;
	}

	public int checkAllGameSame() throws IOException {

		int now = -1;
		String message;
		boolean done = false;
		int count  =0;
		int gameNumber = -1;
		while(clientSockets.size()!=2) {

			HashSet<Integer> set=new HashSet<>();
			message = myDataSocket.receiveMessage();
			
			String temp[] = message.split(";:;");
			
			String userMessage = temp[0].trim();
			String userName = temp[1];
			String sendTimestamp=temp[2];

			if(userMessage.equals("/requestMode/")) {
				clientSockets.put(userName, myDataSocket);
				System.out.println(" ["+userName+"] 이 게임 서버에 입장하셨습니다. "+sendTimestamp);
				continue;
			}
			
			
		}
		
		while(!done) {
			HashSet<Integer> set=new HashSet<>();
			
			for(String name:clientSockets.keySet()) {
				clientSockets.get(name).sendMessage("What game do you want?");
				message=myDataSocket.receiveMessage();
				String temp [] =message.split(";:;");
				gameNumber = Integer.parseInt(temp[0]);
				set.add(gameNumber);
				
			}
			if(set.size()!=1) {
				for(String name:clientSockets.keySet()) {
					clientSockets.get(name).sendMessage("유저들의 게임 번호가 일치하지 않습니다.");	
				}
			}
			else if(!(gameNumber>=1&&gameNumber<=3)) {
				for(String name:clientSockets.keySet()) {
					clientSockets.get(name).sendMessage("유저가 제시된 게임 번호 외의 다른 게임 번호를 입력했습니다.");	
				}
			}
			else {
				done = true;
			}
			
		}

		return gameNumber;

	}

	public void wordGame() throws IOException {
		boolean done=false;
		String message;
		
		for(String customer:clientSockets.keySet()) {
			clientSockets.get(customer).sendMessage("게임이 시작되었습니다. 원하는 사람의 선공으로 시작하세요");
		}
		
		try {
			while (!done) {
				message = myDataSocket.receiveMessage();
				String temp[]= message.split(";:;");
				//대문자 소문자 구별
				//여기서 스토리 텔링. camel case 써서 했는데 계속 리퀘스트 모드해결안됨
				String userWord= temp[0].toLowerCase();
				String userName = temp[1];
				String time = temp[2];
				clientSockets.put(userName, myDataSocket);

				if(userWord.equals("/requestmode/"))
					continue;
				System.out.println(userName +": "+ userWord);

				if ((userWord.trim()).equals (endMessage)){
					System.out.println("Session over.");
					done = true;
				}

				if(wordList.isEmpty()) {
					wordList.add(userWord);
					nameOrder.add(userName);
				}

				//게임 종료 조건. 이미 말한 단어를 말하거나, 1글자 단어거나, 전의 마지막 단어의 끝 글자가 아니라면
				else if(wordList.contains(userWord)||userWord.length()<2|| userWord.charAt(0) != wordList.get(wordList.size()-1).charAt(wordList.get(wordList.size()-1).length()-1)) {
					for(String info:clientSockets.keySet()) {
						if(info!=userName)
							clientSockets.get(info).sendMessage(userName+" : "+userWord);
					}

					for (String otherUser : clientSockets.keySet()) {
						if (!otherUser.equals(userName)) {
							clientSockets.get(otherUser).sendMessage(userName+" lose");
						}
					}
					clientSockets.get(userName).sendMessage("You lose");
					//					
					wordList.clear();
					nameOrder.clear();
					System.out.println(userName +" lose ");

					System.out.println("---new Game Start. -----");
				}
				else {
					nameOrder.add(userName);
					wordList.add(userWord);
					for(String info:clientSockets.keySet()) {
						if(!info.equals(userName))
							clientSockets.get(info).sendMessage(userName+" : "+userWord);
					}

				} 
			}
		}

		catch (Exception ex) {
			System.out.println("Exception caught in thread: " + ex);
		}



	}







}
