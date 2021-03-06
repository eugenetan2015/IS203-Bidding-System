<%-- 
    Document   : Head
    Created on : Sep 15, 2016, 7:03:25 PM
    Author     : Tan Ming Kwang
--%>

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
        <%@include file="protect_admin.jsp"%>
        <%  
            Student user = (Student) session.getAttribute("user");
        %>
    </head>
    <body>

        <nav class="navbar navbar-inverse">
            <div class="container-fluid">
                <div class="navbar-header">
                    <a class="navbar-brand" href="adminHome.jsp">BIOS</a>
                </div>
                <div class="collapse navbar-collapse" id="myNavbar">
                    <ul class="nav navbar-nav">
                        <li><a href="adminHome.jsp">Home</a></li>
                        <li><a href="BootStrapUI.jsp">Bootstrap</a></li>
                    </ul>
                    <ul class="nav navbar-nav navbar-right">
                        <li><a href="LogoutController"><span class="glyphicon glyphicon-log-out"></span> Log out</a></li>
                    </ul>
                </div>
                <div class="text-right">
                    <font color='grey'>Welcome
                    
                    <% if (user != null) {
                        out.println(user.getName()); 
                        }
                    %>!</font>

                </div>
            </div>
        </nav>

    </body>
</html>
