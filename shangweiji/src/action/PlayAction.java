package action;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import server.Client;
import server.IncorrectResponseException;
import server.NoResponseException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.*;

/**
 * 注意，struts2的Action可以通过向自己控制的网页转发，从而储存网页上的相关信息
 */
public class PlayAction extends ActionSupport {
    private String filename;//文件全名
    private List<File> files;
    //    private int jindu;//0~100%
//    private boolean isConnected;
//    private boolean isPlaying;要以json格式存储这些网页播放器信息！

    /**
     * 进入播放页面时使用的方法，在此要初始化files
     */
    public String enter() {
        //将filename存进session域中
        ActionContext.getContext().getSession().put("listening", filename);
        listFiles();
        return SUCCESS;
//        if (ActionContext.getContext().getApplication().get("connectionStatus").equals("true"))
//            return SUCCESS;
//        else return ERROR;
    }

    public String play() throws IOException {
//        fileName在回传的时候丢失了，因此应当被存到seesion中去
        filename = (String) ActionContext.getContext().getSession().get("listening");
        listFiles();
        //在此处调用树莓派播放函数
        //是否收到post请求,流的方式读取
        String json = getJson();

        //按照播放器操作将json转成PiResponse,此处不处理mode与volume
        Client client = (Client) ActionContext.getContext().getApplication().get("client");
        HashMap<String, String> map = parseJSON(json);
        ServletActionContext.getResponse().setCharacterEncoding("UTF-8");
        PrintWriter out = ServletActionContext.getResponse().getWriter();

        try {
            if (map.get("paused").equals("true")) {
                client.stop();//暂停
                out.println("树莓派已暂停");
            } else {
                //先判断是开始播放，还是继续
                if (map.get("currentTime").equals("0")) {
                    //要检验文件是否存在
                    String fp = map.get("file");
                    String f = fp.substring(fp.lastIndexOf("/") + 1, fp.length() - 1);
                    if (client.check(f)) {
                        out.println("树莓派开始播放");
                        client.start(f);
                    } else {
                        out.println("正在向树莓派传输该文件" + fp.substring(1, fp.length() - 1));
                        client.sendFile(new File(fp.substring(1, fp.length() - 1)));
                    }
                } else {
                    client.resume(map.get("currentTime"));
                    out.println("树莓派从该进度开始：" + map.get("currentTime"));
                }
            }
        } catch (NoResponseException e) {
            //info = "树莓派没有响应";
            out.println("树莓派没有响应");
            e.printStackTrace();
            return NONE;
        } catch (IncorrectResponseException e2) {
            //info = "树莓派响应不正确";
            out.println("树莓派响应不正确");
            e2.printStackTrace();
            return NONE;
        }
        out.flush();
        out.close();
        return NONE;
    }

    /**
     * 不嵌套的json解析函数
     *
     * @param input json字符串
     * @return 一个Map
     */
    private static HashMap<String, String> parseJSON(String input) {
        try (JsonReader jsonReader = Json.createReader(new StringReader(input))) {
            HashMap<String, String> map = new HashMap<>();
            JsonObject object = jsonReader.readObject();
            for (String s : object.keySet()) map.put(s, object.get(s).toString());
            return map;
        }
    }

    private String getJson() {
        //是否收到post请求,流的方式读取
        HttpServletRequest request = ServletActionContext.getRequest();
        String json = null;
        try {
            Scanner scanner = new Scanner(request.getInputStream());
            json = scanner.next();
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 无传值时初始化files
     */
    private void listFiles() {
        files = Arrays.asList(Objects.requireNonNull(
                new File(ActionContext.getContext().getSession().get("filepath").toString())
                        .listFiles()));
    }

    public String mode() throws IOException {
        String json = getJson();
        Client client = (Client) ActionContext.getContext().getApplication().get("client");
        HashMap<String, String> map = parseJSON(json);
        ServletActionContext.getResponse().setCharacterEncoding("UTF-8");
        PrintWriter out = ServletActionContext.getResponse().getWriter();
        try {
            String loop = map.get("loop");
            client.setMode(loop);
            client.setMode(map.get("volume"));
        } catch (NoResponseException e) {
            //info = "树莓派没有响应";
            out.println("树莓派没有响应");
            e.printStackTrace();
            return NONE;
        } catch (IncorrectResponseException e2) {
            //info = "树莓派响应不正确";
            out.println("树莓派响应不正确");
            e2.printStackTrace();
            return NONE;
        }
        out.flush();
        out.close();
        return NONE;
    }

    public String last() {
        File last = null;
        for (File s : files) {
            if (filename.contains(s.getName())) {
                if (last == null) {
                    addActionMessage("这是第一首");
                    return SUCCESS;
                }
                break;
            }
            if (s.getName().contains(".") && ServletActionContext.getServletContext().getMimeType(s.getName()).contains("image"))
                last = s;
        }
        filename = last.getName();
        return SUCCESS;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String next() {
        String next = null;
        boolean flag = false;

        Iterator<File> set = files.iterator();
        while (set.hasNext()) {
            String s = set.next().getName();
            if (filename.contains(s)) flag = true;
            if (flag) {
                do {
                    if (!set.hasNext()) {
                        addActionMessage("没有更多了");
                        return SUCCESS;
                    }
                    next = set.next().getName();
                    while (!next.contains("."))//next是文件夹的话
                        next = set.next().getName();
                }
                while (!(ServletActionContext.getServletContext().getMimeType(next).contains("image")));
                break;
            }
        }
        filename = ActionContext.getContext().getApplication().get("root").toString() + "\\" + next;
        return SUCCESS;
    }
}
