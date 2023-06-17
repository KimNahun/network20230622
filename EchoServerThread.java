
import java.io.*;
import java.util.*;



class EchoServerThread implements Runnable {

	static ArrayList<String[]> messageList = new ArrayList<>();
	static ArrayList<String> nameOrder=new ArrayList<>();

	static ArrayList<String> wordList=new ArrayList<>();
	static final String endMessage = ".";


	private static Map<String, MyStreamSocket> clientSockets = new HashMap<>();
	BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
	MyStreamSocket myDataSocket;
	int mode;  // New variable to store the mode


	EchoServerThread(MyStreamSocket myDataSocket,int mode) {
		this.myDataSocket = myDataSocket;
		this.mode=mode;
	}

	public void run()  { 

		if(mode==1) {
			messageMode();
		}
		else if(mode==2) {
			gameMode();
		}






	}

	public void gameMode() {
		boolean done = false;
		String message;

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

	public void messageMode(){
		boolean done = false;
		String message;
		try {
			while (!done) {
				message = myDataSocket.receiveMessage();
				String temp[] = message.split(";:;");
				String userMessage = temp[0].trim();
				String userName = temp[1];
				String sendTimestamp=temp[2];

				clientSockets.put(userName, myDataSocket);
				if(userMessage.equals("/requestMode/")) {

					System.out.println(" ["+userName+"] 이 채팅방에 입장하셨습니다. "+sendTimestamp);
					continue;
				}

				if (userMessage.equals("//1")) {
					showAllMessage(userName);
				}
				else if (userMessage.equals("//2")) {
					showAllCustomer(userName);
				}
				else if(userMessage.startsWith("//3")) { //이거 검색기능 띄어쓰기 제공 안함.
					String tempArray [] = userMessage.split(" ");
					String searchMessage = "";
					for(int i=1;i<tempArray.length;i++) {
						searchMessage+=tempArray[i]+" ";
					}
					searchMessage=searchMessage.trim();

					searchMessage(userName,searchMessage);
				}
				else if(userMessage.startsWith("//4")) {

					String tempArray [] = userMessage.split(" ");
					String sendUser=tempArray[1];
					String sendMessage="";
					for(int i=2;i<tempArray.length;i++) {
						sendMessage+= tempArray[i]+" ";
					}
					sendMessage=sendMessage.trim();


					sendPrivateMessage(userName,sendUser,sendMessage,sendTimestamp);


				}
				else if(userMessage.startsWith("//5")) {

					String tempArray [] = userMessage.split(" ");
					String deleteWord = "";
					for(int i=1;i<tempArray.length;i++) {
						deleteWord+=tempArray[i]+" ";
					}
					deleteWord=deleteWord.trim();

					deleteMessage(userName,deleteWord);

				}

				else if (userMessage.equals(endMessage)) {
					clientSockets.remove(userName);
					System.out.println("["+userName+"] left this room");
				}
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
	public void showAllMessage(String userName) throws IOException {
		StringBuilder sb=new StringBuilder();
		sb.append("----------\n");
		sb.append("Show all Message\n");
		sb.append("\n");
		for(int i=0;i<messageList.size();i++){
			sb.append(messageList.get(i)[1]+":  "+messageList.get(i)[0]+
					" "+messageList.get(i)[2]+"\n");
		}
		sb.append("\n");
		sb.append("----------");

		clientSockets.get(userName).sendMessage(sb.toString());


	}
	public void showAllCustomer(String userName) throws IOException{
		StringBuilder sb=new StringBuilder();
		sb.append("----------\n");
		sb.append("Show all Customer\n");
		sb.append("현재 사용자는 "+clientSockets.size()+"명 있습니다.\n");
		sb.append("\n");
		int idx = 0;
		sb.append("[ ");
		for(String name:clientSockets.keySet()) {
			sb.append(name);
			if(idx++!=clientSockets.size()-1)
				sb.append(",");

		}
		sb.append(" ]");

		sb.append("\n");
		sb.append("----------");

		clientSockets.get(userName).sendMessage(sb.toString());


	}
	public void searchMessage(String userName,String searchingWord)throws IOException {
		StringBuilder sb=new StringBuilder();
		sb.append("----------\n");
		sb.append("Searches for the presence of a specific word : \"" +searchingWord+"\"");
		sb.append("\n");
		boolean find = false;
		for(int i=0;i<messageList.size();i++) {
			if(messageList.get(i)[0].contains(searchingWord)) {
				sb.append(messageList.get(i)[1]+": "+messageList.get(i)[0]+" "+messageList.get(i)[2]+"\n");
				find = true;
			}
		}
		if(!find)
			sb.append("The word \""+searchingWord+"\" doesn't exist");

		sb.append("\n");
		sb.append("----------");



		clientSockets.get(userName).sendMessage(sb.toString());


	}
	public void sendPrivateMessage(String userName,String sendUser,String sendMessage,String sendTimeStamp) throws IOException{
		//if(!clientSockets.contains(sendUser))
		StringBuilder sb=new StringBuilder();
		boolean exist = true;
		sb.append("----------\n");
		sb.append("Send Private Message to ["+sendUser+"]");
		sb.append("\n");

		if(!clientSockets.containsKey(sendUser)) {
			exist = false;
		}
		if(!exist) {
			sb.append("The user ["+sendUser+"] doesn't exit");
		}
		else {
			sb.append("You send a message to ["+sendUser+"] successfully");
		}

		sb.append("\n");
		sb.append("----------");

		clientSockets.get(userName).sendMessage(sb.toString());

		if(clientSockets.containsKey(sendUser)) {

			clientSockets.get(sendUser).sendMessage("User ["+userName+"] send you a private message :"
					+sendMessage+" "+sendTimeStamp+"\n");

		}


	}
	public void deleteMessage(String userName,String deleteWord) throws IOException{

		StringBuilder sb=new StringBuilder();
		sb.append("----------\n");
		if(!deleteWord.equals("."))
			sb.append("delete Message ["+deleteWord+"]");
		else
			sb.append("delete All Message ");
		sb.append("\n");

		boolean find = false;
		int index=0;
		int count=0;
		while(index!=messageList.size()) {

			if(messageList.get(index)[1].equals(userName)&&(messageList.get(index)[0].equals(deleteWord)||deleteWord.equals("."))) {
				sb.append(messageList.get(index)[1]+": "+messageList.get(index)[0]+" "+messageList.get(index)[2]+"\n");
				messageList.remove(index);
				find = true;
				index--;
				count++;
			}

			index++;


		}

		if(!find&&!deleteWord.equals("."))
			sb.append("The word \""+deleteWord+"\" doesn't exist");
		else if(find&&!deleteWord.equals("."))
			sb.append("You delete \""+deleteWord+"\" successfully. total "+count+" removed");
		else if(!find&&deleteWord.equals("."))
			sb.append("You didn't send any message here");
		else
			sb.append("You removed all message successfully that you sent. total "+count+" removed");

		sb.append("\n");
		sb.append("----------");


		clientSockets.get(userName).sendMessage(sb.toString());

	}
} 