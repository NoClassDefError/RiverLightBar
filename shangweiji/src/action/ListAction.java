package action;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class ListAction extends ActionSupport {
    private HashMap<String, String> files = new HashMap<>();
    private String filepath;

    @Override
    public String execute() {
        //文件路径初始化
        ActionContext context = ActionContext.getContext();
        String filepath = context.getApplication().get("root").toString();//默认储存路径
        if (context.getSession().containsKey("filepath")) {
            filepath = context.getSession().get("filepath").toString();
//            System.out.println(context.getSession().get("filepath").toString());
        } else context.getSession().put("filepath", filepath);
        File file = new File(filepath);
        fileList(file, files);
        context.put("filepath", filepath);
        context.put("fileMap", files);
        return SUCCESS;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public HashMap<String, String> getFiles() {
        return files;
    }

    public void setFiles(HashMap<String, String> files) {
        this.files = files;
    }

    /**
     * fileMap第一个是端
     */
    private void fileList(File file, Map fileMap) {
        //如果file代表的不是一个文件，而是一个目录
        File[] files = file.listFiles();
        if (files == null) files = new File(file.getParent()).listFiles();
        for (File file2 : files) {
//                if(!file2.isFile())continue;
//            System.out.println(file2.getName());
            String type;
            if (!file2.isFile()) type = "file";
            else type =  ServletActionContext.getServletContext().getMimeType(file2.getName());
            String realName = file2.getName().substring(file2.getName().lastIndexOf("\\")+1);
            fileMap.put(realName, type);
        }
    }

    public String readRequest() {
        ActionContext.getContext().getSession().replace("filepath", filepath);
        return SUCCESS;
    }
}
