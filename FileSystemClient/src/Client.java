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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private FileStructure fileStruct;
    private Scanner scanner;
    private InputStream input;
    private OutputStream output;
    private BufferedInputStream bis;
    private BufferedOutputStream bos;
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
            input = socket.getInputStream();
            output = socket.getOutputStream();
            dis = new DataInputStream(input); // 기본형 데이터 입출력
            dos = new DataOutputStream(output);
            bis = new BufferedInputStream(input); // 데이터 입출력
            bos = new BufferedOutputStream(output);
            br = new BufferedReader(new InputStreamReader(input)); // 문자열 입출력
            bw = new BufferedWriter(new OutputStreamWriter(output));
            ois = new ObjectInputStream(input);

            // 파일 구조 수신
            fileStruct = (FileStructure) ois.readObject();
            System.out.println("파일 구조 수신 완료");
            fileStruct.printDir();

            // 모드 선택
            int mode = 0;
            while (true) {

                clearbuffer();

                System.out.println("모드를 입력하시오");
                mode = scanner.nextInt();
                scanner.nextLine();

                dos.writeInt(mode);
                System.out.println("모드 전송 완료");

                switch (mode) {
                    case 1:
                        System.out.println("파일 송신 모드");
                        fileOutputMode();
                        break;
                    case 2:
                        System.out.println("파일 수신 모드");
                        fileInputMode();
                        break;
                    case 3:
                        fileDeleteMode();
                        break;
                    case 4:
                        folderCreateMode();
                        break;
                    case 5:
                        folderDeleteMode();
                        break;
                    case 6:
                        fileStruct = (FileStructure) ois.readObject();
                        System.out.println("파일 구조 수신 완료");
                        fileStruct.printDir();
                        break;
                    case 10:
                        return;
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

    public void clearbuffer() {
        try {
            if (input.available() > 0) { // socket input buffer 초기화
                byte[] trashbuffer = new byte[1024];
                while (input.available() > 0) {
                    int bytesRead = input.read(trashbuffer);
                }
            }
            System.out.println("남은 버퍼: " + input.available());
        } catch (IOException e) {
            System.out.println("버퍼 정리 오류");
        }
    }

    public void fileOutputMode() {
        String filename;
        String path;
        System.out.println("전송할 파일 이름을 입력하시오");
        filename = scanner.nextLine();
        // System.out.println("저장할 경로를 입력하시오");
        // path = scanner.nextLine();

        try {

            // 파일 이름+경로 전송
            bw.write(filename + '\n');
            bw.flush();

            File file = new File("./Files/" + filename);
            FileInputStream fis = new FileInputStream(file);

            byte[] buffer = new byte[4096];
            int bytesRead = -1;

            while ((bytesRead = fis.read(buffer)) != -1) {
                dos.writeInt(bytesRead);
                dos.write(buffer, 0, bytesRead);
            }
            output.flush();
            System.out.println("파일 전송 완료");

        } catch (IOException e) {
            System.out.println(e);
            System.out.println("파일 전송 실패");
        }

    }

    public void fileInputMode() {
        String filename;
        System.out.println("전송 받을 파일 이름을 입력하시오");
        filename = scanner.nextLine();

        try {

            // 파일 이름 전송
            bw.write(filename + '\n');
            bw.flush();

            // 송신 받을 파일 생성
            File file = new File("./Files/" + filename);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;

            while (true) {
                bytesRead = dis.readInt();
                dis.readFully(buffer, 0, bytesRead);
                fos.write(buffer, 0, bytesRead);
                if (bytesRead < 4096)
                    break;
            }
            System.out.println("파일 수신 완료");
        } catch (IOException e) {
            System.out.println(e);
            System.out.println("파일 수신 실패");
        }

    }

    public void fileDeleteMode() {

    }

    public void folderCreateMode() {

    }

    public void folderDeleteMode() {

    }

}
