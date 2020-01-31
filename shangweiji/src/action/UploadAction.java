package action;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class UploadAction extends ActionSupport {
    private File upload;
    private String uploadContentType,uploadFileName,uploadPath;
    //Action类必须要xxx,xxxContentType,xxxFileName与文件域的信息对应；

    /**
     * 1.1版本上传单个文件所用的方法
     */
    public String execute() throws IOException {
        uploadPath = ActionContext.getContext().getSession().get("filepath").toString();
        String fn = uploadPath +"\\"+ uploadFileName;
        if(new File(fn).exists()) return ERROR;
        else {
            FileInputStream is = new FileInputStream(upload);
            FileOutputStream os = new FileOutputStream(fn);
            IOUtils.copy(is,os);
            is.close();
            os.close();
        }
        return SUCCESS;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getUploadFileName() {
        return uploadFileName;
    }

    public void setUploadFileName(String uploadFileName) {
        this.uploadFileName = uploadFileName;
    }

    public String getUploadContentType() {
        return uploadContentType;
    }

    public void setUploadContentType(String uploadContentType) {
        this.uploadContentType = uploadContentType;
    }

    public File getUpload() {
        return upload;
    }

    public void setUpload(File upload) {
        this.upload = upload;
    }

}
