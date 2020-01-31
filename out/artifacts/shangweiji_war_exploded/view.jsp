<%
    String mime = application.getMimeType(request.getParameter("filename"));
    System.out.println("view.jsp "+mime);
    response.setContentType(mime + ";" + "charset=UTF-8");
%>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>
    <meta content="<%=request.getParameter("filename")%>">

    <title>查看</title>
</head>
<body>

</body>
</html>
