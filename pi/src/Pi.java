import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import javax.sound.sampled.*;
import java.io.*;

import java.net.*;
import java.util.*;

/**
 * 为了避免linux上的Java运行依赖困难，此处在一个文件中编写所有内容，并尽量避免对非官方jar包的依赖
 * <br>
 * 此类用于测试以及控制播放器
 * <br/>
 *
 * @author Gehanchen
 */
public class Pi {

    private String path = "E:\\PiStorage";
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private PlayerControl control;
    private Player player = new Player();
    private SocketToPython pySocket = null;

    private Pi() throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        //pyLink();
        //System.out.println("已连接python");
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("accepted! IP=" + socket.getInetAddress());
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            //创建播放器控制器
            control = new PlayerControl(new Player(), null);
            //注意！readRequest()方法与player不能是一个线程！直接操控player的是PiUtil的方法
            readRequest();
        }
    }

    private void readRequest() {
        Scanner scanner = new Scanner(dataInputStream);
        Thread thread = null;
        System.out.println("readrequest");

        while (scanner.hasNext()) {
            PiTP piTP = new PiTP(scanner.nextLine());
            System.out.println("get request:" + piTP.code + " " + Arrays.toString(piTP.added));
            switch (piTP.code) {
                case 300://play
                    control.setFilename(path + "\\" + piTP.added[0]);
                    thread = new Thread(control::play);
                    thread.start();
                    new PiTP(201).sendResponse(dataOutputStream);
                    break;
                case 301://mode
                    PlayerControl.mode(piTP.added[0]);
                    new PiTP(201).sendResponse(dataOutputStream);
                    break;
                case 302://transfer file
                    String filename = piTP.added[0];
                    new PiTP(201).sendResponse(dataOutputStream);
                    PiUtils.create(filename, dataInputStream);
                    break;
                case 303://resume
                    if (thread != null)
                        thread.stop();
                    //在此不得不将原播放线程直接停止，重新创建新的播放线程

                    control.setFilename(path + "\\" + "rabbit.wav");
                    control.time = piTP.added[0];
                    thread = new Thread(control::resume);
                    thread.start();
                    new PiTP(201).sendResponse(dataOutputStream);
                    break;
                case 304://set volume
                    control.volume = piTP.added[0];
                    control.setVolume();
                    new PiTP(201).sendResponse(dataOutputStream);
                    break;
                case 305:
                    new PiTP(201).sendResponse(dataOutputStream);
                    pySocket.write("light " + piTP.added[0]);
                    break;
                case 306:
                    new PiTP(201).sendResponse(dataOutputStream);
                    pySocket.write("line_num " + piTP.added[0]);
                    pySocket.write("line_length " + piTP.added[1]);
                    break;
                case 307:
                    new PiTP(201).sendResponse(dataOutputStream);
                    pyLink();//没有做超时处理
                    System.out.println("python已连接");
                    break;
                case 308:
                    new PiTP(201).sendResponse(dataOutputStream);
                    pySocket.write("color " + piTP.added[0]);
                    break;
                case 401://pause
                    control.pause();
                    new PiTP(201).sendResponse(dataOutputStream);
                    break;
                case 402://stop
                    control.pause();
                    player.IsEnd = true;
                    new PiTP(201).sendResponse(dataOutputStream);
                    break;
                case 404://lookup
                    String[] s = PiUtils.lookup(path);
                    if (s == null) new PiTP(200).sendResponse(dataOutputStream);
                    else new PiTP(100, s).sendResponse(dataOutputStream);
                    break;
                case 405://get storage
                    new PiTP(103, new String[]{PiUtils.getStorage(path) + ""}).sendResponse(dataOutputStream);
                    break;
                case 406://clear all
                    PiUtils.clearAll(path);
                    new PiTP(201).sendResponse(dataOutputStream);
                    break;
            }
        }
    }

    void pyLink() {
        pySocket = new SocketToPython();
    }

    public static void main(String[] args) throws IOException {
        new Pi();
    }

}

class SocketToPython {
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    SocketToPython() {
        try {
            Socket socket = new Socket("localhost", 12345);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void write(String s) {
        try {
            dataOutputStream.write(s.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class PiUtils {
    static void create(String filename, DataInputStream fis) {
        File file = new File(filename);
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            copy(fis, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copy(InputStream inputStream, OutputStream outputStream) {
        int n = 1024, count;
        byte[] buf = new byte[n];
        try {
            while (((count = inputStream.read(buf)) != -1))
                outputStream.write(buf, 0, count);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String[] lookup(String path) {
        File file = new File(path);
        if (file.isDirectory()) return file.list();
        else {
            file.mkdirs();
            return null;
        }
    }

    static long getStorage(String path) {
        File file = new File(path);
        long store = 0;
        if (file.isDirectory()) {
            for (File listFile : Objects.requireNonNull(file.listFiles())) store += listFile.getTotalSpace();
            return store;
        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return store;
        }
    }

    static boolean clearAll(String path) {
        File file = new File(path);
        return file.delete();
    }
}

class PlayerControl {
    public Player getPlayer() {
        return player;
    }

    public String getFilename() {
        return filename;
    }

    void setFilename(String filename) {
        this.filename = filename;
    }

    String time = "0";
    String volume = "0.3";
    private Player player;

    private String filename;

    PlayerControl(Player player, String filename) {
        this.player = player;
        this.filename = filename;
    }

    void play() {
        player.IsEnd = false;
        player.audio = filename;
        player.HTTPFlag = false;
        player.play(0);
    }

    void playWeb() {
        player.IsEnd = false;
        player.audio = filename;
        player.HTTPFlag = true;
        player.play(0);
    }

    static void mode(String mode) {
        if (mode.equals("loop")) {

        } else {

        }
    }

    void resume() {
        int length = Player.getAudioTrackLength(player.audio);
        double progress = Double.parseDouble(time) / length;
        player.IsPause = false;
        player.IsEnd = false;
        System.out.println("time " + time + " length " + length + " resume Progress=" + progress);
        player.play(progress);
    }

    void setVolume() {
        player.getFloatControl().setValue((float) (Float.parseFloat(volume) * 0.86 - 80));
    }

    void pause() {
        player.IsPause = true;//使用标志的方法实现对线程的控制，线程在执行任务时要不断查看标志位
    }

}

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
 * 存储容量大小 103<br>
 * 2.没有修饰的Pi响应类型    第一位数字是2<br>
 * 空文件夹 200<br>
 * 已收到命令 201<br>
 *
 * <p>服务器的响应类型：<br>
 * 3.有修饰的服务器响应类型    第一位数字是3<br>
 * 开始播放 300 返回要播放的曲目<br>
 * 播放模式 301 返回播放模式<br>
 * 开始文件传输 302 返回文件名<br>
 * 继续播放 303 返回时值<br>
 * 音量大小 304 <br>
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
 * 上一首 407<br>
 * <p>
 * *由于PiResponse类本身没有多态，因此不需要设计工厂模式
 */
class PiTP {
    int code, hc, lc;//code是状态码，hc为其最高位
    String[] added;
    private String response;

    private String shake(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c < 32) sb.append(0);
            else sb.append(c);
        }
        return new String(sb);
    }

    PiTP(int code, String[] added) {
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
    PiTP(String response) {
        //先查看状态码
        this.response = shake(response);
        System.out.println("解析中：" + shake(response));
        String[] s = shake(response).split(" ");
        code = Integer.parseInt(s[0]);
        hc = code / 100;
        lc = code % 100;
        if (hc == 2 || hc == 3) {
            added = new String[s.length - 1];
            System.arraycopy(s, 1, added, 0, s.length - 1);
        }
    }

    /**
     * 构造没有修饰的响应类型
     */
    PiTP(int code) {
        this.code = code;
        this.lc = code % 100;
        this.hc = code / 100;
        this.response = code + "";
    }

    public String toString() {
        return response;
    }

    /**
     * 向服务器做出响应
     *
     * @param dos socket的outputstream
     */
    void sendResponse(DataOutputStream dos) {
        try {
            System.out.println("response sended  |" + response);
            dos.writeUTF(this.response + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/**
 * 底层播放器，主要控制播放
 */
class Player {

    private AudioInputStream audioInputStream;
    String audio;
    boolean HTTPFlag;//是否为网络资源
    boolean IsPause = false;// 是否停止播放状态
    boolean IsEnd;
    /**
     * -80~6
     */
    float volume = -20;

    // 检测输入流是否阻塞
    private boolean IsChoke;

    //没卵用，用于检查网络连接
    private Timer checkConnection;

    // 音量控制
    private FloatControl floatVoiceControl;

    /**
     * @param progress 0%~100%
     */
    synchronized void play(double progress) {

        try {
            // 获取网络音频输入流
            if (HTTPFlag) {
                try {
                    URL url = new URL(audio);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    audioInputStream = AudioSystem.getAudioInputStream(urlConnection.getInputStream());
                    // 用计时器监测歌曲连接状态 初始启动计时器
                    checkConnectionSchedule();
                } catch (ConnectException e) {
                    // 连接超时
                    System.out.println("网络资源连接异常");
                    return;
                }
            } else
                // 获取本地音频输入流
                audioInputStream = AudioSystem.getAudioInputStream(new File(audio));
            // 获取音频编码格式
            AudioFormat audioFormat = audioInputStream.getFormat();
            // MPEG1L3转PCM_SIGNED
            if (audioFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                        audioFormat.getSampleRate(), 16, audioFormat.getChannels(),
                        audioFormat.getChannels() * 2,
                        audioFormat.getSampleRate(), false);
                audioInputStream = AudioSystem.getAudioInputStream(audioFormat, audioInputStream);
            }
            // 根据上面的音频格式获取输出设备信息
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            // 获取输出设备对象
            SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);

            // 打开输出管道
            sourceDataLine.open();
            // 允许此管道执行数据 I/O
            sourceDataLine.start();

            // 获取总音量的控件
            floatVoiceControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);

            // 音量minValue -80 maxValue 6
            // 设合适的初始音量
            floatVoiceControl.setValue(volume);

            byte[] buf = new byte[1024];
            int onceReadDataSize;

            //确定从哪里开始播放
            audioInputStream.skip((long) (new File(audio).length() * progress));
            System.out.println("space=" + new File(audio).length() + "skipped:" + (long) (new File(audio).length() * progress));
            while ((onceReadDataSize = audioInputStream.read(buf, 0, buf.length)) != -1) {
                // 输入流没有阻塞
                IsChoke = false;
                if (IsEnd) return;
                // 是否暂停
                if (IsPause) pause();
                floatVoiceControl.setValue(volume);
                // 将数据写入混频器中 至输出端口写完前阻塞
                sourceDataLine.write(buf, 0, onceReadDataSize);
                // 预设输入流阻塞
                IsChoke = true;
            }

            IsChoke = false;
            // 冲刷缓冲区数据
            sourceDataLine.drain();

            sourceDataLine.close();
            audioInputStream.close();

            if (checkConnection != null) {
                checkConnection.cancel();
                checkConnection.purge();
                checkConnection = null;
                // System.out.println("EndTimeOutControl");
            }

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkConnectionSchedule() {

        checkConnection = new Timer(true);

        checkConnection.schedule(new TimerTask() {

            // 阻塞计数
            int times = 0;

            @Override
            public void run() {
                if (IsChoke) {
                    times++;
                    // 如果检测到阻塞次数有20次
                    if (times == 20) {
                        try {
                            // 使playThread自然执行完
                            IsEnd = false;
                            // 输入流关闭
                            audioInputStream.close();
                            System.out.println("连接异常中断");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else
                    times = 0;
                // System.out.println(times);
            }
        }, 2000, 500);
    }

    public synchronized void resume() {
        IsPause = false;
        this.notify();
    }

    private synchronized void pause() throws InterruptedException {
        this.wait();
    }

    // 获取音频文件的长度 秒数
    static int getAudioTrackLength(String s) {
        try {
            // 只能获得本地歌曲文件的信息
            AudioFile file = AudioFileIO.read(new File(s));
            // 获取音频文件的头信息
            AudioHeader audioHeader = file.getAudioHeader();
            // 文件长度 转换成时间
            return audioHeader.getTrackLength();
        } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
            e.printStackTrace();
            return -1;
        }
    }

    FloatControl getFloatControl() {
        return floatVoiceControl;
    }

}
