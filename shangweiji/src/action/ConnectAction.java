package action;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import server.Client;
import server.IncorrectResponseException;
import server.NoResponseException;

import javax.servlet.jsp.JspApplicationContext;
import java.util.Map;

public class ConnectAction extends ActionSupport {
    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    private int result;
    private String IP;
    private int port;
    private String info;

    public String execute() {
        Map<String, Object> context = ActionContext.getContext().getApplication();
        if(!context.containsKey("IP")){
            context.put("IP", IP);
            context.put("port", port);
        }else{
            context.replace("IP", IP);
            context.replace("port", port);
        }
        if (((Client) context.get("client")).connect(IP, port)){
            result=1;
        }else result=0;
        return SUCCESS;
    }

    public String clear() {
        try {
            ((Client) ActionContext.getContext().getApplication().get("client")).clear();
        } catch (NoResponseException | IncorrectResponseException e) {
            info = "树莓派无响应";
            result=0;
        }
        result=1;
        return SUCCESS;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
