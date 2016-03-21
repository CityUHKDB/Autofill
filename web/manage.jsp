<%@page import="Autofill.PDFForm"%>
<%@page import="java.util.ArrayList"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>
        <title>FYP</title>
        <script src='js/manage.js'></script>
        <script src='http://code.jquery.com/jquery-latest.js'></script>
        <script src="http://code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
        <link rel="stylesheet" type="text/css" href="css/general.css">
        <link rel="stylesheet" href="http://code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
    </head>
    <body onload='pageLoad()'>
        <%@include file="header.jsp" %>
        <div id='main'>
            <div class='container'>
                <div id='tabArea'>
                    <ul>
                        <li><a href='#userTab'>User Account</a></li>
                        <li><a href='#formTab'>Form</a></li>
                    </ul>
                    <div id='userTab'>
                        <table>
                            <tr>
                                <th>Username</th>
                                <th>Action</th>
                            </tr>
                            <%
                                ArrayList<User> userList = (ArrayList<User>)session.getAttribute("userList");
                                for (User account : userList) {
                            %>
                            <form action='?page=manage&action=deleteAccount' method='POST'>
                                <tr>
                                    <td>
                                        <%=account.getUsername()%>
                                    </td>
                                    <td>
                                            <input type='hidden' name='userName' value='<%=account.getUsername()%>' />
                                            <button type='submit'>Delete</button>
                                    </td>                            
                                </tr>
                            </form>
                            <%
                                }
                            %>
                        </table>                        
                    </div>
                    <div id='formTab'>
                        <table>
                            <tr>
                                <th>Form Name</th>
                                <th>Description</th>
                                <th>Action</th>
                            </tr>
                            <%
                                ArrayList<PDFForm> formList = (ArrayList<PDFForm>)session.getAttribute("formList");
                                for (PDFForm form : formList) {
                            %>
                            <form action='?page=manage&action=deleteForm' method='POST'>
                                <tr>
                                    <td>
                                        <%=form.getName()%>
                                    </td>
                                    <td>
                                        <%=form.getDescription()%>
                                    </td>
                                    <td>
                                            <input type='hidden' name='formName' value='<%=form.getName()%>' />
                                            <button type='submit'>Delete</button>
                                    </td>
                                </tr>                                    
                            </form>
                            <%
                                }
                            %>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>