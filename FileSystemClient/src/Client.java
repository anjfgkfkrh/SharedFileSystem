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

import com.fasterxml.jackson.databind.ObjectMapper;

public class Client {
    private Socket socket;
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
    private FileNode fileNode;

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

            clearbuffer();

            // 파일 구조 수신
            receiveFileSturcture();

            // 모드 선택
            // int mode = 0;
            // while (true) {

            // // clearbuffer();

            // // System.out.println("모드를 입력하시오");
            // // mode = scanner.nextInt();
            // // scanner.nextLine();

            // // dos.writeInt(mode);
            // // System.out.println("모드 전송 완료");

            // // switch (mode) {
            // // case 1:
            // // System.out.println("파일 송신 모드");
            // // fileOutputMode();
            // // break;
            // // case 2:
            // // System.out.println("파일 수신 모드");
            // // fileInputMode();
            // // break;
            // // case 3:
            // // System.out.println("파일 삭제 모드");
            // // fileDeleteMode();
            // // break;
            // // case 4:
            // // folderCreateMode();
            // // break;
            // // case 5:
            // // folderDeleteMode();
            // // break;
            // // case 6:
            // // receiveFileSturcture();
            // // System.out.println("파일 구조 수신 완료");
            // // System.out.println();
            // // System.out.println("------------------------------");
            // // FilePrinter.print(fileNode);
            // // System.out.println("------------------------------");
            // // System.out.println();
            // // break;
            // // case 10:
            // // return;
            // // default:
            // // break;
            // // }

            // }

        } catch (IOException e) {
            System.out.println("서버 연결 실패");
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

    public void fileOutputMode(File file, String path) {

        try {

            clearbuffer();

            // 모드 전송
            dos.writeInt(1);
            dos.flush();

            // 파일 이름+경로 전송
            bw.write(path + '/' + file.getName() + '\n');
            bw.flush();

            FileInputStream fis = new FileInputStream(file);

            byte[] buffer = new byte[4096];
            int bytesRead = -1;

            dis.readInt();

            while (true) {
                bytesRead = fis.read(buffer);
                if (bytesRead == -1) {
                    dos.writeInt(0);
                    break;
                } else {
                    dos.writeInt(bytesRead);
                    dos.write(buffer, 0, bytesRead);
                }
            }
            dos.flush();
            System.out.println("파일 전송 완료");

        } catch (IOException e) {
            System.out.println(e);
            System.out.println("파일 전송 실패");
        }

    }

    public void fileInputMode(String filename, String path) {

        try {

            clearbuffer();

            dos.writeInt(2);
            dos.flush();

            // 파일 이름 전송
            bw.write(path + '/' + filename + '\n');
            bw.flush();

            // 송신 받을 파일 생성
            File file = new File("./Files/" + filename);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;

            dos.writeInt(100);// 파일 수신 준비 완료 송신
            dos.flush();

            while (true) {
                bytesRead = dis.readInt();
                if (bytesRead == 0) {
                    break;
                }
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

    public void fileDeleteMode(String filename, String path) {
        try {
            // 모드 전송
            dos.writeInt(3);
            dos.flush();

            // 파일 이름+경로 전송
            bw.write(path + '/' + filename + '\n');
            bw.flush();

        } catch (IOException e) {
            System.out.println(e);
            System.out.println("파일 삭제 오류");
        }

    }

    public void folderCreateMode(File file, String path) {

        try {
            // 모드 전송
            dos.writeInt(4);
            dos.flush();

            // 파일 이름+경로 전송
            bw.write(path + '/' + file.getName() + '\n');
            bw.flush();

            System.out.println("폴더 정보 전송 완료");

        } catch (IOException e) {
            System.out.println(e);
            System.out.println("폴더 생성 오류");
        }
    }

    public void folderDeleteMode(File file, String path) {

        try {
            dos.writeInt(5);
            dos.flush();

            // 파일 이름+경로 전송
            bw.write(path + '/' + file.getName() + '\n');
            bw.flush();

            System.out.println("폴더 정보 전송 완료");

        } catch (IOException e) {
            System.out.println(e);
            System.out.println("폴더 삭제 오류");
        }
    }

    public void receiveFileSturcture() {
        // 1. 파일 구조를 저장한 json 파일 수신
        // 2. json파일을 FileNode 객체로 변환
        try {
            clearbuffer();

            // 모드 번호 전송
            dos.writeInt(6);

            // 준비 완료 전송
            dos.writeInt(1);

            // 송신 받을 파일 생성
            File file = new File("./FileStructure.json");
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;

            while (true) {
                bytesRead = dis.readInt();
                dis.readFully(buffer, 0, bytesRead);
                fos.write(buffer, 0, bytesRead);
                if (bytesRead < 4096) {
                    break;
                }
            }

            // json 파일 객체로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            fileNode = objectMapper.readValue(new File("./FileStructure.json"), FileNode.class);

            System.out.println("FileNode객체 변환 완료");

            System.out.println("파일 구조 수신 완료");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("파일 구조 수신 실패");
        }
    }

    public FileNode getFileNode() {
        return fileNode;
    }

    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                dos.writeInt(10);
                dos.flush();
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("소켓 종료 중 오류 발생");
        }
    }

}
