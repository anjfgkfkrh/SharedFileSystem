import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
                System.out.println("ClientNum" + clientNum + " 클라이언트 접속 완료");
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
    private InputStream input;
    private ObjectOutputStream oos;
    private DataInputStream dis;
    private BufferedReader br;

    public ServerThread(Socket socket, int clientNum, FileStructure fileStruct) {
        this.clientNum = clientNum;
        this.socket = socket;
        this.fileStruct = fileStruct;
        try {
            this.input = socket.getInputStream();
            this.oos = new ObjectOutputStream(socket.getOutputStream());
            this.dis = new DataInputStream(socket.getInputStream());
            this.br = new BufferedReader(new InputStreamReader(input));
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
        int mode = 0;
        while (true) {

            // 버퍼 초기화
            clearbuffer();

            try {

                // 모드 수신
                mode = dis.readInt();

                switch (mode) {
                    case 1:
                        System.out.println("ClientNum" + clientNum + " 파일 송신 모드");
                        fileOutputMode();
                        break;
                    case 2:
                        System.out.println("ClientNum" + clientNum + " 파일 수신 모드");
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
                    case 10:
                        return;
                }
            } catch (IOException e) {
                System.out.println("ClientNum" + clientNum + " 비정상 종료");
                return;
            }
        }
    }

    public void fileInputMode() {
        try {
            String filename;
            filename = br.readLine();
            System.out.println("ClientNum" + clientNum + " 파일 이름 수신 완료");
            System.out.println(filename);

            File file = new File(address + filename);
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

            System.out.println("ClientNum" + clientNum + " 파일 수신 완료");

        } catch (IOException e) {
            System.out.println(e);
            System.out.println("ClientNum" + clientNum + " 파일 수신 오류");
        }

    }

    public void fileOutputMode() {

    }

    public void fileDeleteMode() {

    }

    public void folderCreateMode() {

    }

    public void folderDeleteMode() {

    }

    public void sendFileStructureObj(ObjectOutputStream oos, FileStructure filesStruct) {
        try {
            oos.writeObject(filesStruct);
            oos.reset();
        } catch (IOException e) {
            System.out.println(e);
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
            System.out.println("ClientNum" + clientNum + " 남은 버퍼: " + input.available());
        } catch (IOException e) {
            System.out.println("버퍼 정리 오류");
        }
    }

}