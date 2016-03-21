<%@page import="Autofill.AcroFormField"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <script>
            var data = JSON.parse('${user.data}');  // Decode JSON
        </script>
        <script src='js/process.js'></script>
        <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>
        <title>FYP</title>
        <link rel="stylesheet" type="text/css" href="css/general.css">
    </head>
    <body onload='pageLoad()'>
        <%@include file="header.jsp" %>
        <div id='main'>
            <div class='container'>
                <%
                    ArrayList<AcroFormField> fields = (ArrayList<AcroFormField>)(session.getAttribute("fields"));
                %>
                <form method='POST' action='?page=result'>
                    <table>
                        <tr>
                            <th>Field Label</th>
                            <th>Field Value</th>
                            <th>Modify</th>
                        </tr>
                        <%  
                            String preGroup = "";
                            for (AcroFormField field : fields) {
                                if (field.getFieldLabel() != null && field.getFieldLabel().length() != 0) { 
                                    if (field.getGroup()!= null && !field.getGroup().equals(preGroup)) {     
                                        preGroup = field.getGroup();
                        %>
                        <tr>
                            <td colspan='3'><%=field.getGroup()%></td>
                        </tr>
                        <%
                                    }
                        %>
                        <tr>
                            <td><%=field.getFieldLabel()%></td>
                            <td><input type='text' name='<%=field.getFieldName()%>' value='<%=field.getFieldValue()%>' readonly='readonly' /></td>
                            <td>
                                <select name='<%=field.getFieldName() + "_option" %>' class='personalFieldList'>
                                    <option value='' ></option>
                                </select>
                                <input type ='hidden' name='<%=field.getFieldName() + "_personalFieldName" %>' value='<%=field.getPersonalFieldName() %>' />
                            </td>
                        </tr>
                        <%
                                }
                            }
                        %>
                        <tr>
                            <td colspan='3'>
                                <input type='submit' value='Confirm' />
                            </td>
                        </tr>
                    </table>
                </form>
            </div>
        </div>
    </body>
</html>
