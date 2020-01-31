package action;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.io.IOUtils;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class StreamAction extends ActionSupport {
    private String filename;
    /**
     * 按照流的形式输出到网页
     *
     * @return “success”
     * @throws IOException yes
     */
    public String execute() throws IOException {
        HttpServletResponse response = ServletActionContext.getResponse();
        OutputStream outputStream = response.getOutputStream();
//        String filename = (String) ActionContext.getContext().get("filename");
        String type = ServletActionContext.getRequest().getServletContext().getMimeType(filename);
        FileInputStream fileInputStream = new FileInputStream(filename);
        response.setContentType(type);
        if (type.contains("%")) response.setCharacterEncoding(getEncodingByMeta(fileInputStream));
        else response.setCharacterEncoding(codeString(new File(filename)));
        IOUtils.copy(fileInputStream, outputStream);
        fileInputStream.close();
        outputStream.close();
        return SUCCESS;
    }

    /**
     * 判断文件的编码格式
     *
     * @param file
     * @return 文件编码格式
     * @throws Exception
     */
    private static String codeString(File file) throws IOException {
        BufferedInputStream bin = new BufferedInputStream(new FileInputStream(file));
        int p = (bin.read() << 8) + bin.read();
        String code = null;
        //其中的 0xefbb、0xfffe、0xfeff、0x5c75这些都是这个文件的前面两个字节的16进制数
        switch (p) {
            case 0xefbb:
                code = "UTF-8";
                break;
            case 0xfffe:
                code = "Unicode";
                break;
            case 0xfeff:
                code = "UTF-16BE";
                break;
            case 0x5c75:
                code = "ANSI|ASCII";
                break;
            default:
                code = "GBK";
        }
        return code;
    }

    /**
     * 从网页meta中解析出charset
     */
    private static String getEncodingByMeta(InputStream inputs) {
        String strencoding;
        // StringBuffer sb = new StringBuffer();
        String line;
        BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(inputs));
            while ((line = in.readLine()) != null) {
                // sb.append(line);
                if (line.contains("<meta") || line.contains("<META")) {
                    // 解析html源码，取出<meta />区域，并取出charset
                    line = line.toLowerCase();
                    String strbegin = "<meta";
                    String strend = ">";
                    String strtmp;
                    int begin = line.indexOf(strbegin);
                    int end = -1;
                    int inttmp;
                    while (begin > -1) {
                        end = line.substring(begin).indexOf(strend);
                        if (begin > -1 && end > -1) {
                            strtmp = line.substring(begin, begin + end)
                                    .toLowerCase();
                            inttmp = strtmp.indexOf("charset");
                            if (inttmp > -1) {
                                strencoding = strtmp.substring(inttmp + 7, end)
                                        .replace("=", "").replace("/", "")
                                        .replace("\"", "").replace("\'", "");
//										.replace(" ", "#");
                                if (strencoding.indexOf(" ") != -1) {
                                    strencoding = strencoding.substring(0, strencoding.indexOf(" "));
                                }
                                return strencoding;
                            }
                        }
                        line = line.substring(begin);
                        begin = line.indexOf(strbegin);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e);
        }
        return "utf-8";
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
