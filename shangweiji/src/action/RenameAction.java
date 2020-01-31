package action;

import com.opensymphony.xwork2.ActionSupport;

import java.io.File;

public class RenameAction extends ActionSupport {
    private String fullname, newname;//储存改名的文件的filepath在request中容易与session里的发生冲突

    public String execute() {
        File old = new File(fullname);
        File newFile = new File(fullname.substring(0,fullname.lastIndexOf("\\"))+"\\"+newname);
        old.renameTo(newFile);
//        ActionContext.getContext().getSession().replace("filepath",newFile.getAbsolutePath());
        return SUCCESS;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getNewname() {
        return newname;
    }

    public void setNewname(String newname) {
        this.newname = newname;
    }
}
