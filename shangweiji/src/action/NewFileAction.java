package action;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import java.io.File;

public class NewFileAction extends ActionSupport {
    private String filepath;

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String execute(){
        ActionContext actionContext = ActionContext.getContext();
        filepath = actionContext.getSession().get("filepath").toString();
        File file= new File(filepath,"新建文件夹");
        file.mkdir();
        return SUCCESS;
    }
}
