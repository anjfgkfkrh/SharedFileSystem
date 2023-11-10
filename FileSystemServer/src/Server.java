import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ExecutorService executorService;
    private Scanner scanner;
    private String address = "./Files/";
    private FileStruct filesStruct;

    public Server(){
        try {
            scanner = new Scanner(System.in);
            executorService = Executors.newFixedThreadPool(10); // 스레드 관리 클래스
            serverSocket = new ServerSocket(5555);
            int clientNum = 0;

            filesStruct = new FileStruct(address,"Files",0); // 서버 파일 구조 관리
            filesStruct.printDir(); // 서버 파일 구조 출력

            while(true) {
                System.out.println("접속 대기중.....");
                clientSocket = serverSocket.accept();
                System.out.println("서버 스레드 생성");
                executorService.execute((new ServerThread(clientSocket,clientNum,filesStruct)));
                System.out.println("ClientNum" + clientNum +": 클라이언트 접속 완료");
                clientNum++;
            }
        }catch (IOException e){
            System.out.println("서버 생성에 실패했습니다.");
            executorService.shutdown();
        };

    }
}
class ServerThread extends Thread{

    private int clientNum;
    private Socket socket;
    private FileStruct fileStruct;
    private File file;
    private String address = "./Files/";
    private boolean wait = true;
    private FileInputStream fis;
    private FileOutputStream fos;
    private StreamManager sm;
    private FileInputMode fim;

    public ServerThread(Socket socket,int clientNum,FileStruct fileStruct){
        this.clientNum = clientNum;
        this.socket = socket;
        this.fileStruct = fileStruct;
        sm = new StreamManager(socket);
        fim = new FileInputMode(sm,clientNum);
        try {
            sm.oos().writeObject(fileStruct);
            sm.oos().reset();
            sm.oos().flush();
            System.out.println("ClientNum" + clientNum + ": 파일 구조 송신 완료");
        }catch (IOException e){}
    }
    @Override
    public void run(){
        int mode;
        while(true){
            try {
                mode = sm.dis().readInt();
                switch (mode) {
                    case 1:
                        System.out.println("ClientNum" + clientNum + ": 파일 수신 모드");
                        fim.fileInput();
                        fileStruct.refresh();
//                        sm.oos().writeObject(fileStruct);
                        System.out.println("ClientNum" + clientNum + ": 파일 구조 재송신 완료");
                        break;
                    case 2:
                        System.out.println("ClientNum" + clientNum + ": 파일 송신 모드");
                        fileOutputMode();
                        System.out.println("ClientNum" + clientNum + ": 파일 구조 재송신 완료");
                        break;
                    case 3:
                        System.out.println("ClientNum" + clientNum + ": 파일 삭제 모드");
                        fim.fileDelete();
                        fileStruct.refresh();
//                        sm.oos().writeObject(fileStruct);
                        System.out.println("ClientNum" + clientNum + ": 파일 구조 재송신 완료");
                    case 10:
                        System.out.println("Client" + clientNum + ": 정상 종료");
                        return;
                    default:
                        System.out.println("Client" + clientNum + ": 잘못된 입력입니다.");
                }
            }catch(IOException e){
                System.out.println("Client" + clientNum + ": 비정상 종료");
                return;
            }
        }
    }
    public void fileOutputMode(){

    }
}
