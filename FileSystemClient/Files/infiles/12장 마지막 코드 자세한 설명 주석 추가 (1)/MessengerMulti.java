
// 클라이언트

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

//소스를 입력하고 Ctrl+Shift+O를 눌러서 필요한 파일을 포함한다. 
public class MessengerMulti {
	
	// 맴버 변수 선언, 이하 이 클래스 안의 모든 메소드 및 (내부) 클래스에서 공유해서 사용할 변수 생성 
	final static int ServerPort = 5000;   // 포트 번호
	protected JTextField textField;
	protected JTextArea textArea;
	DataInputStream is;
	DataOutputStream os;

	public MessengerMulti() throws IOException {     // 2. (생성자) MessengerMulti의 객체를 만들면 자동 실행 
		MyFrame f = new MyFrame();                   // 3. MyFrame 내부 클래스를 이용해서 GUI 만드는 부분
		InetAddress ip = InetAddress.getByName("localhost");  // 4. 도메인 네임 이용해서 IP 주소를 알아내는 부분
		System.out.println(ip);                      // 5. 반환받은 ip 주소 확인(테스트 용도)
		
		Socket s = new Socket(ip, ServerPort);       // 6. 소켓 생성
		
		is = new DataInputStream(s.getInputStream());   // 7. 입력 스트림 객체 생성
		os = new DataOutputStream(s.getOutputStream()); // 8. 출력 스트림 객체 생성

		Thread thread2 = new Thread(new Runnable() {    // 9. (스레스 생성) 클라이언트에서 GUI와 함께 같이 동시에 실행해야 할 코드 (run() 메소드 안에) 작성
			@Override                                    
			public void run() {
				while (true) {                          // 10. 서버로부터 메시지를 받아서 화면(JTextArea)에 출력하는 코드(무한 반복)
					
					try {
						String msg = is.readUTF();      // 11. 기다리고 있다가 서버에서 보낸 메시지를 읽어들여서
						textArea.append("RECIEVED: " + new String(msg) + "\n");   // 12. JtextArea에 출력
					} catch (Exception e) {
						e.printStackTrace();
						break;
					}
				}
			}
		});
		thread2.start();                // 13. 스레드 안의 프로그램(run() 메소드 안의 프로그램)도 바로 실행
	}

	
	
	// MyFrame이라는 내부(inner) 클래스 정의
	class MyFrame extends JFrame implements ActionListener {    // ActionListener 인터페이스를 '구현하는 클래스'를 (별도의 클래스로 만들어도 되지만) 
		                                                        // 그냥 편하게 이 클래스 자체를 ActionListener를 구현하는 클래스로 만들었음
		                                                        // 나중에 클릭했을 때(또는 엔터를 쳤을 때) actionPerformed() 함수를 호출하게 하고 싶은 위젯(JTextField)에 
		                                                        // (이 actionPerformed() 메소드가 들어있는) 그냥 이 클래스를 넣어주면 됩니다.  
	
		public MyFrame() {       // 추후 MyFrame 클래스의 객체를 만드는 순간 '이 생성자가 호출되면서 바로 GUI 생성'
			super("MessengerA"); // 상속 받은 상위클래스의 '문자열을 1개 받는 생성자' 호출, JFrame 클래스의 생성자 중 하나 => JFrame(String title)
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			textField = new JTextField(30);
			textField.addActionListener(this);  // JTextField에 메시지를 입력하고 엔터를 치면 => 자동으로 호출할 함수(actionPerformed())가 들어있는 객체 이름(참조변수) 등록

			textArea = new JTextArea(10, 30);
			textArea.setEditable(false);

			add(textField, BorderLayout.PAGE_END);
			add(textArea, BorderLayout.CENTER);
			pack();
			setVisible(true);
		}

		                                                  // (간단히) 서버로 메시지를 전송하는 기능 수행
		public void actionPerformed(ActionEvent evt) {    // - JTextArea에 사용자가 메시지를 입력하고 엔터를 치는 순간 '자동으로 호출되는 메소드'
			String s = textField.getText();               //   사용자가 JTextField에 입력한 데이터를 읽어들여서 
			                                              //   서버로 전송한 후, 본인 JTextArea에도 출력하는 등의 기능 수행
			try {
				os.writeUTF(s);                           // 서버로 전송
			} catch (IOException e) {
				e.printStackTrace();
			}
			textArea.append("SENT: " + s + "\n");         // 이후 본인(클라이언트) 창에도 출력
			textField.selectAll();                       // 텍스트 필드의 모든 텍스트를 선택하는 메소드, 다음 입력 때 기존 내용을 지우고 편하게 입력하도록 설정한 것
			textArea.setCaretPosition(textArea.getDocument().getLength());  //JtextArea라는 텍스트 영역의 커서 위치를 텍스트 영역의 맨 끝으로 설정
		}
		
	}
	

	
	public static void main(String[] args) throws IOException {   
		MessengerMulti m = new MessengerMulti();    // 1. MessengerMulti 객체를 생성하면 (자동으로 생성자를 실행하면서 
		                                            //    GUI 화면을 만들고(실행도 하고..), 스레드도 만들고 실행도 하고.., 
		                                            //    즉 GUI와 스레드, 이렇게 2개 프로그램이 동시에 자동으로 실행됨)
	}                                               //    main() 메소드는 다른 클래스를 만들어서 그 안에 넣어줘도 되고
	                                                //    (static 메소드(객체를 만들지 않도도 사용 가능)이므로) 그냥 여기 이렇게 넣어줘도 상관없음
}