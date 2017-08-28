<%-- 
    Document   : adminHome
    Created on : Sep 19, 2016, 8:26:06 AM
    Author     : Tan Ming Kwang
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%@include file="Head_Admin.jsp" %>
        <title>Welcome to BIOS</title>
        <style>
            .content {
                position: absolute;
                top: 30%;
            }
        </style>
        <%            BidDAO bidDAO = new BidDAO();
            String[] roundStatus = bidDAO.getCurrentBidRound();
        %>
    </head>
    <body>
        <div class='container'>
            <div class='row'>
                <%
                    String successMsg = (String) request.getAttribute("successMsg");
                    if (successMsg != null) {
                        out.print("<div class='alert alert-success' role='alert'>" + successMsg + "</div>");
                    }
                %>
                <div class='col-md-12'>
                    <h1>Welcome to BIOS!</h1>
                </div>
            </div>
            <br />
            <div class='row text-center'>
                <h2>Current round: <%=roundStatus[1]%> (<%=roundStatus[0]%>)</h2>
            </div>    
            <div class='row text-center'>
                <div class='col-md-6'>
                    <a href='BootStrapUI.jsp'>
                        <input type='image' width ='150' height='150' src='img/upload-icon.png' alt='Bootstrap'> <br />
                    </a>
                    <font size='5'>Bootstrap</font>
                </div>
                <% if (roundStatus[0].equals("ended")) { %>
                <div class='col-md-6'>
                    <a href='StartRoundController' onclick="return confirm('Are you sure you want to start the round?')">
                        <input type='image' width ='150' height='150' src='img/start-icon.png' alt='Start Round'> <br />
                    </a>
                    <font size='5'>Start a Round</font>
                </div>
                <% } else { %>
                <div class='col-md-6'>
                    <a href='EndRoundController' onclick="return confirm('Are you sure you want to end the round?')">
                        <input type='image' width ='150' height='150' src='img/stop-icon.png' alt='Stop Round'> <br />
                    </a>
                    <font size='5'>Stop a Round</font>
                </div>
                <% }%>
            </div>
        </div>
    </body>
</html>
