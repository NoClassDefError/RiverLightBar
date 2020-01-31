package server;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * 服务端要实现的功能：<br>
 * 1.发送控制信息<br>
 * 播放，暂停，曲目，播放模式，灯条模式<br>
 * 2.接受检测信息<br>
 * 获取树莓派端的曲目，播放状态，灯条模式<br>
 * 3.发送文件<br>
 * <p>
 * 树莓派与上位机通信协议：<br>
 * 1.上位机向树莓派发送文件时，上位机先向树莓派发送请求头meta信息，树莓派接受，再进行文件传输。<br>
 * 2.上位机向树莓派发送控制信息时，只需向树莓派发送相应请求码即可，树莓派接受并做出响应。<br>
 * 3.树莓派向上位机发送自身状态，
 */
public class Client {

    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    public Socket socket;

    public Client(String IP, int port) {
        connect(IP, port);
    }

    public boolean connect(String IP, int port) {
        try {
            //if(!socket.isClosed())socket.close();
            socket = new Socket(IP, port);
            System.out.println(socket.getInetAddress() + " 已连接");
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            return true;
        } catch (IOException e) {
            System.out.println("未能连接至树莓派");
            return false;
        }
    }

    /**
     * 命令行控制服务器端各类接口
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Client ss = new Client("localhost", 1234);
        while (true) {
            String command = scanner.nextLine();
            try {
                new PiTP(command).sendOrder(new DataInputStream(ss.inputStream), new DataOutputStream(ss.outputStream));
            } catch (NoResponseException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 向树莓派发送音乐文件，注意发送后要关闭流
     *
     * @param file 该文件必须为.wav格式
     */
    public void sendFile(File file) throws NoResponseException {
        try {
            //写入头信息
            int resp = new PiTP(302, new String[]{file.getName()}).sendOrder(inputStream, outputStream).code;
            if (resp != 201) throw new IncorrectResponseException();
            //开始传输文件
            IOUtils.copy(new FileInputStream(file), outputStream);
        } catch (IncorrectResponseException | IOException e) {
            e.printStackTrace();
        }
    }


    public boolean check(String File) throws NoResponseException {
        String[] ss = getFileNames();
        System.out.println("check"+ss[0] +"file"+File);
        for (String s : ss) if (s.equals(File)) return true;
        return false;
    }

    public void start(String fileName) throws NoResponseException, IncorrectResponseException {
        int result = new PiTP(300, new String[]{fileName}).sendOrder(inputStream, outputStream).code;
        if (result != 201) throw new IncorrectResponseException();
    }

    public void resume(String currentTime) throws NoResponseException, IncorrectResponseException {
        int result = new PiTP(303, new String[]{currentTime}).sendOrder(inputStream, outputStream).code;
        if (result != 201) throw new IncorrectResponseException();
    }

    public void stop() throws NoResponseException, IncorrectResponseException {
        int result = new PiTP(401).sendOrder(inputStream, outputStream).code;
        if (result != 201) throw new IncorrectResponseException();
    }

    public String[] getFileNames() throws NoResponseException {
        return new PiTP(404).sendOrder(inputStream, outputStream).added;
    }

    public String getStorage() throws NoResponseException {
        return new PiTP(405).sendOrder(inputStream, outputStream).added[0];
    }

    public void clear() throws NoResponseException, IncorrectResponseException {
        if (new PiTP(406).sendOrder(inputStream, outputStream).code != 201)
            throw new IncorrectResponseException();
    }

    public void setMode(String mode) throws NoResponseException, IncorrectResponseException {
        if (new PiTP(301, new String[]{mode}).sendOrder(inputStream, outputStream).code != 201)
            throw new IncorrectResponseException();
    }

    public void setVolume(String volume) throws IncorrectResponseException, NoResponseException {
        if (new PiTP(301, new String[]{volume}).sendOrder(inputStream, outputStream).code != 201)
            throw new IncorrectResponseException();
    }

    public void setLight(String light) throws NoResponseException, IncorrectResponseException {
        if (new PiTP(305, new String[]{light}).sendOrder(inputStream, outputStream).code != 201)
            throw new IncorrectResponseException();
    }

    public void setNumber(String lineNum,String lineLength) throws NoResponseException, IncorrectResponseException {
        if (new PiTP(306, new String[]{lineNum,lineLength}).sendOrder(inputStream, outputStream).code != 201)
            throw new IncorrectResponseException();
    }

    public boolean getPython() throws NoResponseException {
        return new PiTP(307).sendOrder(inputStream, outputStream).code != 201;
    }

    public void setColor(String color) throws IncorrectResponseException, NoResponseException {
        if (new PiTP(308, new String[]{color}).sendOrder(inputStream, outputStream).code != 201)
            throw new IncorrectResponseException();
    }
}
