<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>主页</title>
    <style type="text/css">
        /*网盘内容的框*/
        .section1{
            font-family: Microsoft JhengHei, serif;font-size: 20px;border: 2px solid #dddddd;border-radius: 5px;width: 500px;height: 200px;padding-top: 30px;margin-top: 50px;padding-left: 100px;margin-left: 320px;
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
    <li style="margin-left: 150px"><a style="padding: 13px 100px;">音乐文件</a></li>
    <li><a href="indexUpload.jsp" target="_parent" style="padding: 13px 100px;">上传文件</a></li>
    <li><a style="padding: 13px 50px;" class="active" href="console.jsp" target="_parent">灯光控制</a></li>
</ul>
<div class="section1">

</div>
</body>
</html>
