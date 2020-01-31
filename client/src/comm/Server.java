package comm;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 认定电脑为服务端，树莓派为客户端，本类为服务端
 */
public class Server {
    public static void main(String [] args){
        File file = new File ("D:\\Music\\music\\aLIEz - Aldnoah.zero ED 2 [Piano].wav");
        try{
            ServerSocket serverSocket = new java.net.ServerSocket(1234);
//            while(true){
// 可以没有这个无限循环，serverSocket.accept()函数在没人来试图连接时一直处于阻塞状态，
// 这个循环是为了能在服务结束后开启新的一轮服务
                Socket socket = serverSocket.accept();
                //在此处可以向socket中带有的outputStream写入数据

                OutputStream ouputStream = socket.getOutputStream();
                DataOutputStream dataOutputStream  = new DataOutputStream(ouputStream);
//                IOUtils.copy(new FileInputStream(file),dataOutputStream);
                dataOutputStream.writeUTF("Hello," + socket.getInetAddress() +" port "+ socket.getPort());
                //接下来做实验：重复读取socket的outputStream会读到之前的内容吗？
                //不会，重复读取会清空之前的内容，如果没有内容则会抛出EOFException。
                dataOutputStream.close();
                socket.close();
//            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
