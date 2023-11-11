import java.io.*;
import java.net.Socket;

public class StreamManager {
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private BufferedInputStream bufferedInputStream;
    private BufferedOutputStream bufferedOutputStream;
    private ObjectOutputStream objectOutputStream;

    public StreamManager(Socket socket){
        try {
            dataInputStream = new DataInputStream(socket.getInputStream()); // 기본형 데이터 입출력
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            bufferedInputStream = new BufferedInputStream(socket.getInputStream()); // 데이터 입출력
            bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // 문자열 입출력
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        }catch (IOException e){};
    }

    public void close() {
        try {
            dataOutputStream.close();
            dataInputStream.close();
            bufferedWriter.close();
            bufferedReader.close();
            bufferedOutputStream.close();
            bufferedInputStream.close();
        }catch (IOException e){}
    }

    public DataInputStream dis(){
        return dataInputStream;
    }
    public DataOutputStream dos(){
        return dataOutputStream;
    }
    public BufferedInputStream bis(){
        return bufferedInputStream;
    }
    public BufferedOutputStream bos() {
        return bufferedOutputStream;
    }
    public BufferedReader br() {
        return bufferedReader;
    }
    public BufferedWriter bw() {
        return bufferedWriter;
    }
    public ObjectOutputStream oos(){
        return objectOutputStream;
    }

    public void ObjectOutput(Object obj) throws IOException {
        objectOutputStream.writeObject(obj);
        objectOutputStream.reset();
    }
}
