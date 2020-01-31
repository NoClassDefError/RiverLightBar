package action;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import server.Client;
import server.IncorrectResponseException;
import server.NoResponseException;

public class WS2812action extends ActionSupport {
    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    private int result;

    public int getLineLength() {
        return lineLength;
    }

    public void setLineLength(int lineLength) {
        this.lineLength = lineLength;
    }

    private int light;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    private String color;

    public int getLight() {
        return light;
    }

    public void setLight(int light) {
        this.light = light;
    }

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    private int lineNum;
    private int lineLength;

    public String light() {
        if(!((boolean) (ActionContext.getContext().getApplication().get("python")))){
            result=0;
            return SUCCESS;
        }
        try {
            ((Client) ActionContext.getContext().getApplication().get("client"))
                    .setLight(light+"");
        } catch (NoResponseException | IncorrectResponseException e) {
            e.printStackTrace();
            result=0;
        }
        result=1;
        return SUCCESS;
    }

    public String number(){
        if(!((boolean) (ActionContext.getContext().getApplication().get("python")))){
            result=0;
            return SUCCESS;
        }
        try {
            ((Client) ActionContext.getContext().getApplication().get("client"))
                    .setNumber(lineNum+"",lineLength+"");
        } catch (NoResponseException | IncorrectResponseException e) {
            e.printStackTrace();
            result=0;
        }
        result=1;
        return SUCCESS;
    }

    public String python(){
        try {
            ((Client) ActionContext.getContext().getApplication().get("client")).getPython();
        } catch (NoResponseException e) {
            e.printStackTrace();
            result=0;
        }
        result=1;
        ActionContext.getContext().getApplication().replace("python",true);
        return SUCCESS;
    }

    public String color() throws IncorrectResponseException {
        if(!((boolean) (ActionContext.getContext().getApplication().get("python")))){
            result=0;
            return SUCCESS;
        }
        try {
            ((Client) ActionContext.getContext().getApplication().get("client")).setColor(color);
        } catch (NoResponseException e) {
            e.printStackTrace();
            result=0;
        }
        result=1;

        return SUCCESS;
    }

}
