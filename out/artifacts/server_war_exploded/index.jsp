<%@ page import="java.util.HashMap" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>主页</title>
    <style type="text/css">
        /*网盘内容的框*/
        .section1 {
            font-family: Microsoft JhengHei, serif;
            font-size: 18px;
            border: 2px solid #dddddd;
            border-radius: 5px;
            padding-left: 30px;
            padding-right: 30px;
            padding-bottom: 10px;
            margin-left: 50px;
            margin-top: 15px;
            padding-top: 1px;
            width: 85%;
            height: 83%;
        }

        /*  本来是注意的外框，后来弃用
        .section2{
            border: 2px solid #dddddd;border-radius: 5px;width: 400px;height: 400px;margin-top: 15px;padding-left: 30px;padding-right: 30px;padding-bottom: 10px;margin-left: 50px;padding-top: 5px;margin-left: 750px;overflow-y: scroll;color: #111;
        }   */

        /*网盘内容里文件展开的框*/
        .section3 {
            border: 2px solid #f3f3f3;
            border-radius: 5px;
            overflow: scroll;
            height: 70%
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

        .submit1 {
            background-color: #fff; /* Green */
            border: none;
            color: white;
            padding: 3px 8px;
            text-align: center;
            text-decoration: none;
            display: inline-block;
            font-size: 12px;
            margin: 4px 2px;
            -webkit-transition-duration: 0.4s; /* Safari */
            transition-duration: 0.4s;
            cursor: pointer;
        }

        .submit1:hover {
            background-color: #777799;
            color: white;
        }
    </style>

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
                <input type="hidden" value="<%=file%>" name="filepath">
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
                <th>大小</th>
                <th colspan="6">操作</th>
            </tr>
            <%--      fileMap的key储存文件名，value储存类型
                      tpath 是文件带有路径的全名--%>
            <c:forEach var="me" items="<%=map%>">
                <tr>
                    <c:set var="tpath" value="<%=file%>\\${me.key}"/>
                        <%--此处使用url标签为超链接添加get请求--%>
                    <td>
                            ${me.key}
                    </td>
                    <td>
                        <!--在此处显示文件的大小 -->

                    </td>
                    <td>
                        <form method="post" action="download.action">
                            <input type="hidden" value="${tpath}" name="filepath"/>
                            <input class="submit1" type="submit" value="下载"/>
                        </form>
                    </td>
                    <!--音乐文件在手机端的预览功能 -->
                    <c:if test="${fn:contains(me.value,'music')||fn:contains(me.value, 'image')||fn:contains(me.value, 'pdf')||fn:contains(me.value, 'text')}">
                        <td>
                            <form method="post" action="play.action">
                                <input type="hidden" value="${tpath}" name="filename"/>
                                    <%--没必要向PlayAction传输files，PlayAction应当根据session中的当前文件目录自动找files--%>
                                    <%--<c:forEach var="file" items="${fileMap}">--%>
                                    <%--    <input type="hidden" value="${file.key}" name="files"/>--%>
                                    <%--</c:forEach>--%>
                                <input class="submit1" type="submit" value="播放"/>
                            </form>
                        </td>
                    </c:if>
                    <td>
                        <form method="post" action="delete.action">
                            <input type="hidden" value="${tpath}" name="filepath"/>
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
                                <input value="${tpath}" type="hidden" name="filepath"/>
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