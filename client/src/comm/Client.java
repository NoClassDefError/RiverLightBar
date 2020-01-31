package comm;

import org.apache.commons.io.IOUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * 树莓派客户端要实现的功能：
 * 控制音乐播放 要使用javax.media包或者其他第三方jar包如jl1.0.jar
 * 解析控制信息协议 播放（下载），暂停，停止（删除）
 */
class Client{
    public static void main(String[] args){
//        System.out.println("HelloWorld");
        try{
            //与指定IP及端口的服务器建立网络连接
            Socket ss = new Socket("localhost", 1234);
            DataInputStream dis = new DataInputStream(ss.getInputStream());
            DataOutputStream dos = new DataOutputStream(ss.getOutputStream());
//            File file = new File("D:\\Music\\music\\1.wav");
//            if(!file.exists()) file.createNewFile();
//            IOUtils.copy(dis,new FileOutputStream(file));

            String s = dis.readUTF();
            System.out.println(s);
            PiResponse resp = new PiResponse(s);

            switch(resp.code){
                case 302: new PiResponse(201).sendResponse(dos);break;
                case 4:break;
            }

            dis.close();
            ss.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


}
