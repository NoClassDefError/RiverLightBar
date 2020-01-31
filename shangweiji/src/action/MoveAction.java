package action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MoveAction extends ActionSupport {
    private String filepath, location;

    public String execute() throws IOException {
        location = location + "\\" + filepath.substring(filepath.lastIndexOf("\\"));
        File p = new File(filepath), l = new File(location);
        if (!l.exists())
            l.createNewFile();
        FileInputStream inputStream = new FileInputStream(p);
        FileOutputStream outputStream = new FileOutputStream(l);
        IOUtils.copy(inputStream, outputStream);
        inputStream.close();
        outputStream.close();
        DeleteAction.delete(p);
        return SUCCESS;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String path) {
        this.filepath = path;
    }
}
