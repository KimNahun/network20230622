package pjt;

import java.io.*;
import java.net.ServerSocket;
import java.util.*;


public class MessageHelper {
	ArrayList<String[]> messageList;
	Map<String, MyStreamSocket> clientSockets;
	
	MessageHelper(ArrayList<String[]> messageList,Map<String,MyStreamSocket> clientSockets){
		this.messageList=messageList;
		this.clientSockets=clientSockets;
	}
	//모든 메시지를 보여주는 기능.
	public void showAllMessage(String userName) throws IOException {
		
			StringBuilder sb=new StringBuilder();
			sb.append("----------\n");
			sb.append("Show all Message\n");
			sb.append("\n");
			//메시지 리스트를 순회하며, 모든 메시지를 스트링 빌더에 담는다.
			for(int i=0;i<messageList.size();i++){
				sb.append(messageList.get(i)[1]+":  "+messageList.get(i)[0]+
						" "+messageList.get(i)[2]+"\n");
			}
			sb.append("\n");
			sb.append("----------");
			//요청한 사용자에게, 스트링 빌더의 내용을 보낸다.
			clientSockets.get(userName).sendMessage(sb.toString());
	
	
		}
	//모든 사용자를 보여주는 기능
		public void showAllCustomer(String userName) throws IOException{
			StringBuilder sb=new StringBuilder();
			sb.append("----------\n");
			sb.append("Show all Customer\n");
			//해시맵의 사이즈가 사용자의 수임.
			sb.append("현재 사용자는 "+clientSockets.size()+"명 있습니다.\n");
			sb.append("\n");
			int idx = 0;
			sb.append("[ ");
			//다른 모든 사용자들을 스트링빌더에 담음
			for(String name:clientSockets.keySet()) {
				sb.append(name);
				if(idx++!=clientSockets.size()-1)
					sb.append(",");
	
			}
			sb.append(" ]");
	
			sb.append("\n");
			sb.append("----------");
	
			//요청한 사용자에게,스트링빌더의 내용을 보냄
			clientSockets.get(userName).sendMessage(sb.toString());
	
		}
		//해당 메시지가 존재하는지 검색하는 기능
		public void searchMessage(String userName,String searchingWord)throws IOException {
			StringBuilder sb=new StringBuilder();
			sb.append("----------\n");
			sb.append("Searches for the presence of a specific word : \"" +searchingWord+"\"");
			sb.append("\n");
			//find의 초깃값은 false
			boolean find = false;
			//메시지 리스트를 순회하며 해당 메시지를 포함하는 문구가 있나 검색
			for(int i=0;i<messageList.size();i++) {
				if(messageList.get(i)[0].contains(searchingWord)) {
					sb.append(messageList.get(i)[1]+": "+messageList.get(i)[0]+" "+messageList.get(i)[2]+"\n");
					//하나라도 찾았다면, find 가 true가 됨.
					find = true;
				}
			}
			//하나도 찾지 못했다면, 해당 단어가 존재하지 않는다는 사실을 기술. (예외처리)
			if(!find)
				sb.append("The word \""+searchingWord+"\" doesn't exist");
	
			sb.append("\n");
			sb.append("----------");
	
			//요청한 사용자에게, 스트링 빌더의 내용을 보냄.
			clientSockets.get(userName).sendMessage(sb.toString());
	
		}
		//특정 사용자에게만 메시지를 보내는 기능
		public void sendPrivateMessage(String userName,String sendUser,String sendMessage,String sendTimeStamp) throws IOException{
			//if(!clientSockets.contains(sendUser))
			StringBuilder sb=new StringBuilder();
			boolean exist = true;
			//받는 사람의 이름을 담음
			sb.append("----------\n");
			sb.append("Send Private Message to ["+sendUser+"]");
			sb.append("\n");
	
			//만약 해당 사용자가 존재하지 않는다면, exist가 false가 됨.(예외처리)
			if(!clientSockets.containsKey(sendUser)) {
				exist = false;
			}
			//존재하지 않는다면, 해당 사용자가 존재하지 않음을 담음.
			if(!exist) {
				sb.append("The user ["+sendUser+"] doesn't exit");
			}
			//존재한다면, 해당 사용자에게 성공적으로 메시지를 보냈다는 내용을 담음
			else {
				sb.append("You send a message to ["+sendUser+"] successfully");
			}
	
			sb.append("\n");
			sb.append("----------");
	
			//요청한 사용자에게, 스트링 빌더의 내용을 보냄
			clientSockets.get(userName).sendMessage(sb.toString());
	
			//만약, 받는 해당 사용자가 존재한다면.
			if(clientSockets.containsKey(sendUser)) {
	
				//해당 사용자에게, 누가 이 메시지를 보냈는지와, 메시지의 내용, 시간을 보냄
				clientSockets.get(sendUser).sendMessage("User ["+userName+"] send you a private message :"
						+sendMessage+" "+sendTimeStamp+"\n");
	
			}
	
	
		}
		//내가 보냈던 메시지를 삭제하는 기능
		public void deleteMessage(String userName,String deleteWord) throws IOException{
	
			StringBuilder sb=new StringBuilder();
			sb.append("----------\n");
			// . 이 아니라면, 특정 메시지를 삭제하는 것임
			if(!deleteWord.equals("."))
				sb.append("delete Message ["+deleteWord+"]");
			// . 이라면, 내가 보낸 모든 메시지를 삭제하는 것임.
			else
				sb.append("delete All Message ");
			sb.append("\n");
	
			boolean find = false;
			int index=0;
			int count=0;
			//인덱스가 0일 때부터 탐색함.
			while(index!=messageList.size()) {
				//메시지들 중에서, 내가 보낸 메시지이면서, (해당 메시지가 <삭제할 문구> 와 정확히 일치하거나 or . 이라면) 해당 메시지를 삭제
				if(messageList.get(index)[1].equals(userName)&&(messageList.get(index)[0].equals(deleteWord)||deleteWord.equals("."))) {
					//삭제할 메시지를 스트링 빌더에 담음
					sb.append(messageList.get(index)[1]+": "+messageList.get(index)[0]+" "+messageList.get(index)[2]+"\n");
					//삭제함.
					messageList.remove(index);
					//하나라도 삭제했다면, find가 true
					find = true;
					//index는 이게 ArrayList이므로, 하나 삭제하면 뒤의 인덱스들이 하나씩 당겨져옴.
					//그래서 인덱스를 하나 줄여야 다음 인덱스를 성공적으로 탐색가능
					index--;
					//count는 내가 메시지를 몇개 삭제했는지 세어줌
					count++;
				}
	
				index++;
	
			}
			//메시지를 삭제하지 못했고, 특정 메시지를 삭제하려고 했다면, 해당 메시지가 존재하지 않는다고 함.
			if(!find&&!deleteWord.equals("."))
				sb.append("The word \""+deleteWord+"\" doesn't exist");
			//메시지를 삭제했고, 특정 메시지를 삭제하려고 했다면, 삭제 성공과, 몇개 삭제했는지 담음.
			else if(find&&!deleteWord.equals("."))
				sb.append("You delete \""+deleteWord+"\" successfully. total "+count+" removed");
			//메시지를 삭제하지 못했고, 모든 메시지를 삭제하려고 했다면, 나는 아무런 채팅도 치지 않은 것임. 
			else if(!find&&deleteWord.equals("."))
				sb.append("You didn't send any message here");
			//메시지를 삭제했고, 모든 메시지를 삭제하려고 했다면, 모든 메시지 삭제 성공과 함께 몇개 삭제했는지 담음.
			else
				sb.append("You removed all message successfully that you sent. total "+count+" removed");
	
			sb.append("\n");
			sb.append("----------");
	
	
			//요청한 사용자에게 보냄
			clientSockets.get(userName).sendMessage(sb.toString());
	
		}
	
	
	

	
	
}
