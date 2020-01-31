package play;

import javax.sound.sampled.*;
import java.io.*;

public class MusicPlayer {
    //注意不支持MP3格式
    File file = new File("D:\\Music\\music\\rabbit.mp3");

    private MusicPlayer() {
        AudioInputStream audioInputStream;
        try {
            String filename = file.getPath() ;
            int pointIndex = filename.lastIndexOf(".");
            if (filename.substring(pointIndex).contains("mp3")) {
                filename = filename.replace("mp3", "wav");
                byteToWav(getBytes(file), filename);
            }
            audioInputStream = AudioSystem.getAudioInputStream(new File(filename));
            // 4,用AudioFormat来获取AudioInputStream的格式
            AudioFormat format = audioInputStream.getFormat();
            // 5.源数据行SoureDataLine是可以写入数据的数据行
            SourceDataLine auline = null;
            // 获取受数据行支持的音频格式DataLine.info
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            // 获得与指定info类型相匹配的行
            try {
                auline = (SourceDataLine) AudioSystem.getLine(info);
                // 打开具有指定格式的行，这样可使行获得所有所需系统资源并变得可操作
                auline.open();
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
            // 允许某一个数据行执行数据i/o
            auline.start();
            // 写出数据
            int nBytesRead = 0;
            byte[] abData = new byte[1024];
            // 从音频流读取指定的最大数量的数据字节，并将其放入给定的字节数组中。
            while (nBytesRead != -1) {
                nBytesRead = audioInputStream.read(abData, 0, abData.length);
                // 通过此源数据行将数据写入混频器
                if (nBytesRead >= 0)
                    auline.write(abData, 0, nBytesRead);
            }
            auline.drain();
            auline.close();
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new MusicPlayer();
    }

    /**
     * mp3的字节数组生成wav文件
     */
    public static boolean byteToWav(byte[] sourceBytes, String targetPath) {
        if (sourceBytes == null || sourceBytes.length == 0) {
            System.out.println("Illegal Argument passed to this method");
            return false;
        }

        try (final ByteArrayInputStream bais = new ByteArrayInputStream(sourceBytes); final AudioInputStream sourceAIS = AudioSystem.getAudioInputStream(bais)) {
            AudioFormat sourceFormat = sourceAIS.getFormat();
            // 设置MP3的语音格式,并设置16bit
            AudioFormat mp3tFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sourceFormat.getSampleRate(), 16, sourceFormat.getChannels(), sourceFormat.getChannels() * 2, sourceFormat.getSampleRate(), false);
            // 设置百度语音识别的音频格式
            AudioFormat pcmFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000, 16, 1, 2, 16000, false);
            try (
                    // 先通过MP3转一次，使音频流能的格式完整
                    final AudioInputStream mp3AIS = AudioSystem.getAudioInputStream(mp3tFormat, sourceAIS);
                    // 转成百度需要的流
                    final AudioInputStream pcmAIS = AudioSystem.getAudioInputStream(pcmFormat, mp3AIS)) {
                // 根据路径生成wav文件
                AudioSystem.write(pcmAIS, AudioFileFormat.Type.WAVE, new File(targetPath));
            }
            return true;
        } catch (IOException | UnsupportedAudioFileException e) {
            System.out.println("文件转换异常：" + e.getMessage());
            return false;
        }
    }

    /**
     * 将文件转成字节流
     */
    private static byte[] getBytes(File file) {
        byte[] buffer = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }
}
