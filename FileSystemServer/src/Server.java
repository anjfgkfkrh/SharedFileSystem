import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ExecutorService executorService;
    private Scanner scanner;


    public Server(){
        try {
            scanner = new Scanner(System.in);
            executorService = Executors.newFixedThreadPool(10); // 스레드 관리 클래스
            serverSocket = new ServerSocket(5555);
            int clientNum = 0;
            while(true) {
                System.out.println("접속 대기중.....");
                clientSocket = serverSocket.accept();
                System.out.println("서버스레드 생성");
                executorService.execute((new ServerThread(clientSocket,clientNum)));
                System.out.println("ClientNum" + clientNum +": 클라이언트 접속 완료");
                clientNum++;
            }
        }catch (IOException e){
            System.out.println("서버생성에 실패했습니다.");
            executorService.shutdown();
        };

    }
}
class ServerThread extends Thread{

    private int clientNum;
    private Socket socket;
    private File file;
    private String address = "./Files/";
    private BufferedInputStream bis;
    private BufferedOutputStream bos;
    private FileInputStream fis;
    private FileOutputStream fos;
    private DataInputStream dis;
    private DataOutputStream dos;
    private BufferedReader br;
    private BufferedWriter bw;
    private boolean wait = true;

    public ServerThread(Socket socket,int clientNum){
        this.clientNum = clientNum;
        this.socket = socket;
        try {
            dis = new DataInputStream(socket.getInputStream()); // 기본형 데이터 입출력
            dos = new DataOutputStream(socket.getOutputStream());
            bis = new BufferedInputStream(socket.getInputStream()); // 데이터 입출력
            bos = new BufferedOutputStream(socket.getOutputStream());
            br = new BufferedReader(new InputStreamReader(socket.getInputStream())); // 문자열 입출력
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }catch (IOException e){
            System.out.println("Client"+clientNum+": 서버 스레드 생성에 실패하였습니다.");
        }
    }
    @Override
    public void run(){
        int mode;
        while(true){
            try {
                mode = dis.readInt();
            }catch(IOException e){
                mode = 3;
            }
            switch (mode){
                case 1:
                    fileInputMode();
                    break;
                case 2:
                    fileOutputMode();
                    break;
                case 3:
                    System.out.println("Client" + clientNum + ": 종료");
                    return;
                default:
                    System.out.println("Client" + clientNum + ": 잘못된 입력입니다.");
            }
        }
    }
    // 1.파일 이름 수신
    // 2.파일 사이즈 수신
    // 3.파일 수신
    // 4.로컬 저장소에 파일 쓰기
    public void fileInputMode() {
        try { // 파일 수신
            boolean isFileCreate;
            dos.writeBoolean(wait);
            String fileName = br.readLine(); // 1.파일 이름 수신
            System.out.println("ClientNum" + clientNum + ": 파일 이름 수신 완료");
            file = new File(address + fileName); // 파일 생성
            if(!file.exists()){
                isFileCreate = file.createNewFile();
                System.out.println("ClientNum" + clientNum + ": 파일 생성 완료");
            }
            dos.writeBoolean(wait);
            long fileSize = dis.readLong(); // 2.파일 사이즈 수신
            System.out.println("ClientNum" + clientNum + ": 파일 사이즈 수신 완료");
            byte[] fileBuf = new byte[(int) fileSize]; // 파일 버퍼 생성

            dos.writeBoolean(wait);
            dis.readFully(fileBuf); // 3.fileBuf에 파일 수신
            System.out.println("ClientNum" + clientNum + ": 파일 수신 완료");

            fos = new FileOutputStream(address + fileName); // 저장할 파일 로컬 저장소에 생성
            fos.write(fileBuf); // 로컬 저장소에 파일 쓰기
            System.out.println("ClientNum" + clientNum + ": 파일 저장 완료");

        }catch (IOException e) {
            System.out.println("ClientNum" + clientNum + ": 파일 생성에 실패했습니다.");
        }




    }
    public void fileOutputMode(){

    }
}
