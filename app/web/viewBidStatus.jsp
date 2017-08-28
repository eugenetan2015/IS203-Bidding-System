<%-- 
    Document   : viewBidStatus
    Created on : Sep 28, 2016, 6:318:56 PM
    Author     : Eugene Tan
--%>

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Scanner"%>
<%@page import="java.io.File"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <%@include file="Head.jsp" %>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <div class='container'>
            <div class="panel panel-default">
                <%                if (request.getAttribute("success") != null && (boolean) request.getAttribute("success") == true) {
                        out.print("<div class='alert alert-success' role='alert'>" + (String) request.getAttribute("course_id") + " " + (String) request.getAttribute("section_id") + " dropped successfully." + " Refund amt: $" + request.getAttribute("bidAmt") + "</div>");
                    }
                %>
                <div class="panel-heading"><h2>View Bid status:</h2></div>
                <div class="panel-body">
                    <br />

                    <div class="row col-md-12">
                        <%
                            Student current_student = (Student) session.getAttribute("user");
                            if (current_student != null) {
                                String student_id = (String) current_student.getStudentID();
                                //out.println(student_id); 
                                BidDAO bidDao = new BidDAO();
                                CourseDAO courseDao = new CourseDAO();
                                SectionDAO sectionDao = new SectionDAO();
                                ArrayList<Bid> allStudentBids = bidDao.viewStudentBids2(student_id, Integer.parseInt(roundStatus[1]));
                                if (allStudentBids == null || allStudentBids.isEmpty()) {
                                    out.println("<div class='text-left'>");
                                    out.println("You have no bids for this round. To drop your previous successful bids, go to <a href='viewMyClasses.jsp'>View My Classes</a>");
                                    out.println("</div>");
                                } else {
                                    if (roundStatus[1].equals("2") && roundStatus[0].equals("started")) {
                                        out.println("<b>Applicable only for Round 2</b><p/><p/>");
                                        out.println("Current standing with a tick - You won the bid<p/>");
                                        out.println("Current standing with a cross - You lose the bid<p/>");
                                    } else if (roundStatus[1].equals("1") && roundStatus[0].equals("ended")) {
                                        out.println("<div class='text-left'>");
                                        out.println("You have no bids");
                                        out.println("</div>");
                                        return;
                                    }
                                    out.println("<b>How to use?</b><p/><p/>");
                                    out.println("Press (-) button to drop a bid.");
                                    out.println("<table class='table table-hover'>");
                                    out.println("<tr>");
                                    out.println("<th></th>");
                                    out.println("<th></th>");
                                    out.println("<th>Course ID</th>");
                                    out.println("<th>Title</th>");
                                    out.println("<th>School</th>");
                                    out.println("<th>Section</th>");
                                    out.println("<th>Instructor</th>");
                                    out.println("<th>Day</th>");
                                    out.println("<th>Start Time</th>");
                                    out.println("<th>End Time</th>");
                                    if (roundStatus[1].equals("2") && roundStatus[0].equals("started")) {
                                        out.println("<th>Bid (Min)</th>");
                                        out.println("<th>Vacancy</th>");
                                        out.println("<th>Current Standing</th>");
                                    } else {
                                        out.println("<th>Bid Amount</th>");
                                    }
                                    out.println("<th>Result</th>");
                                    out.println("</tr>");

                                    for (Bid b : allStudentBids) {
                                        Section s = sectionDao.retrieve(b.getCourseID(), b.getSectionID());
                                        int vacancy = s.checkVacancy() - sectionDao.retrieveNumOfFilledSections(s.getCourseID(), s.getSectionID());
                                        double minAmount = 10.00;
                                        if (roundStatus[0].equals("started") && roundStatus[1].equals("2")) {
                                            minAmount = bidDao.calculateMinBid(s);
                                        }

                                        out.println("<tr>");
                                        Course c_obj = courseDao.retrieveCourse(b.getCourseID());
                                        Section sec_obj = sectionDao.retrieve(b.getCourseID(), b.getSectionID());
                                        if (c_obj == null || sec_obj == null) {
                                            out.println("error");
                                            return;
                                        }
                                        out.println("<th></th>");
                                        if (roundStatus[0].equals("started")) {
                                            out.println("<td>");
                                            out.println("<a href='DropBidController?CourseID=" + b.getCourseID() + "'>");
                                            out.println("<span class='glyphicon glyphicon-minus'></span>");
                                            out.println("</td>");
                                        } else {
                                            out.println("<td></td>");
                                        }
                                        out.println("<td>" + b.getCourseID() + "</td>");
                                        out.println("<td>" + c_obj.getTitle() + "</td>");
                                        out.println("<td>" + c_obj.getSchool() + "</td>");
                                        out.println("<td>" + b.getSectionID() + "</td>");
                                        out.println("<td>" + sec_obj.getInstructor() + "</td>");
                                        out.println("<td>" + sec_obj.getDay() + "</td>");
                                        out.println("<td>" + sec_obj.getStartDate() + "</td>");
                                        out.println("<td>" + sec_obj.getEndDate() + "</td>");
                                        if (roundStatus[1].equals("2") && roundStatus[0].equals("started")) {
                                            out.println("<td>" + b.getBidAmount() + " (" + minAmount + ")" + "</td>");
                                        } else {
                                            out.println("<td>" + b.getBidAmount() + "</td>");
                                        }
                                        if (roundStatus[1].equals("2") && roundStatus[0].equals("started")) {
                                            out.println("<td>" + s.checkVacancy() + "</td>");
                                            if (bidDao.stillInRange(b, s)) {
                                                out.println("<td class='text-center'><span class='glyphicon glyphicon-ok'></span></td>");
                                            } else {
                                                out.println("<td class='text-center'><span class='glyphicon glyphicon-remove'></span></td>");
                                            }
                                        }

                                        out.println("<td>" + b.getStatus() + "</td>");
                                        out.println("</tr>");
                                    }
                                }
                            }
                        %>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
