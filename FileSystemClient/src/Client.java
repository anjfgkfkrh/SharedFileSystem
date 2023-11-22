import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private FileStructure fileStruct;
    private Scanner scanner;
    private BufferedInputStream bis;
    private BufferedOutputStream bos;
    private FileInputStream fis;
    private FileOutputStream fos;
    private DataInputStream dis;
    private DataOutputStream dos;
    private BufferedReader br;
    private BufferedWriter bw;
    private ObjectInputStream ois;
    private File file;

    public Client() {
        try {
            // 서버 접속
            socket = new Socket("localhost", 5555);
            System.out.println("서버 접속 완료");

            // 스트림 생성
            scanner = new Scanner(System.in);
            dis = new DataInputStream(socket.getInputStream()); // 기본형 데이터 입출력
            dos = new DataOutputStream(socket.getOutputStream());
            bis = new BufferedInputStream(socket.getInputStream()); // 데이터 입출력
            bos = new BufferedOutputStream(socket.getOutputStream());
            br = new BufferedReader(new InputStreamReader(socket.getInputStream())); // 문자열 입출력
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            ois = new ObjectInputStream(socket.getInputStream());
            InputStream input = socket.getInputStream();

            // 파일 구조 수신
            fileStruct = (FileStructure) ois.readObject();
            System.out.println("파일 구조 수신 완료");
            fileStruct.printDir();

            // 모드 선택
            int mode = 0;
            while (true) {

                claerbuffer(input);

                System.out.println("모드를 입력하시오");
                mode = scanner.nextInt();

                switch (mode) {
                    case 1:

                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 10:
                        break;
                    default:
                        break;
                }

            }

        } catch (IOException e) {
            System.out.println("서버 연결 실패");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void claerbuffer(InputStream input) {
        try {
            if (input.available() > 0) { // socket input buffer 초기화
                byte[] trashbuffer = new byte[1024];
                while (input.available() > 0) {
                    int bytesRead = input.read(trashbuffer);
                }
            }
            System.out.println("남은 버퍼:" + input.available());
        } catch (IOException e) {
            System.out.println("버퍼 정리 오류");
        }
    }

    public void fileOutputMode() {

    }

}
