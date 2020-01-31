import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        //Socket socket=serverSocket.accept();
//        DataOutputStream dataOutputStream =
//                new DataOutputStream(socket.getOutputStream());
//        DataInputStream dataInputStream =
//                new DataInputStream(socket.getInputStream());

        //Scanner scanner = new Scanner(dataInputStream);
        Scanner s2 = new Scanner(System.in);
        while (s2.hasNext()) {
            System.out.println(s2.nextLine());
        }
    }
}
