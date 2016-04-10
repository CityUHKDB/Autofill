<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>
        <title>FYP</title>
        <script>
            // Decode JSON
            var data = JSON.parse('${user.data}')
            var group = JSON.parse('${user.group}');
        </script>
        <script src='js/record.js'></script>
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
                        <li><a href='#groupBox'>Group</a></li>
                        <li><a href='#dataBox'>Data</a></li>
                        <li><a href='#importBox'>Import</a></li>
                    </ul>
                    <div id='groupBox'>
                        <table id='groupTable'>
                            <tr>
                                <th>Group Name</th>
                                <th>Action</th>
                            </tr>
                            <tr id='inputGroupRow'>
                                <td><input type='text' id='groupName' /></td>
                                <td><button onclick='insertGroup()'>Insert</button></td>
                            </tr>
                        </table>
                        <br/>
                        <button onclick='saveGroup()'>Save Changes</button>
                    </div>
                    <div id='dataBox'>
                        <table id='dataTable'>
                            <tr>
                                <th>Data Type</th>
                                <th>Field Name</th>
                                <th>Value</th>
                                <th>Field Group</th>
                                <th>Action</th>
                            </tr>
                            <tr id='inputRow'>
                                <td>
                                    <select id='dataTypeList' onchange='listChanged()'>
                                        <option value='Text' selected>Text</option>
                                        <option value='Date'>Date</option>
                                    </select>
                                </td>
                                <td><input type='text' id='fieldName' /></td>
                                <td><input type='text' id='fieldValue' /></td>
                                <td>
                                    <select id='fieldGroup'>
                                        <option value='' selected></option>
                                    </select>
                                </td>
                                <td><button onclick='insertData()'>Insert</button></td>
                            </tr>
                        </table>
                        <br/>
                        <button onclick='saveData()'>Save Changes</button>
                    </div>
                    <div id='importBox'>
                        <table>
                            <tr>
                                <th>Import From PDF:</th>
                                <td>
                                    <form method='POST' action='?page=import' enctype="multipart/form-data">
                                        <input type='file' name='uploadFile' />
                                        <input type='submit' value='Import' />
                                    </form>
                                </td>
                            </tr>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
