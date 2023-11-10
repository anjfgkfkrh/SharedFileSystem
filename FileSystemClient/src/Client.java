import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private FileStruct fileStruct;
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
    private boolean wait = false;

    public Client(){
        try{
            socket = new Socket("localhost", 5555);
            System.out.println("서버 접속 완료");
            scanner = new Scanner(System.in);
            dis = new DataInputStream(socket.getInputStream()); // 기본형 데이터 입출력
            dos = new DataOutputStream(socket.getOutputStream());
            bis = new BufferedInputStream(socket.getInputStream()); // 데이터 입출력
            bos = new BufferedOutputStream(socket.getOutputStream());
            br = new BufferedReader(new InputStreamReader(socket.getInputStream())); // 문자열 입출력
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            ois = new ObjectInputStream(socket.getInputStream());

            fileStruct = (FileStruct) ois.readObject();
            System.out.println("파일 구조 수신 완료");
            fileStruct.printDir();

            while (true){

                int mode = 3;

                dos.writeInt(mode);
                dos.flush();
                System.out.println("파일 모드 전송 완료");

                File file = new File("./Files/" + "다운로드.jfif");
                wait = dis.readBoolean();
                bw.write("다운로드.jfif" + "\n");
                bw.flush();
                System.out.println("파일 이름 전송 완료");
                wait = false;

//                long fileSize = file.length();
//
//                wait = dis.readBoolean();
//                dos.writeLong(fileSize);
//                System.out.println("파일 사이즈 전송 완료");
//                wait = false;
//
//                fis = new FileInputStream(file);
//                byte[] buffer = new byte[4096];
//                int read;
//                wait = dis.readBoolean();
//                while ((read = fis.read(buffer)) != -1) {
//                    dos.write(buffer, 0, read);
//                }
//                dos.flush();
//                System.out.println("파일 전송 완료");
//                wait = false;

                try {
                    fileStruct = (FileStruct) ois.readObject();
                    fileStruct.printDir();
                } catch (EOFException e) {
                    System.out.println("서버로부터 더 이상 데이터를 수신받지 못함");
                } catch (ClassNotFoundException e) {
                    System.out.println("클래스 정의를 찾을 수 없음");
                } catch (IOException e) {
                    System.out.println("네트워크 오류 발생");
                }

                dos.writeInt(10);
                System.out.println("종료 코드 전송 완료");
                break;

            }
        } catch (IOException e){
            System.out.println("파일 전송 실패");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
