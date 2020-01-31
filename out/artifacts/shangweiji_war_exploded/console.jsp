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
    <%
        Client client = (Client) application.getAttribute("client");
        Socket socket = client.socket;
        if (socket!=null&&socket.isConnected()) {
            try {
    %>树莓派IP：
    <%=socket.getInetAddress()%>
    <br>端口：
    <%=socket.getPort()%><br>
    文件：
    <% for(String s:client.getFileNames()) out.println(s); %>
    <br>
    存储状况：
    <%=client.getStorage()%>
    点击清空
    <form action="connect.action">

    </form>
    <% } catch (NoResponseException e) {
        e.printStackTrace();
    }
    } else {%>
    未连接，点击重连
    <form action="connect.action">
        <input type="text" value="localhost" name="IP">
        <input type="text" value="1234" name="port">
        <input type="submit" value="重连">
    </form>
    <% }%>
</div>
</body>
</html>
