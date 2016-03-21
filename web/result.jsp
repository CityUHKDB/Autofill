<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>
        <title>FYP</title>
        <link rel="stylesheet" type="text/css" href="css/general.css">
    </head>
    <body>
        <%@include file="header.jsp" %>
        <div id='main'>
            <div class='container'>
                <iframe src='<%=(String)session.getAttribute("filepath")%>' width='800px' height='600px' >
            </div>
        </div>
    </body>
</html>
