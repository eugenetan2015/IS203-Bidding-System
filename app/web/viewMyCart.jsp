<%-- 
    Document   : viewMyCart
    Created on : Sep 25, 2016, 2:08:58 AM
    Author     : Eugene Tan
--%>
<%@page import="java.util.ArrayList"%>
<%@page import="bios.entity.Course"%>
<%@page import="bios.dao.CourseDAO"%>
<%@page import="bios.entity.ShoppingCartItem"%>
<%@page import="bios.entity.ShoppingCartItem"%>
<%@page import="bios.entity.ShoppingCart"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>View My Cart</title>
        <script>
            function validateNum(input) {
                if (input.value < 10)
                    input.value = 10;
            }

            function validateNum2(input, minBid) {
                if (input.value < minBid)
                    input.value = minBid;
            }
        </script>
    </head>
    <%@include file="Head.jsp" %>
    <body>
        <div class='container'>
            <div class="panel panel-default">
                <%     
                    if(roundStatus[0].equals("ended") && request.getAttribute("res") == null){
                        out.println("<div class='alert alert-danger' role='alert'>" + "Round has ended. You cannot check out." + "</div>");
                    }
                    
                    int leftPage = 0;
                    if (request.getAttribute("res") != null && (boolean) request.getAttribute("res") == true) {
                        out.print("<div class='alert alert-success' role='alert'>" + (String) request.getAttribute("cid") + " " + (String) request.getAttribute("sid") + " dropped from Cart" + "</div>");
                    }
                    if (request.getAttribute("BA") == null && leftPage == 1) {
                        out.print("<div class='alert alert-danger' role='alert'>" + "Error: Please enter Bid amounts of more than $10.00" + "</div>");
                    } else if (request.getAttribute("BA") != null && (boolean) request.getAttribute("BA") == false) {
                        ArrayList<Bid> error = (ArrayList<Bid>) request.getAttribute("errorBids");
                        if (error != null) {
                            out.print("<div class='alert alert-danger' role='alert'>" + "Error: Bids already exist, please drop and re-bid:");
                            for (Bid b : error) {
                                out.println("<br />" + b.getCourseID());
                            }
                            out.println("</div>");
                        }
                    } else if (request.getAttribute("deductFailed") != null) {
                        out.println("<div class='alert alert-danger' role='alert'>" + "Error: Not enough eDollars in balance" + "</div>");
                    }

                %>   
                <div class="panel-heading"><h3>Shopping Cart:</h3></div>
                <div class="panel-body">
                    <form name="CheckOutCartController" action="CheckOutCart" method = "post">
                        <%                            ShoppingCart shopCart = new ShoppingCart();
                            Student current_User = (Student) session.getAttribute("user");
                            if ((shopCart != null) && (current_User != null)) {
                                ArrayList<Section> sList = shopCart.displayShoppingCartItems((String) current_User.getStudentID());

                                if (sList != null && sList.size() == 0) {
                                    out.println("No Courses In Bidding Cart");
                                    return;
                                }

                                out.println("<table class='table table-hover'>");
                                out.println("<tr>");
                                out.println("<th></th>");
                                out.println("<th>Course ID</th>");
                                out.println("<th>Title</th>");
                                out.println("<th>School</th>");
                                out.println("<th>Section</th>");
                                out.println("<th>Instructor</th>");
                                out.println("<th>Day</th>");
                                out.println("<th>Start Time</th>");
                                out.println("<th>End Time</th>");
                                if (roundStatus[0].equals("started")) {
                                    out.println("<th>Bid Amount</th>");
                                }

                                out.println("</tr>");

                                int n = 1;

                                for (Section sci : sList) {
                                    String course_id = sci.getCourseID();
                                    CourseDAO courseDAO = new CourseDAO();
                                    Course course = courseDAO.retrieveCourse(course_id);
                                    String title = course.getTitle();
                                    String school = course.getSchool();
                                    out.println("<tr>");
                                    out.println("<td>");
                                    out.println("<a href='DeleteFromCartController?courseID=" + sci.getCourseID() + "&sectionID=" + sci.getSectionID() + "'>");
                                    out.println("<span class='glyphicon glyphicon-minus'></span>");
                                    out.println("</td>");
                                    out.println("<td>" + sci.getCourseID() + "</td>");
                                    out.println("<td>" + title + "</td>");
                                    out.println("<td>" + school + "</td>");
                                    out.println("<td>");
                                    out.println("<a href='viewSection.jsp?courseID=" + course_id + "&sectionID=" + sci.getSectionID() + "&vacancy=" + sci.checkVacancy() + "'>" + sci.getSectionID() + "</a>");
                                    out.println("</td>");
                                    out.println("<td>" + sci.getInstructor() + "</td>");
                                    out.println("<td>" + sci.getDay() + "</td>");
                                    out.println("<td>" + sci.getStartDate() + "</td>");
                                    out.println("<td>" + sci.getEndDate() + "</td>");
                                    if (roundStatus[0].equals("started")) {
                                        if (roundStatus[1].equals("1")) {
                                            out.println("<td><div class='col-xs-5'><input class='form-control' value='10.00' maxlength = '5' name='bidDollars'" + " type='number' step='0.01' min = '10.00' onchange='validateNum(this)'></div></td>");
                                        } else {
                                            double minBid = bidDAO.calculateMinBid(sci);
                                            out.println("<td><div class='col-xs-5'><input class='form-control' value='" + minBid + "' maxlength = '5' name='bidDollars'" + " type='number' step='0.01' min = '" + minBid + "' onchange='validateNum2(this, " + minBid + ")'></div></td>");
                                        }
                                        out.println("</tr>");
                                        n++;
                                    } else {
                                        out.println("</tr>");
                                        n++;
                                    }
                                }
                                //out.println("<input type='hidden' name='username' id='username' value='" + current_User.getStudentID() + "' />");

                                out.println("<tr>");
                                for (int i = 0; i < 9; i++) {
                                    out.println("<td></td>");
                                }
                                if (roundStatus[0].equals("started")) {
                                    out.println("<td><input type='submit' class='btn btn-default ' value='Checkout' onclick=\"return confirm('Are you sure you want to checkout?')\" /> <br /> <br /></td></tr>");
                                }

                                if (request.getAttribute("error") != null) {
                                    out.print("<div class='alert alert-danger' role='alert'>" + "Bids could not be checked out" + request.getAttribute("error") + "</div>");
                                }

                            }
                        %>
                    </form>
                </div>
            </div>
        </div>
</body>
</html>
