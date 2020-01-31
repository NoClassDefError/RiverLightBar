import org.apache.commons.io.IOUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class TestClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("192.168.137.43", 8088);
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        Scanner scanner = new Scanner(dis);
        dos.write("helloworld2".getBytes());
        IOUtils.copy(dis, System.out);
        //socket.close();
    }
}
