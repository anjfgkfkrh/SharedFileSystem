import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ExecutorService executorService;
    private Scanner scanner;
    private String address = "./Files/";
    private FileNode fileNode;

    public Server() {
        try {
            scanner = new Scanner(System.in);
            executorService = Executors.newFixedThreadPool(10); // 스레드 관리 클래스
            serverSocket = new ServerSocket(5555);
            int clientNum = 0;

            FileStructureSaver fileStructureSaver = new FileStructureSaver();
            fileNode = fileStructureSaver.createFileNode(new File(address));
            fileStructureSaver.saveToFile(fileNode, Paths.get("./FileStructure.json"));

            System.out.println();
            System.out.println("------------------------------");
            FilePrinter.print(fileNode);
            System.out.println("------------------------------");
            System.out.println();

            while (true) {
                System.out.println("접속 대기중.....");
                clientSocket = serverSocket.accept();
                System.out.println("서버 스레드 생성");
                executorService.execute((new ServerThread(clientSocket, clientNum, fileNode)));
                System.out.println("ClientNum" + clientNum + " 클라이언트 접속 완료");
                clientNum++;
            }
        } catch (IOException e) {
            System.out.println("서버 생성에 실패했습니다.");
            executorService.shutdown();
        }
    }
}

class ServerThread extends Thread {

    private int clientNum;
    private Socket socket;
    private File file;
    private FileNode fileNode;
    private String address = "./Files/";
    private InputStream input;
    private ObjectOutputStream oos;
    private DataInputStream dis;
    private DataOutputStream dos;
    private BufferedReader br;
    private FileStructureSaver fileStructureSaver;

    public ServerThread(Socket socket, int clientNum, FileNode fileNode) {
        this.clientNum = clientNum;
        this.socket = socket;
        this.fileNode = fileNode;
        fileStructureSaver = new FileStructureSaver();
        try {
            this.input = socket.getInputStream();
            this.oos = new ObjectOutputStream(socket.getOutputStream());
            this.dis = new DataInputStream(input);
            this.br = new BufferedReader(new InputStreamReader(input));
            this.dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
        }

    }

    @Override
    public void run() {
        sendFileStructure();
        int mode = 0;
        while (true) {

            // 버퍼 초기화
            clearbuffer();

            try {

                fileNode = fileStructureSaver.createFileNode(new File(address));
                fileStructureSaver.saveToFile(fileNode, Paths.get("./FileStructure.json"));

                // 모드 수신
                mode = dis.readInt();

                switch (mode) {
                    case 1:
                        System.out.println("ClientNum" + clientNum + " 파일 수신 모드");
                        fileInputMode();
                        break;
                    case 2:
                        System.out.println("ClientNum" + clientNum + " 파일 송신 모드");
                        fileOutputMode();
                        break;
                    case 3:
                        System.out.println("파일 삭제 모드");
                        fileDeleteMode();
                        break;
                    case 4:
                        folderCreateMode();
                        break;
                    case 5:
                        folderDeleteMode();
                        break;
                    case 6:
                        sendFileStructure();
                        break;
                    case 10:
                        System.out.println("ClientNum" + clientNum + " 정상 종료");
                        return;
                }
            } catch (IOException e) {
                System.out.println("ClientNum" + clientNum + " 비정상 종료");
                return;
            }
        }
    }

    private void fileInputMode() {
        // 1. 파일 이름 수신
        // 2. 전송받을 버퍼 크기 수신
        // 3. 버퍼 수신
        // 4. 전송받은 버퍼 크기가 일정 크기 이하일 때 까지 2,3 반복
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

    private void fileOutputMode() {
        // 1. 파일 이름 수신
        // 2. 전송할 버퍼 크기 전송
        // 3. 버퍼 전송
        // 4. 파일 끝까지 2,3 반복
        try {
            String filename;
            filename = br.readLine();
            System.out.println("ClientNum" + clientNum + " 파일 이름 수신 완료");
            System.out.println(filename);

            File file = new File(address + filename);
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;

            while ((bytesRead = fis.read(buffer)) != -1) {
                dos.writeInt(bytesRead);
                dos.write(buffer, 0, bytesRead);
            }
            dos.flush();
            System.out.println("파일 전송 완료");

        } catch (IOException e) {
            System.out.println(e);
            System.out.println("ClientNum" + clientNum + " 파일 송신 오류");
        }

    }

    private void fileDeleteMode() {
        // 1. 파일 이름 수신
        // 2. 파일 삭제
        String filename;

        try {
            filename = br.readLine();
            System.out.println("ClientNum" + clientNum + "파일 이름 수신 완료");

            File file = new File(address + filename);
            file.delete();
            System.out.println("ClientNum" + clientNum + "파일 삭제 완료");

        } catch (IOException e) {
            System.out.println(e);
            System.out.println("ClientNum" + clientNum + " 파일 삭제 오류");
        }
    }

    private void folderCreateMode() {
        // 1. 폴더 이름 수신
        // 2. 폴더 생성
        String dirName;

        try {
            dirName = br.readLine();
            System.out.println("ClientNum" + clientNum + "폴더 이름 수신 완료");

            File dir = new File(address + dirName);
            dir.mkdir();
            System.out.println("ClientNum" + clientNum + "폴더 생성 완료");

        } catch (IOException e) {
            System.out.println(e);
            System.out.println("ClientNum" + clientNum + "폴더 생성 오류");
        }
    }

    private void folderDeleteMode() {
        // 1. 폴더 이름 수신
        // 2.폴더 삭제
        String dirName;

        try {
            dirName = br.readLine();
            System.out.println("ClientNum" + clientNum + "폴더 이름 수신 완료");

            File dir = new File(address + dirName);
            deleteFolder(dir);

            System.out.println("ClientNum" + clientNum + "폴더 삭제 완료");

        } catch (IOException e) {
            System.out.println(e);
            System.out.println("ClientNum" + clientNum + "폴더 생성 오류");
        }
    }

    private void deleteFolder(File dir) {
        // 1. 폴더에서 파일 리스트 추출
        // 2. 폴더가 있을시 재귀 호출
        // 3. 파일 전부 제거
        // 4. 폴더 제거
        File[] files = dir.listFiles();

        // 폴더 안에 파일이 존재할 경우
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                // 폴더일 경우
                if (files[i].isDirectory())
                    deleteFolder(files[i]);
                files[i].delete();
            }
        }

        // 폴더 삭제
        dir.delete();

    }

    private void sendFileStructure() {
        try {
            dis.readInt();
            File file = new File("./FileStructure.json");
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;

            while ((bytesRead = fis.read(buffer)) != -1) {
                dos.writeInt(bytesRead);
                dos.write(buffer, 0, bytesRead);
            }
            dos.flush();
            System.out.println("파일 구조 전송 완료");

        } catch (IOException e) {
            System.out.println(e);
            System.out.println("ClientNum" + clientNum + " 파일 구조 송신 오류");
        }
    }

    private void clearbuffer() {
        try {
            if (input.available() > 0) { // socket input buffer 초기화
                byte[] trashbuffer = new byte[1024];
                while (input.available() > 0) {
                    int bytesRead = input.read(trashbuffer);
                }
            }
            System.out.println("ClientNum" + clientNum + " 남은 버퍼: " + input.available());
        } catch (IOException e) {
            System.out.println("ClientNum" + clientNum + "버퍼 정리 오류");
        }
    }

}