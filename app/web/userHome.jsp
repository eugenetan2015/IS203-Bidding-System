<%-- 
    Document   : userHome
    Created on : Sep 15, 2016, 8:44:02 PM
    Author     : Tan Ming Kwang
--%>

<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <%@include file="Head.jsp" %>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Welcome to BIOS</title>
    </head>
    <body>
        <div class='container'>
            <div class='row'>
                <div class='col-md-12'>
                    <h1>Welcome to BIOS!</h1>
                </div>
            </div>
            <br />
            <div class='row text-center'>
                <div class='col-md-4'>
                    <a href='viewCourses.jsp'>
                        <input type='image' width ='150' height='150' src='img/bid-icon.png' alt='Make a Bid Icon'> <br />
                    </a>    
                    <font size='5'>Make a Bid</font>
                </div>
                <div class='col-md-4'>
                    <a href='viewBidStatus.jsp'> 
                        <input type='image' width ='150' height='150' src='img/status-icon.png' alt='Make a Bid Icon'> <br />
                    </a>
                    <font size='5'>View Bid Status</font>
                </div>
                <div class='col-md-4'>
                    <form action='StudentTimeTableController' method='post'>
                        <input type='image' width ='150' height='150' src='img/class-icon.png' alt='View Classes Icon'> <br />
                    </form>
                    <font size='5'>View My Classes</font>
                </div>
            </div>

            <div class="panel-body"> <br/><br/> 
                <%                        Student current_student = (Student) session.getAttribute("user");

                    if (current_student != null) {
                        String student_id = (String) current_student.getStudentID();

                        BidDAO bidDao = new BidDAO();
                        CourseDAO courseDAO = new CourseDAO();
                        SectionDAO sectDAO = new SectionDAO();

                        String roundStatusStr = roundStatus[1];
                        if (roundStatus[0].equals("started") && roundStatus[1].equals("2")) {
                            roundStatusStr = "1";
                        }

                        ArrayList<Bid> allStudentBids = bidDao.viewStudentBids2(student_id, Integer.parseInt(roundStatusStr));

                        if (allStudentBids == null || allStudentBids.isEmpty()) {
                            out.println("<table class='table table-hover'>");
                            out.println("<tr>");
                            out.println("<td colspan='8'><h4 align='center'><b>Your Bidding Results for Round " + roundStatusStr + "</b></h4></td>");
                            out.println("<tr>");
                            out.println("<td align='center'><h4>You have not bidded for any course in Round " + roundStatusStr + "</h4></td>");
                            out.println("</tr>");
                            out.println("</table>");
                        } else {

                            for (Bid bid : allStudentBids) {
                                if (roundStatusStr.equals("1") && bid.getStatus().equals("Pending")) {
                                    out.println("<table class='table table-hover'>");
                                    out.println("<tr>");
                                    out.println("<td colspan='8'><h4 align='center'><b>Your Bidding Results for Round " + roundStatusStr + "</b></h4></td>");
                                    out.println("<tr>");
                                    out.println("<td></td>");
                                    //out.println("<td align='center'><h4>You have not bidded for any course in the previous round<h4></td>");
                                    out.println("</tr>");
                                    out.println("</table>");
                                    return;
                                }
                            }

                            out.println("<table class='table table-hover'>");
                            out.println("<tr>");
                            out.println("<td colspan='8'><h4 align='center'><b>Your Bidding Results for Round " + roundStatusStr + "</b></h4></td>");
                            out.println("</tr>");
                            out.println("<tr>");
                            out.println("<th></th>");
                            out.println("<th></th>");
                            out.println("<th>Course ID</th>");
                            out.println("<th>Course Title</th>");
                            out.println("<th>Section</th>");
                            out.println("<th>Bid Amount</th>");
                            out.println("<th>Status</th>");
                            out.println("</tr>");

                            DecimalFormat df = new DecimalFormat("0.00");
                            for (Bid bid : allStudentBids) {
                                String courseID = bid.getCourseID();
                                Course course = courseDAO.retrieveCourse(courseID);
                                String sectionID = bid.getSectionID();
                                Section sect = sectDAO.retrieve(courseID, sectionID);

                                out.println("<tr>");
                                out.println("<th></th>");
                                out.println("<th></th>");
                                out.println("<td>" + bid.getCourseID() + "</td>");
                                out.println("<td>" + course.getTitle() + "</td>");
                                out.println("<td><a href='viewSection.jsp?courseID=" + sect.getCourseID() + "&sectionID=" + sect.getSectionID() + "'>" + sect.getSectionID() + "</a></td>");
                                
                                out.println("<td>" + df.format(bid.getBidAmount()) + "</td>");
                                out.println("<td>" + bid.getStatus() + "</td>");
                                out.println("</tr>");
                            }
                            out.println("</table>");
                        }
                    }
                %>
            </div>
        </div>

    </body>
</html>
