<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title>播放</title>
</head>
<%
    String filename = (String) request.getAttribute("filename");
%>
<script src="jquery3.4.1.js"></script>
<script>
    //js控制dom标签技术，document.hetElementById能够获取标签对象
    var audio = document.getElementById("audio");
    audio.muted = document.getElementById("mute");
    //如何监听audio产生的事件？当单击audio播放的按钮时，向PlayAction发送树莓派控制信息

    var file = audio.currentSrc;
    var process = audio.currentTime;
    var url = 'play.action';
    $.post(url, {"file": file, "currentTime": process});
    //js如何向action发送json数据?
    var jsonData = {
        "file": file,
        "currentTime": process,
        "paused": audio.paused,
        "ended": audio.ended,
        "loop": audio.loop,
        "volume": audio.volume
    };

    var data = JSON.stringify(jsonData);
    //通过ajax向PlayAction实时传递播放信息
    $.ajax({
        async: false,
        url: url,
        data: data,
        success: function normal() {

        },
        error: function error() {

        }
    });

    /**
     * 构造form对象发出post请求的方法
     * @param URL
     * @param PARAMS
     * @returns {HTMLFormElement}
     */
    function post(URL, PARAMS) {
        var temp = document.createElement("form");
        temp.action = URL;
        temp.method = "post";
        temp.style.display = "none";
        for (var x in PARAMS) {
            var opt = document.createElement("textarea");
            opt.name = x;
            opt.value = PARAMS[x];
            // alert(opt.name)
            temp.appendChild(opt);
        }
        document.body.appendChild(temp);
        temp.submit();
        return temp;
    }
</script>
<body>

<form action="lastmusic.action" method="post" style="margin:0;display:inline;">
    <input type="hidden" value="<%=filename%>" name="filename"/>
    <input type="submit" value="上一首">
</form>

<form action="nextmusic.action" method="post">
    <input type="hidden" value="<%=filename%>" name="filename"/>
    <input type="submit" value="下一首" style="margin:0;display:inline;">
</form>
<form action="list.action" method="post" style="margin:0;display:inline;">
    <input type="submit" value="返回网盘">
</form>
手机是否静音？<input type="radio" id="mute"/>
音量 <!--做一个播放控件-->
<div id="volume">
     <!--定义滑块组件-->
     <input type="range" id="slider" min="0" max="1000" step="1" value="0" onchange="print()"/>
     <!--显示滑块组件的当前值-->
     <p>当前值为:<span id="print">50</span></p>
</div>
<audio id="audio" controls="controls" preload="auto" src="<%=filename%>" loop="loop"></audio>
</body>
</html>
