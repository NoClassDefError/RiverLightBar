package action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class DownloadAction extends ActionSupport {
    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    private String filepath, filename, type;

    public InputStream getTargetFile() throws FileNotFoundException {
        System.out.println("下载："+filepath);
        return new FileInputStream(filepath);
    }

    public String execute() {
        File file = new File(filename);
        filename = file.getName();
        type = ServletActionContext.getServletContext().getMimeType(filepath);
        return "success";
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
