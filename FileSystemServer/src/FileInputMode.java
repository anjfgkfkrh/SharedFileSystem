import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class FileInputMode {
    private StreamManager sm;
    private FileOutputStream fos;
    private File file;
    private boolean wait = true;
    private int clientNum;
    private String address = "./Files/";

    public FileInputMode(StreamManager sm, int clientNum){
        this.sm = sm;
        this.clientNum = clientNum;
    }

    public void fileInput(){
        try { // 파일 수신
            boolean isFileCreate;
            sm.dos().writeBoolean(wait);
            String fileName = sm.br().readLine(); // 1.파일 이름 수신
            System.out.println("ClientNum" + clientNum + ": 파일 이름 수신 완료");
            file = new File(address + fileName); // 파일 생성
            if(!file.exists()){
                isFileCreate = file.createNewFile();
                if(isFileCreate)
                    System.out.println("ClientNum" + clientNum + ": 파일 생성 완료");
                else
                    System.out.println("ClientNum" + clientNum + ": 파일 생성 실패");
            }
            sm.dos().writeBoolean(wait);
            long fileSize = sm.dis().readLong(); // 2.파일 사이즈 수신
            System.out.println("ClientNum" + clientNum + ": 파일 사이즈 수신 완료");
            byte[] fileBuf = new byte[(int) fileSize]; // 파일 버퍼 생성

            sm.dos().writeBoolean(wait);
            sm.dis().readFully(fileBuf); // 3.fileBuf에 파일 수신
            System.out.println("ClientNum" + clientNum + ": 파일 수신 완료");

            fos = new FileOutputStream(address + fileName); // 저장할 파일 로컬 저장소에 생성
            fos.write(fileBuf); // 로컬 저장소에 파일 쓰기
            System.out.println("ClientNum" + clientNum + ": 파일 저장 완료");

        }catch (IOException e) {
            System.out.println("ClientNum" + clientNum + ": 파일 수신에 실패했습니다.");
        }
    }

    public void fileDelete(){
        try {
            boolean isFileDelete;
            sm.dos().writeBoolean(wait);
            String fileName = sm.br().readLine(); // 1.파일 이름 수신
            System.out.println("ClientNum" + clientNum + ": 파일 이름 수신 완료");
            file = new File(address + fileName); // 파일 생성
            isFileDelete = file.delete();
            if(isFileDelete)
                System.out.println("ClientNum" + clientNum + ": 파일 삭제 완료");
            else
                System.out.println("ClientNum" + clientNum + ": 파일 삭제 완료");
        }catch (IOException e){}
    }
}
