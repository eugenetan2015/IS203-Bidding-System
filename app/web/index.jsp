<%-- 
    Document   : index
    Created on : Sep 15, 2016, 7:38:50 PM
    Author     : Tan Ming Kwang
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <%@include file="Head_Login.jsp" %>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Log In</title>
        <style>
            .content {
                position: absolute;
                top: 50%;
                left:50%;
                transform: translate(-50%,-50%);
            }
        </style>
    </head>
    <body style="background-color:#EBEDEF;">
        <div class="center-block" style="height:600px;width:800px;background-color:#FFFFFF;">
            <div class="content">
                <%
                    if (request.getSession(false) != null) {
                        session.invalidate();
                    }
                    
                    String logoutMsg = (String) request.getAttribute("logoutMsg");
                    if (logoutMsg != null) {
                        out.print("<div class='alert alert-success' role='alert'>" + logoutMsg + "</div>");
                    }

                    String errorMsg = (String) request.getAttribute("errorMsg");
                    if (errorMsg != null) {
                        out.print("<div class='alert alert-danger' role='alert'>" + errorMsg + "</div>");
                    }
                %>
                <h2>Log In:</h2>
                <br />
                <form class="form-horizontal" name="login-form" action="LoginController" method="post">

                    <%
                        String username = (String) request.getAttribute("user");
                        if (username == null) {
                            out.println("<div class='form-group'>");
                            out.println("<label for='usr' class='col-sm-3 control-label'>Username:</label>");
                            out.println("<div class='col-sm-9'>");
                            // TESTING PURPOSE
                            out.println("<input type='text' class='form-control' name='username' autofocus='true' placeholder='Username' id='usr'>");
                            out.println("</div>");
                            out.println("</div>");

                            out.println("<div class='form-group'>");
                            out.println("<label for='pwd' class='col-sm-3 control-label'>Password:</label>");
                            out.println("<div class='col-sm-9'>");
                            
                            // TESTING PURPOSE
                            out.println("<input type='password' class='form-control' name='password' placeholder='Password' id='pwd'>");
                            //out.println("<input type='password' class='form-control' name='password' placeholder='Password' id='pwd' >");
                            out.println("</div>");
                            out.println("</div>");
                            //out.println("<p>Username: <input type='text' class='text-info' name='username' autofocus='true'> </p>");
                            //out.println("<p>Password: <input type='password' class='text-info' name='password'> </p>");
                        } else {
                            //out.println("<p>Username: <input type='text' class='text-info' name='username'" + " value='" + username + "'> </p>");
                            //out.println("<p>Password: <input type='password' class='text-info' name='password' autofocus='true'> </p>");
                            out.println("<div class='form-group'>");
                            out.println("<label for='usr' class='col-sm-3 control-label'>Username:</label>");
                            out.println("<div class='col-sm-9'>");
                            out.println("<input type='text' class='form-control' name='username' placeholder='Username' id='usr' value='" + username + "'>");
                            out.println("</div>");
                            out.println("</div>");

                            out.println("<div class='form-group'>");
                            out.println("<label for='pwd' class='col-sm-3 control-label'>Password:</label>");
                            out.println("<div class='col-sm-9'>");
                            out.println("<input type='password' class='form-control' name='password' autofocus='true' placeholder='Password' id='pwd'>");
                            out.println("</div>");
                            out.println("</div>");
                        }
                    %>
                    <input type='submit' class='btn btn-default pull-right' value="Login" /> <br /> <br />
                </form>
            </div>
        </div> 
    </body>
</html>
