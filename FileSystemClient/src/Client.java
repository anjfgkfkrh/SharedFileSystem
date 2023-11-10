import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private Scanner scanner;

    public Client(){
        try{
            socket = new Socket("localhost", 5555);
            System.out.println("서버 접속 완료");
            scanner = new Scanner(System.in);
            while (true){

            }
        }catch (IOException e){}
    }
}
