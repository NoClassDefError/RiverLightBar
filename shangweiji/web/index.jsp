<%@ page import="java.util.HashMap" %>
<%@ page import="server.Client" %>
<%@ page import="java.net.Socket" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>主页</title>
    <link href="style.css" rel="stylesheet" type="text/css" />
</head>
<body>
<ul class="daohang">
    <li style="margin-top: 3px">
        <span style="font-size: 27px;color: #6666ee;font-family: Microsoft JhengHei,serif">绿色灯光河谷秀</span>
        <span style="font-size: 10px;color: #777">&emsp;版本 1.0 beta</span></li>
    <li style="margin-left: 150px"><a class="active" style="padding: 13px 100px;">音乐文件</a></li>
    <li><a href="indexUpload.jsp" target="_parent" style="padding: 13px 100px;">上传文件</a></li>
    <li><a style="padding: 13px 50px;" href="console.jsp" target="_parent">灯光控制</a></li>
</ul>

<%  HashMap map=(HashMap)request.getAttribute("files");
    //将file的路径输出出来
    String file = request.getAttribute("filepath").toString();
    if (!file.equals(application.getAttribute("root")))
        file = file.substring(0, file.lastIndexOf("\\"));
%>

树莓派状态：
<%
    Client client = (Client) application.getAttribute("client");
    Socket socket = client.socket;
    if(socket!=null&&!socket.isClosed()){
%>
    <%="已连接上"+socket.getInetAddress()%>
<%
    } else {
        out.write("未连接");
    }
%>
<div class="section1">

    <div>
        <p style="width:92%;float: left;clear: both;margin-top: 22px"><b>音乐文件</b></p><br>
        <form action="list.action" name="download" method="post">
            <!--在网盘1.3版本中，此处放置登录按钮 -->

            <input type="submit" value="查看">
        </form>
    </div>

    <hr>
    <div>
        <div align="left" style="float:left">
            <form action="open.action" method="post" style="margin:0;display:inline;">
                <input type="hidden" value="<%=file%>" name="filename">
                <input type="submit" value="返回上一级">
            </form>
        </div>
        <div>
            <form method="post" action="newfile.action">
                <input type="hidden" value="<%=file%>" name="filename"/>
                <input type="submit" value="新建歌单"/>
            </form>
        </div>
        <div><span style="font-size: 13px;">当前路径： <%=file%> </span></div>
    </div>

    <div class="section3">
        <!-- 遍历Map集合 -->
        <table border="1">
            <tr>
                <th>文件名</th>
                <th>位置</th>
                <th colspan="6">操作</th>
            </tr>
            <%--      fileMap的key储存文件名，value储存类型
                      tpath 是文件带有路径的全名--%>
            <c:forEach var="me" items="<%=map%>">
                <tr>
                    <c:set var="tpath" value="${filepath}\\${me.key}"/>
                    <td>
                            ${me.key}
                    </td>
                    <td>
                        <!--在此处显示文件的大小 -->
                        ${tpath}
                    </td>
                    <td>
                        <form method="post" action="download.action">
                            <input type="hidden" value="${tpath}" name="filename"/>
                            <input type="hidden" value="${tpath}" name="filepath"/>
                            <input class="submit1" type="submit" value="下载"/>
                        </form>
                    </td>
                    <!--音乐文件在手机端的预览功能 -->
                    <c:if test="${fn:contains(me.value,'audio')}">
                        <td>
                            <form method="post" action="enter.action">
                                <input type="hidden" value="${tpath}" name="filename"/>
                                <input class="submit1" type="submit" value="播放"/>
                            </form>
                        </td>
                    </c:if>
                    <c:if test="${fn:contains(me.value, 'image')||fn:contains(me.value, 'pdf')||fn:contains(me.value, 'text')||fn:contains(me.value, 'video')}">
                        <td>
                            <form method="post" action="stream.action">
                                <input type="hidden" value="${tpath}" name="filename"/>
                                <input class="submit1" type="submit" value="查看"/>
                            </form>
                        </td>
                    </c:if>
                    <td>
                        <form method="post" action="delete.action">
                            <input type="hidden" value="${tpath}" name="filename"/>
                            <input class="submit1" type="submit" value="删除"/>
                        </form>
                    </td>

                    <c:if test="${me.value eq 'file'}">
                        <td>
                            <form method="post" action="open.action">
                                <input type="hidden" value="${tpath}" name="filepath"/>
                                <input type="submit" class="submit1" value="打开目录"/>
                            </form>
                        </td>
                    </c:if>

                    <td>
                        <form action="rename.action" method="get"
                              style="margin:0;display:inline;">
                            <label>
                                <input type="text" value="注意输入后缀" name="newname"/>
                                <input value="${tpath}" type="hidden" name="fullname"/>
                                <input type="submit" class="submit1" value="重命名"/>
                            </label>
                        </form>
                    </td>
                    <td>
                        <form action="move.action" method="get"
                              style="display: inline;margin: 0">
                            <label>
                                <input type="text" value="粘贴地址到这里" name="location"/>
                                <input value="${tpath}" type="hidden" name="filename"/>
                                <input type="submit" class="submit1" value="移动"/>
                            </label>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
</div>
</body>
</html>