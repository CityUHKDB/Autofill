<%@page import="Autofill.Statistics"%>
<%@page import="java.util.ArrayList"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>
        <title>FYP</title>

        <script>
            var statistics = new Array();
            var tempStat;
            <%
                ArrayList<Statistics> statistics = (ArrayList<Statistics>)session.getAttribute("statistics");
                for (int i=0; i<statistics.size(); i ++) {
            %>  
            tempStat = {'standard': '<%=statistics.get(i).getStandard()%>', 'synonym': '<%=statistics.get(i).getSynonym()%>', 'history': '<%=statistics.get(i).getHistory()%>'};
            //tempStat.standard = "<%=statistics.get(i).getStandard()%>";
            //tempStat.synonym = "<%=statistics.get(i).getSynonym()%>";
            //tempStat.history = "<%=statistics.get(i).getHistory()%>";
            statistics.push(tempStat);
            <%
                }
            %>
        </script>
        
        <script src='http://code.jquery.com/jquery-latest.js'></script>
        <script src="http://code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
        <script src="https://www.gstatic.com/charts/loader.js"></script>
        <script src='js/statistics.js'></script>
        <link rel="stylesheet" type="text/css" href="css/general.css">
        <link rel="stylesheet" href="http://code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
    </head>
    <body onload='pageLoad()'>
        <%@include file="header.jsp" %>
        <div id='main'>
            <div class='container'>
                <select id="synonymPair" onchange="displayStatistics()">
                    <%
                        for (int i=0; i<statistics.size(); i++) {
                            Statistics s = statistics.get(i);
                    %>
                    <option value="<%=i%>"><%=s.getStandard() + " - " + s.getSynonym()%></option>
                    <%
                        }
                    %>
                </select> 
                <div id='tabArea'>
                    <ul>
                        <li><a href='#lineChartTab'>Line Chart</a></li>
                        <li><a href='#historyTab'>History</a></li>
                    </ul>
                    <div id='lineChartTab'>
                        <div id='chart'></div>   
                    </div>
                    <div id='historyTab'>
                        <table id='historyTable'></table>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>