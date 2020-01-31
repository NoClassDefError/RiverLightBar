import org.apache.commons.io.IOUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TestServer {
    public static void main(String[] args) {
        new TestServer();
    }

    DataInputStream inputStream;
    DataOutputStream outputStream;

    TestServer() {
        try {
            Socket socket = new Socket("localhost", 1234);
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());

            Thread getWrite = new Thread(this::aaaa);
            getWrite.start();
            //主线程中将System.in拷贝进outputStream
//            outputStream.writeUTF("404\n");
            IOUtils.copy(System.in, outputStream);

            //scanner.hasNext()函数会使得程序原地等待输入
            //程序执行到此便不再往下执行，可能是因为IOUtils.copy中带有无限循环
            //服务端必须采用多线程进行读写，树莓派端通常先读再写，不需要多线程

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void aaaa() {
        try {
            System.out.println("aaaa() running");
            byte[] b = new byte[32];
            while (inputStream.read(b) != -1){
                shake(b);
                System.out.write(b);
                b = new byte[32];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void shake(byte[] b) {
        for (int i = 0; i < b.length; i++) if (b[i] < 32) b[i] = 0;
    }
}
