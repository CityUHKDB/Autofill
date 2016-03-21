<%@page import="Autofill.PDFForm"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>
        <title>FYP</title>
        <script src='js/fill.js'></script>
        <script src='http://code.jquery.com/jquery-latest.js'></script>
        <link rel="stylesheet" type="text/css" href="css/general.css">
    </head>
    <body onload='pageLoad()'>
        <%@include file="header.jsp" %>
        <div id='main'>
            <div class='container'>
                <table>
                    <tr>
                        <th>Form:</th>
                        <td>
                            <select id='formList' onchange='formListChanged()'>
                                <%
                                    ArrayList<PDFForm> formList = (ArrayList<PDFForm>)session.getAttribute("formList");
                                    for (PDFForm form : formList) {
                                %>
                                <option value="<%= form.getName() %>"><%= form.getDescription() %></option>
                                <%
                                    }
                                %>
                                <option value='other'>Other</option>
                            </select>
                            <div id='uploadBox'>
                                <form method='POST' action='?page=process' enctype="multipart/form-data">
                                    <input type='file' name='uploadFile' />
                                    <input type='submit' value='Fill' />
                                </form>
                            </div>
                            <div id='existingBox'>
                                <form method='POST' action='?page=process'>
                                    <input type='hidden' id='formPath' name='formPath' value=''>
                                    <input type='submit' value='Fill' />
                                </form>
                            </div>
                        </td>
                    </tr>		
                </table>
            </div>
        </div>
    </body>
</html>
