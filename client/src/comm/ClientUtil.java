package comm;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 *
 */
public class ClientUtil {

    /**
     * 查询树莓派上的音乐文件<br>
     * 初步决定树莓派通过外接U盘的方式，以单个文件夹储存音乐文件
     *
     * 100-文件夹目录<br>200-空文件夹
     */
    public static void sendFileNames(File dir, DataOutputStream dos) {
        try {
            if (dir.list() != null) dos.writeUTF(new PiResponse(100, Objects.requireNonNull(dir.list())).response);
            else dos.writeUTF(new PiResponse(200).response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询树莓派上音乐文件的播放状态
     * <br>
     * 正在播放时，每隔0.5秒发送一次播放状态
     *
     *  101-正在播放<br>102-已暂停<br>202-停止
     */
    public static void sendPlayStatus(DataOutputStream dos) {


    }


}
