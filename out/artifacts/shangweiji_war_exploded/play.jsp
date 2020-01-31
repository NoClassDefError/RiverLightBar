<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title>播放</title>
</head>
<%
    String filename = (String) session.getAttribute("listening");
%>
<script src="jquery3.4.1.js"></script>
<script>
    window.onload = function () {
        $(":radio").click(function () {
            var r = $(this).attr("name");
            $(":radio[name=" + r + "]:not(:checked)").attr("tag", 0);
            if ($(this).attr("tag") == 1) {
                $(this).attr("checked", false);
                $(this).attr("tag", 0);
            }
            else {
                $(this).attr("tag",1);
            }
        });
        //js控制dom标签技术，document.hetElementById能够获取标签对象
        var audio = document.getElementById('audio');

        // window.alert(audio);
        //为播放器添加监听器,随时向树莓派传递信息
        audio.addEventListener("play", function playListener() {
            //如何监听audio产生的事件？当单击audio播放的按钮时，向PlayAction发送树莓派控制信息
            console.log("played");
            sendAll('pla.action', audio);
        });
        audio.addEventListener("pause", function () {
            sendAll('pla.action', audio);
        });
        audio.addEventListener("seeked", function () {
            sendAll('pla.action', audio);
        });
        document.getElementById("mute").addEventListener("click", function () {
            console.log(document.getElementById("mute").input);
            audio.muted = document.getElementById("mute").input;
        });

        var info = '<%=request.getParameter("info")%>';
        if (info !== '') window.alert(info);
    };


    // var url = 'play.action';
    // $.post(url, {"file": file, "currentTime": process});

    function sendAll(url, audio) {
        //js如何向action发送json数据?
        var jsonData = {
            "file": audio.currentSrc,
            "currentTime": audio.currentTime,
            "paused": audio.paused,
            "ended": audio.ended,
            "loop": audio.loop,
            "volume": audio.volume
        };
        var data = JSON.stringify(jsonData);
        //通过ajax向PlayAction实时传递播放信息
        console.log("ajax sended: " + data);
        $.ajax({
            type: "POST",
            contentType: "application/json",
            async: true,
            url: url,
            data: data,
            dataType: "json",
            success: function normal() {
                console.log("ajax success");
            },
            error: function error() {
                console.log("ajax error");
            }
        });
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
<h5><%=filename%>
</h5>
是否仅在树莓派上播放？<input type="radio" id="mute"/><br>

<%--注意此处不能使用直接物理路径！--%>
<audio id="audio" controls="controls" preload="auto"
       src="<%="/root\\"+filename.substring(filename.lastIndexOf("\\")+1)%>" loop="loop"></audio>
</body>
</html>
