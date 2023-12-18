
// 클래스가 2개 들어있는 소스 파일, 서버를 만들고 실행하는 클래스(ServerM)와 스래드만 처리하는 클래스(ServerThread)를 따로 만듦
// 다중 클라이언트 구현

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


public class ServerM {  // 서버를 만들고 실행하는 클래스

	static ArrayList<ServerThread> list = new ArrayList<>();    // 3주차 컬렉션 참고, static으로 선언(클래스 이름으로 접근 가능(

	static int clientCount = 0;            // 접속한 클라이언트 유저 번호 저장, 0번 사용자부터 시작

	public static void main(String[] args) throws IOException {
		ServerSocket ssocket = new ServerSocket(5000);     // 1. 서버 소켓 생성

		Socket s;                   

		while (true) {    // 2. 무한 반복, 반복문 안에서 소켓 객체를 생성하고 있으므로 다중 클라이언트 가능
			
			s = ssocket.accept();   // 3. 클라이언트로부터의 연결 요청을 기다림, 특정 클라이언트와 연결되면 해당 클라이언트와 통신하기 위한 소켓 객체 생성후 s로 참조 

			DataInputStream is = new DataInputStream(s.getInputStream());    // 4. 입력 스트림 객체 생성
			DataOutputStream os = new DataOutputStream(s.getOutputStream()); // 5. 출력 스트림 객체 생성

			ServerThread thread = new ServerThread(s, "client " + clientCount, is, os);    // 6. (서버 안에서 동시에 실행될) 스레드 객체를 생성하면서 
			                                                                               //    (위에서 만들어진) '해당 사용자와 통신할 수 있는 소켓 객체'와 
			                                                                               //    클라이언트 번호, 입출력 스트림 객체 모두 전달
			                                                                               //   - 사용자 별로 각각 스레스 객체 생성 
			list.add(thread);  // 7. (추후 모든 사용자에게 메시지를 전달하기 위해) 
			                   //     생성된 스레드 객체를 ArrayList에 저장(해당 스레드 객체에 접근할 수 있는 참조값(주소) 저장)
			thread.start();    // 8. 생성된 스레드 실행
			clientCount++;     // 9. 클라이언트 유저 번호 증가

		}
	}
}



class ServerThread extends Thread {
	Scanner scn = new Scanner(System.in);
	private String name;
	final DataInputStream is;
	final DataOutputStream os;
	Socket s;
	boolean active;

	public ServerThread(Socket s, String name, DataInputStream is, DataOutputStream os) {
		this.is = is;
		this.os = os;
		this.name = name;
		this.s = s;
		this.active = true;
	}

	@Override
	public void run() {
		String message;
		while (true) {
			try {
				message = is.readUTF();        // 어떤 클라이언트로 부터 들어오는 데이터를 읽어들여서 
				System.out.println(message);   // (일단 서버의 콘솔장에 출력해서 확인하고) 
				for (ServerThread t : ServerM.list) {        // ArrayList에 등록되어 있는 모든 사용자에게 순서대로 그 메시지 전달
					t.os.writeUTF(this.name + " : " + message);   // t 사용자와 통신하는 스레드 안의 os.writeUTF()를 호출하여 메시지 전달
				}
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
		try {
			this.is.close();
			this.os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
