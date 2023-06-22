

import java.io.IOException;
import java.util.*;

public class GameHelper {
	ArrayList<String> nameOrder;
	ArrayList<String> wordList;
	Map<String, MyStreamSocket> clientSockets;
	static final String endMessage = ".";
	static HashMap<String,Integer> userScore=new HashMap<>();


	GameHelper(ArrayList<String> nameOrder,ArrayList<String> wordList,Map<String,MyStreamSocket> clientSockets){
		this.nameOrder=nameOrder;
		this.wordList=wordList;
		this.clientSockets=clientSockets;
	}


	public void wordGame() throws IOException {
		boolean done=false;
		MyStreamSocket order[]=new MyStreamSocket[2];
		int idx=0;
		for(String name:clientSockets.keySet()) {
			order[idx++] = clientSockets.get(name);
		}

		String message = "";


		//		order[1].sendMessage("send any message to connect the server");
		//		message= order[1].receiveMessage();

		for(String customer:clientSockets.keySet()) {
			clientSockets.get(customer).sendMessage("game started.");
		}

		int mod = 0;
		while(!done) {

			try { //

				if(mod==0) {
					order[0].sendMessage("You first. start the game");
				}

				if(mod%2==0)
					message=order[0].receiveMessage();
				else
					message=order[1].receiveMessage();

				mod++;

				String temp[]= message.split(";:;");

				String userWord= temp[0].toLowerCase();
				String userName = temp[1];
				String time = temp[2];

				System.out.println(userName +": "+ userWord);

				if ((userWord.trim()).equals (endMessage)){
					System.out.println(userName+"leave this room");
					clientSockets.remove(userName);
					done = true;
				}

				else if(wordList.isEmpty()) {
					wordList.add(userWord);
					nameOrder.add(userName);
					for(String info:clientSockets.keySet()) {
						if(!info.equals(userName))
							clientSockets.get(info).sendMessage(userName+" : "+userWord);
					}
				}

				//게임 종료 조건. 이미 말한 단어를 말하거나, 1글자 단어거나, 전의 마지막 단어의 끝 글자가 아니라면
				else if(wordList.contains(userWord)||userWord.length()<2|| userWord.charAt(0) != wordList.get(wordList.size()-1).charAt(wordList.get(wordList.size()-1).length()-1)) {
					for(String info:clientSockets.keySet()) {
						if(!info.equals(userName))
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
					System.out.println("----------------");
					mod = 0;
					System.out.println("---new Game Start. -----");
					idx=0;
					for(String name:clientSockets.keySet()) {
						order[idx++] = clientSockets.get(name);
					}

				}
				else {
					nameOrder.add(userName);
					wordList.add(userWord);
					for(String info:clientSockets.keySet()) {
						if(!info.equals(userName))
							clientSockets.get(info).sendMessage(userName+" : "+userWord);
					}

				} 
				///
			} //

			catch (Exception ex) {
				System.out.println("Exception caught in thread: " + ex);
			}

		}


	}
	public void rockScissorsPaper() throws IOException{
		MyStreamSocket order[]=new MyStreamSocket[2];
		int idx=0;
		for(String name:clientSockets.keySet()) {
			order[idx++] = clientSockets.get(name);
		}

		String message = "";

		order[0].sendMessage("rock : 0 , scissors : 1, paper : 2");

		String message1 = order[0].receiveMessage();

		order[1].sendMessage("rock : 0 , scissors : 1, paper : 2");

		String message2 = order[1].receiveMessage();

		String temp1[]=message1.split(";:;");
		String temp2[]=message2.split(";:;");
		int num1=Integer.parseInt(temp1[0]);
		int num2=Integer.parseInt(temp2[0]);

		if(num1==num2) {
			order[0].sendMessage("draw");
			order[1].sendMessage("draw");
		}
		else if(num1==0&&num2==1||num1==1&&num2==2||num1==2&&num2==0) {
			order[0].sendMessage("You win");
			order[1].sendMessage("You lose");
		}
		else {
			order[0].sendMessage("You lose");
			order[1].sendMessage("You win");
		}




	}


	public void blockGame () throws IOException {
		MyStreamSocket order[]=new MyStreamSocket[2];
		int idx=0;
		String message = "";
		for(String name:clientSockets.keySet()) {
			order[idx++] = clientSockets.get(name);
		}

		order[0].sendMessage("w = up, s = down , a = left , d = right");

		order[1].sendMessage("w = up, s = down , a = left , d = right");
		int arr[] = {0,0,0,0,0,0,2,4,8,16,32,64};
		int map[][]=new int[10][10];

		for(int i=0;i<map.length;i++) {
			Random rand = new Random();
			for(int j=0;j<map.length;j++) {
				int num = arr[rand.nextInt(12)];
				map[i][j] = num;
			}
		}
		String name [] = new String[2];
		idx = 0;
		for(String now:clientSockets.keySet()) {
			name[idx++] = now;
			userScore.put(now,0);
		}

		int count = 10;

		while(count-->0) {
			StringBuilder sb=new StringBuilder();
			for(int i=0;i<map.length;i++) {
				for(int j=0;j<map.length;j++) {
					int length = Integer.toString(map[i][j]).length();
					if(length == 1) {
						sb.append(String.format("  %d", map[i][j]));
					}
					else if(length == 2) {
						sb.append(String.format(" %d", map[i][j]));
					}
					else {
						sb.append(String.format("%d", map[i][j]));
					}

				}
				sb.append("\n");
			}
			sb.append("\n");

			if(count%2==0) {
				order[0].sendMessage(sb.toString());
				order[0].sendMessage("It's your turn.");
				order[0].sendMessage("w = up, s = down , a = left , d = right");
				message = order[0].receiveMessage();
			}
			else {
				order[1].sendMessage(sb.toString());
				order[1].sendMessage("It's your turn.");
				order[1].sendMessage("w = up, s = down , a = left , d = right");
				message = order[1].receiveMessage();
			}

			String temp[] = message.split(";:;");
			String direction = temp[0];
			String userName = temp[1];
			direction = direction.toLowerCase();
			if(temp[0].equals("w")) {
				map=up(map,userName);
			}

			else if(temp[0].equals("s")) {
				map=down(map,userName);
			}

			else if(temp[0].equals("a")) {
				map=left(map,userName);
			}

			else if(temp[0].equals("d")) {
				map=right(map,userName);
			}
			sb=new StringBuilder();
			for(int i=0;i<map.length;i++) {
				for(int j=0;j<map.length;j++) {
					int length = Integer.toString(map[i][j]).length();
					if(length == 1) {
						sb.append(String.format("  %d", map[i][j]));
					}
					else if(length == 2) {
						sb.append(String.format(" %d", map[i][j]));
					}
					else {
						sb.append(String.format("%d", map[i][j]));
					}
				}
				sb.append("\n");
			}

			sb.append("\n");

			sb.append(name[0]+" "+userScore.get(name[0])+"\n");
			sb.append(name[1]+" "+userScore.get(name[1])+"\n");

			if(count%2==0) {
				order[0].sendMessage(sb.toString());

			}
			else {
				order[1].sendMessage(sb.toString());
			}

			System.out.println(sb.toString());







		}
		StringBuilder sb=new StringBuilder();
		sb.append("show total score\n");


		sb.append(name[0]+" "+userScore.get(name[0])+"\n");
		sb.append(name[1]+" "+userScore.get(name[1])+"\n");


		if(userScore.get(name[0])>userScore.get(name[1])) {
			sb.append(name[0]+" win\n");
		}
		else if(userScore.get(name[0])<userScore.get(name[1])) {
			sb.append(name[1]+" win\n");
		}
		else {
			sb.append("draw!");
		}
		order[0].sendMessage(sb.toString());
		order[1].sendMessage(sb.toString());



	}
	public static int [][] left(int map[][],String name) {


		int standard=0;
		int indexI=0;
		int indexJ=0;
		int mapping[][]=new int[map.length][map.length];
		for(int i=0;i<map.length;i++) {
			standard=0;
			for(int j=0;j<map.length;j++) {
				if(map[i][j]==0) {
					continue;
				}
				else if(standard==0) {
					standard=map[i][j];
					indexI=i;
					indexJ=j;
				}
				else if(map[i][j]==standard) {
					map[indexI][indexJ]*=2;
					map[i][j]=0;
					standard=0;
					if(!userScore.containsKey(name)) {
						userScore.put(name,map[indexI][indexJ]);
					}
					else {
						userScore.replace(name,userScore.get(name)+map[indexI][indexJ]);
					}

				}
				else {
					standard=map[i][j];
					indexI=i;
					indexJ=j;
				}
			}
		}	

		for(int i=0;i<mapping.length;i++) {
			int cnt=0;
			for(int j=0;j<mapping.length;j++) {
				if(map[i][j]!=0) {
					mapping[i][cnt++]=map[i][j];
				}
			}
		}


		return mapping;
	}
	public static int[][] right(int map[][],String name) {


		int standard=0;
		int indexI=0;
		int indexJ=0;
		int mapping[][]=new int[map.length][map.length];
		for(int i=0;i<map.length;i++) {
			standard=0;
			for(int j=map.length-1;j>=0;j--) {
				if(map[i][j]==0) {
					continue;
				}
				else if(standard==0) {
					standard=map[i][j];
					indexI=i;
					indexJ=j;
				}
				else if(map[i][j]==standard) {
					map[indexI][indexJ]*=2;
					map[i][j]=0;
					standard=0;
					if(!userScore.containsKey(name)) {
						userScore.put(name,map[indexI][indexJ]);
					}
					else {
						userScore.replace(name,userScore.get(name)+map[indexI][indexJ]);
					}
				}
				else {
					standard=map[i][j];
					indexI=i;
					indexJ=j;
				}
			}
		}
		for(int i=0;i<mapping.length;i++) {
			int cnt=map.length-1;
			for(int j=map.length-1;j>=0;j--) {
				if(map[i][j]!=0) {
					mapping[i][cnt--]=map[i][j];
				}
			}
		}


		return mapping;
	}

	public static int[][] up(int map[][],String name) {

		int standard=0;
		int indexI=0;
		int indexJ=0;
		int mapping[][]=new int[map.length][map.length];
		for(int i=0;i<map.length;i++) {
			standard=0;
			for(int j=0;j<map.length;j++) {
				if(map[j][i]==0) {
					continue;
				}
				else if(standard==0) {
					standard=map[j][i];
					indexI=i;
					indexJ=j;
				}
				else if(map[j][i]==standard) {
					map[indexJ][indexI]*=2;
					map[j][i]=0;
					standard=0;
					if(!userScore.containsKey(name)) {
						userScore.put(name,map[indexI][indexJ]);
					}
					else {
						userScore.replace(name,userScore.get(name)+map[indexI][indexJ]);
					}
				}
				else {
					standard=map[j][i];
					indexI=i;
					indexJ=j;
				}
			}
		}

		for(int i=0;i<mapping.length;i++) {
			int cnt=0;
			for(int j=0;j<mapping.length;j++) {
				if(map[j][i]!=0) {
					mapping[cnt++][i]=map[j][i];
				}
			}
		}

		return mapping;
	}
	public static int[][] down(int map[][],String name) {


		int standard=0;
		int indexI=0;
		int indexJ=0;
		int mapping[][]=new int[map.length][map.length];
		for(int i=0;i<map.length;i++) {
			standard=0;
			for(int j=map.length-1;j>=0;j--) {
				if(map[j][i]==0) {
					continue;
				}
				else if(standard==0) {
					standard=map[j][i];
					indexI=i;
					indexJ=j;
				}
				else if(map[j][i]==standard) {
					map[indexJ][indexI]*=2;
					map[j][i]=0;
					standard=0;
					if(!userScore.containsKey(name)) {
						userScore.put(name,map[indexI][indexJ]);
					}
					else {
						userScore.replace(name,userScore.get(name)+map[indexI][indexJ]);
					}
				}
				else {
					standard=map[j][i];
					indexI=i;
					indexJ=j;
				}
			}
		}
		for(int i=0;i<mapping.length;i++) {
			int cnt=map.length-1;
			for(int j=map.length-1;j>=0;j--) {
				if(map[j][i]!=0) {
					mapping[cnt--][i]=map[j][i];
				}
			}
		}


		return mapping;
	}






}
