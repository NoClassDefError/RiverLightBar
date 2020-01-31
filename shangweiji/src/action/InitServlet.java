package action;

import server.Client;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;


public class InitServlet extends HttpServlet {
    public void init(){
        ServletContext servletContext = this.getServletContext();
        Client client = new Client("localhost",1234);
        servletContext.setAttribute("client",client);
        servletContext.setAttribute("root","C:\\root");
        servletContext.setAttribute("IP","localhost");
        servletContext.setAttribute("port",1234);
        servletContext.setAttribute("python",false);
    }

    public void destroy(){

    }
}
