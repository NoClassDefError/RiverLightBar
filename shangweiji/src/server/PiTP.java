package server;


import org.apache.commons.io.IOUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * 基于TCP协议的PiTP协议，Pi transport protocol <br><br>
 * 此连接协议被设计为多对一的，取决于java socket连接的特性，
 * 本协议中的服务器端信息格式由本类决定。<br>
 * <p>
 * 本类用于将树莓派和服务器发出各种响应打包并发送<br>
 *
 * <p>树莓派的响应类型：<br>
 * 1.有修饰的Pi响应类型     第一位数字是1<br>
 * 文件查看 100 返回一个文件名数组 <br>
 * 播放进度 101<br>102-已暂停<br>
 * 2.没有修饰的Pi响应类型    第一位数字是2<br>
 * 空文件夹 200<br>
 * 已收到命令 201<br>
 * 错误 202<br>
 *
 * <p>服务器的响应类型：<br>
 * 3.有修饰的服务器响应类型    第一位数字是3<br>
 * 开始播放 300 返回要播放的曲目<br>
 * 播放模式 301 返回播放模式<br>
 * 开始文件传输 302 返回文件名<br>
 * 继续播放 303 返回时值<br>
 * 音量大小 304 <br>
 * 亮度 返回亮度305<br>
 * 灯条尺寸 306<br>
 * python连接 307<br>
 * 颜色  308
 *
 * <p>
 * 4.没有修饰的服务器响应类型   第一位数字是4<br>
 * <p>
 * 暂停 401<br>
 * 停止 402<br>
 * 下一首 403<br>
 * 文件查看 404<br>
 * 文件存储情况 405<br>
 * 清空内存 406<br>
 * <p>
 * *由于PiResponse类本身没有多态，因此不需要设计工厂模式
 */
public class PiTP {
    public int code, hc, lc;//code是状态码，hc为其最高位
    public String[] added;
    public String response;

    public PiTP(int code, String[] added) {
        this.code = code;
        this.added = added;
        StringBuilder responseb = new StringBuilder();
        responseb.append(code).append(" ");
        for (String s : added) responseb.append(s).append(" ");
        this.response = new String(responseb);
        hc = code / 100;
        lc = code % 100;
    }

    /**
     * 用于解析各种响应
     */
    public PiTP(String response) {
        //先查看状态码
        this.response = response;
        System.out.println(response);
        String[] s = response.split(" ");
        code = Integer.parseInt(s[0].substring(2));
        hc = code / 100;
        lc = code % 100;
        if (hc != 2) {
            added = new String[s.length - 1];
            System.arraycopy(s, 1, added, 0, s.length - 1);
        }
    }

    /**
     * 构造没有修饰的响应类型
     */
    public PiTP(int code) {
        this.code = code;
        this.lc = code % 100;
        this.hc = code / 100;
        this.response = code + "";
    }

    public String toString() {
        return response;
    }

    /**
     * 向树莓派发出请求，返回它的响应 必须使用多线程实现
     *
     * @param dis socket的inputstream
     * @param dos socket的outputstream
     * @return 树莓派做出的响应
     */
    public PiTP sendOrder(DataInputStream dis, DataOutputStream dos) throws NoResponseException {
        PiTP piTP = null;
        try {
            System.out.println(new Date().toString() + " response sended  |" + response);
            dataInputStream = dis;
            //dos.writeUTF(response+"\n");
            Thread t = new Thread(this::readRequest);
            t.start();
            //System.out.println("waiting...");
            Thread.sleep(500);
            request = null;
            dos.writeUTF(response + "\n");
            Thread.sleep(500);

            long num = 100000000;
            while(true){
                if(request!=null){
                    piTP = new PiTP(request);//直到有响应,线程的赋值对于它不可见
                    request=null;
                    break;
                }
                num--;
                if(num<=0){//处理响应超时
                    System.out.println(new Date().toString() + " 重新发送请求 response sended  |" + response);
                    Thread t2 = new Thread(this::readRequest);
                    t2.start();
                    Thread.sleep(500);
                    request = null;
                    dos.writeUTF(response+"\n");
                    num=100000000;
                }
            }
        } catch (NoSuchElementException | NullPointerException e2) {
            throw new NoResponseException();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if(piTP==null){
            System.out.println("已发出控制信息，但树莓派正忙");
            piTP = new PiTP(201);//强制接收
        }
        return piTP;
    }

    private DataInputStream dataInputStream;
    private volatile String request = null;//必须确保可见性,并防止线程的占用

    private void readRequest() {
        System.out.println(new Date() + " readRequest() running  |");
        Scanner scanner = new Scanner(dataInputStream);
        if (scanner.hasNext()) {
            request = scanner.nextLine();
            System.out.println("request: " + request);
        }

//            byte[] b = new byte[1024];
//            while (dataInputStream.read(b) != -1) {//文件超长，它读了两次，并把之前的给覆盖了！
//                for (int i = 0; i < b.length; i++) if (b[i] < 32) b[i] = 0;
//                request = new String(b);
//                b = new byte[1024];
//            }

    }
}

