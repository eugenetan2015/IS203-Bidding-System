<%-- 
    Document   : Head
    Created on : Sep 15, 2016, 7:03:25 PM
    Author     : Tan Ming Kwang
--%>

<%@page import="java.text.DecimalFormat"%>
<%@page import="bios.dao.*"%>
<%@page import="bios.entity.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
        <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
        <style>
            /* Remove the navbar's default margin-bottom and rounded borders */
            .navbar {
                margin-bottom: 0;
                border-radius: 0;
            }

            /* Set height of the grid so .sidenav can be 100% (adjust as needed) */
            .row.content {height: 450px}

            /* Set gray background color and 100% height */
            .sidenav {
                padding-top: 20px;
                background-color: #f1f1f1;
                height: 100%;
            }

            /* Set black background color, white text and some padding */
            footer {
                background-color: #555;
                color: white;
                padding: 15px;
            }

            /* On small screens, set height to 'auto' for sidenav and grid */
            @media screen and (max-width: 767px) {
                .sidenav {
                    height: auto;
                    padding: 15px;
                }
                .row.content {height:auto;}
            }
        </style>
        <%@include file="protect.jsp"%>
        <%            
            Student user = (Student) session.getAttribute("user");
            BidDAO bidDAO = new BidDAO();
            String[] roundStatus = bidDAO.getCurrentBidRound();
            if (user != null) {
                StudentDAO studentDAO = new StudentDAO();
                user = studentDAO.retrieveStudent(user.getStudentID());
            }
        %>
    </head>
    <body>

        <nav class="navbar navbar-inverse">
            <div class="container-fluid">
                <div class="navbar-header">
                    <a class="navbar-brand" href="userHome.jsp">BIOS</a>
                </div>
                <div class="collapse navbar-collapse" id="myNavbar">
                    <ul class="nav navbar-nav">
                        <li><a href="userHome.jsp">Home</a></li>
                        <li><a href="viewCourses.jsp">Make a Bid</a></li>
                        <li><a href="viewBidStatus.jsp">View Bid Status</a></li> 
                        <li><a href="StudentTimeTableController">View My Classes</a></li>
                    </ul>
                    <ul class="nav navbar-nav navbar-right">
                        <li><a>Current Round: <%=roundStatus[1]%> (<%=roundStatus[0]%>)</a></li>
                        <li><a href="LogoutController"><span class="glyphicon glyphicon-log-out"></span> Log out</a></li>
                    </ul>
                </div>
                <div class="pull-right">
                    <%
                        if (user != null) {
                            DecimalFormat df = new DecimalFormat("$0.00");
                            String amt = df.format(user.geteDollars());
                            out.print("<font color='grey'>Welcome " + user.getName() + " (e" + amt + ")" + "</font>");
                        }
                    %>

                    <a href="viewMyCart.jsp" style="color:grey">
                        <span class="glyphicon glyphicon-shopping-cart"></span> View my cart 
                    </a>
                </div>
            </div>
        </nav>

    </body>
</html>
