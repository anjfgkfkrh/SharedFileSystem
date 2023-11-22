import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
    private FileStructure filesStruct;

    public Server() {
        try {
            scanner = new Scanner(System.in);
            executorService = Executors.newFixedThreadPool(10); // 스레드 관리 클래스
            serverSocket = new ServerSocket(5555);
            int clientNum = 0;

            filesStruct = new FileStructure(address, "Files", 0);
            filesStruct.printDir(); // 서버 파일 구조 출력

            while (true) {
                System.out.println("접속 대기중.....");
                clientSocket = serverSocket.accept();
                System.out.println("서버 스레드 생성");
                executorService.execute((new ServerThread(clientSocket, clientNum, filesStruct)));
                System.out.println("ClientNum" + clientNum + ": 클라이언트 접속 완료");
                clientNum++;
            }
        } catch (IOException e) {
            System.out.println("서버 생성에 실패했습니다.");
            executorService.shutdown();
        }
        ;

    }
}

class ServerThread extends Thread {

    private int clientNum;
    private Socket socket;
    private FileStructure fileStruct;
    private File file;
    private String address = "./Files/";
    private FileInputStream fis;
    private FileOutputStream fos;
    private ObjectOutputStream oos;

    public static void sendFileStructureObj(ObjectOutputStream oos, FileStructure filesStruct) {
        try {
            oos.writeObject(filesStruct);
            oos.reset();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public ServerThread(Socket socket, int clientNum, FileStructure fileStruct) {
        this.clientNum = clientNum;
        this.socket = socket;
        this.fileStruct = fileStruct;
        try {
            this.oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
        }

        fileStruct.refresh();
        sendFileStructureObj(oos, fileStruct);
        file = new File(address + "myfile.txt");
        file.delete();
        fileStruct.refresh();
        fileStruct.printDir();
        sendFileStructureObj(oos, fileStruct);
    }

    @Override
    public void run() {
        // int mode = 0;
        // while (true) {
        // try {
        // switch (mode) {
        // case 1:
        // break;
        // case 2:
        // break;
        // case 3:
        // break;
        // case 4:
        // break;
        // case 10:
        // return;
        // default:
        // System.out.println("ClientNum" + clientNum + ": 잘못된 입력입니다.");
        // }
        // } catch (Exception e) {
        // System.out.println("ClientNum" + clientNum + ": 비정상 종료");
        // return;
        // }
        // }
    }
}