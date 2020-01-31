<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page import="server.Client" %>
<%@ page import="server.NoResponseException" %>
<%@ page import="java.net.Socket" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>主页</title>
    <style type="text/css">
        /*网盘内容的框*/
        .section1 {
            font-family: Microsoft JhengHei, serif;
            font-size: 20px;
            border: 2px solid #dddddd;
            border-radius: 5px;
            width: 500px;
            height: 200px;
            padding-top: 30px;
            margin-top: 50px;
            padding-left: 100px;
            margin-left: 320px;
        }

        ul {
            list-style-type: none;
            margin: 0;
            padding: 0;
            overflow: hidden;
            background-color: #fff;
        }

        li {
            float: left;
        }

        li a {
            display: block;
            color: #666;
            text-align: center;
            text-decoration: none;
        }

        li a:hover:not(.active) {
            background-color: #ddd;
        }

        li a.active {
            color: white;
            background-color: #4CAF50;
        }
    </style>
</head>
<body>
<ul class="daohang">
    <li style="margin-top: 3px">
        <span style="font-size: 27px;color: #6666ee;font-family: Microsoft JhengHei,serif">绿色灯光河谷秀</span>
        <span style="font-size: 10px;color: #777">&emsp;版本 1.0 beta</span></li>
    <li style="margin-left: 150px"><a style="padding: 13px 100px;" href="list.action">音乐文件</a></li>
    <li><a href="indexUpload.jsp" target="_parent" style="padding: 13px 100px;">上传文件</a></li>
    <li><a style="padding: 13px 50px;" class="active" target="_parent">灯光控制</a></li>
</ul>
<div class="section1">
    树莓派的连接状态：
    <s:if test="result==0">树莓派未响应</s:if>
    <s:else>响应成功！</s:else>
    <br>
    <%
        Client client = (Client) application.getAttribute("client");
        Socket socket = client.socket;
        if (socket != null && !socket.isClosed()) {

//(client.connect((String) application.getAttribute("IP"),(int) application.getAttribute("port")))
            //树莓派端重启之后，页面刷新时不再执行此判断？
            //执行了，是因为除了isClose方法，Socket类还有一个isConnected方法来判断Socket对象是否连接成功。
            // 看到这个名字，也许读者会产生误解。其实isConnected方法所判断的并不是Socket对象的当前连接状态，
            // 而是Socket对象是否曾经连接成功过，如果成功连接过，即使现在isClose返回true，isConnected仍然返回true。
            try {
    %>树莓派IP：
    <%=socket.getInetAddress()%>
    <br>端口：
    <%=socket.getPort()%><br><p>

    文件：
    <% for (String s : client.getFileNames()) out.println(s); %>
    <br>
    存储状况：
    <%=client.getStorage()%>

    <form action="clear.action">
        <input value="点击清空" type="submit"/>
    </form><br><p>

    亮度：<s:property value="light"/>
    <form action="light.action">
        <input value="100" name="light" type="text"/>
        <input value="点击修改" type="submit"/>
    </form><br>

    灯珠数：<br>
    <form action="number.action">
        每列灯珠数：<input value="30" name="lineLength" type="text"/><br>
        列数：<input value="10" name="lineNum" type="text"/>
        <input value="点击修改" type="submit"/>
    </form><br><p>

    颜色：<br>
    <form action="color.action">
        (r,g,b)：<input value="(100,100,100)" name="color" type="text"/><br>
        <input value="点击修改" type="submit"/>
    </form><br><p>

    python灯条驱动：
    <form action="python.action">
        <input value="点击开启" type="submit"/>
    </form><br>


    <% } catch (NoResponseException e) {
        out.write("树莓派无响应");
        e.printStackTrace();
    }
    } else {
    %>
    <pre>未连接，点击重连</pre><br>
    <form action="connect.action">
        <input type="text" value="<%=application.getAttribute("IP")%>" name="IP">
        <input type="text" value="<%=application.getAttribute("port")%>" name="port">
        <input type="submit" value="重连">
    </form>
    <% }%>
</div>
</body>
</html>
