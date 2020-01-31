package action;

import com.opensymphony.xwork2.ActionSupport;

import java.io.File;

public class DeleteAction extends ActionSupport {
    private String filename;

    public String getFilename() {
        return filename.substring(0, filename.lastIndexOf("\\"));
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String execute() {
        File file = new File(filename);
        delete(file);//为什么刚上传的文件删不掉?因为流没有关闭，导致占用
        return SUCCESS;
    }

    static void delete(File file) {
        if (file.exists())
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) delete(f);
                if (file.listFiles().length == 0) if (!file.delete()) System.out.println("删不掉！");
            } else if (!file.delete()) System.out.println("删不掉！");
    }

}
