package comm;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
 *
 * <p>服务器的响应类型：<br>
 * 3.有修饰的服务器响应类型    第一位数字是3<br>
 * 开始播放 300 返回要播放的曲目<br>
 * 播放模式 301 返回播放模式<br>
 * 开始文件传输 302 返回文件名<br>
 * <p>
 * 4.没有修饰的服务器响应类型   第一位数字是4<br>
 * <p>
 * 暂停 401<br>
 * 停止 402<br>
 * 下一首 403<br>
 */
public class PiResponse {
    public int code, hc, lc;//code是状态码，hc为其最高位
    public String[] added;
    public String response;

    public PiResponse(int code, String[] added) {
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
    public PiResponse(String response) {
        //先查看状态码
        this.response = response;
        String[] s = response.split(" ");
        code = Integer.parseInt(s[0]);
        hc = code / 100;
        lc = code % 100;
        if (hc == 2) {
            added = new String[s.length - 1];
            System.arraycopy(s, 1, added, 0, s.length - 1);
        }
    }

    /**
     * 构造没有修饰的响应类型
     */
    public PiResponse(int code) {
        this.code = code;
        this.lc = code % 100;
        this.hc = code / 100;
        this.response = code+"";
    }

    public String toString() {
        return response;
    }

    /**
     * 向树莓派发出请求，返回它的响应
     *
     * @param dis socket的inputstream
     * @param dos socket的outputstream
     * @return 树莓派做出的响应码
     */
    public int sendOrder(DataInputStream dis, DataOutputStream dos) {
        int a = 0;
        try {
            System.out.println("response sended  |"+response);
            dos.writeUTF(this.response);
            Scanner scanner = new Scanner(dis);
            a = new PiResponse(scanner.nextLine()).code;
        } catch (NoSuchElementException e2) {
            System.out.println("shumeipai meixiangying");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return a;
    }

    /**
     * 向服务器做出响应
     *
     * @param dos socket的outputstream
     */
    public void sendResponse(DataOutputStream dos) {
        try {
            System.out.println("response sended  |"+response);
            dos.writeUTF(this.response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

