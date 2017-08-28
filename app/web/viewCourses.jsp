<%-- 
    Document   : viewBids
    Created on : Sep 20, 2016, 9:12:21 PM
    Author     : Tan Ming Kwang
--%>

<%@page import="java.util.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <%@include file="Head.jsp" %>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>View all Courses</title>
    </head>
    <body>
        <div class='container'>
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h2>Choose a section:</h2>
                    <%                        ShoppingCartItem addedItem = (ShoppingCartItem) request.getAttribute("addItem");
                        ArrayList<String> errorMsgs = (ArrayList<String>) request.getAttribute("errorMsgs");
                        String errorMsg = (String) request.getAttribute("errorMsg");
                        if (addedItem != null) {
                            out.print("<div class='alert alert-success' role='alert'>" + addedItem.getSection().getCourseID() + " " + addedItem.getSection().getSectionID() + " added to Cart" + "</div>");
                        }
                        if (errorMsgs != null) {
                            if (errorMsgs.size() > 0) {
                                out.println("<div class='alert alert-danger' role='alert'>");
                                out.println(errorMsgs.get(0) + "<br />");
                                out.println("</div>");
                            }
                        }
                        //out.println(session.getAttribute("userBid")); 
                        Student stu = (Student) session.getAttribute("user");

                        if (errorMsg != null) {
                            out.print("<div class='alert alert-danger' role='alert'>" + errorMsg + "</div>");
                        }

                    %>  
                </div>
                <div class="panel-body">
                    <b>How to use?</b>
                    <p/>
                    <p/>Step 1: Press (+) button to add course(s) to Bidding Cart
                    <p/>Step 2: After you have added all the course, click on View My Cart function (top right hand corner)
                    <div class='row'>
                        <br />
                        <form action="searchController" method="get">
                            <div class="form-group col-md-2 col-md-offset-3">
                                <select class="form-control" name="searchOption">
                                    <option value="coursecode">Course Code</option>
                                    <option value="coursename">Course Name</option>
                                </select>   
                            </div>
                            <div class="form-group col-md-3">
                                <input type="text" class="form-control" placeholder="Search" name="searchBox">
                            </div>
                            <button type="submit" class="btn btn-default">Search</button>
                        </form>
                    </div>
                    <form action="AddToCartController" method="post">
                        <div class="row col-md-12">
                            <%                                BidDAO bidDao = new BidDAO();
                                if (request.getAttribute("searchCourses") == null) {
                                    SectionDAO sectionDAO = new SectionDAO();
                                    CourseDAO courseDAO = new CourseDAO();
                                    ArrayList<Section> allSections = new ArrayList<>();
                                    if (stu != null) {
                                        if (roundStatus[1].equals("2")) {
                                            allSections = sectionDAO.retrieveAll();
                                        } else {
                                            allSections = sectionDAO.retrieveAllBySchool(stu.getSchool());
                                        }
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
                                    if (roundStatus[1].equals("2")) {
                                        out.println("<th>Min. Bid Amount</th>");
                                    }
                                    out.println("<th>Vacancy</th>");
                                    out.println("</tr>");
                                    for (Section s : allSections) {
                                        Course currentCourse = courseDAO.retrieveCourse(s.getCourseID());
                                        int vacancy = s.checkVacancy() - sectionDAO.retrieveNumOfFilledSections(s.getCourseID(), s.getSectionID());
                                        if (vacancy > 0) {
                                            out.println("<tr>");
                                            out.println("<td>");
                                            out.println("<a href='AddToCartController?courseID=" + s.getCourseID() + "&sectionID=" + s.getSectionID() + "'>");
                                            out.println("<span class='glyphicon glyphicon-plus'></span>");
                                            out.println("</a>");
                                            out.println("</td>");
                                            out.println("<td>" + s.getCourseID() + "</td>");
                                            out.println("<td>" + currentCourse.getTitle() + "</td>");
                                            out.println("<td>" + currentCourse.getSchool() + "</td>");
                                            out.println("<td>");
                                            out.println("<a href='viewSection.jsp?courseID=" + s.getCourseID() + "&sectionID=" + s.getSectionID() + "&vacancy=" + vacancy + "'>" + s.getSectionID() + "</a>");
                                            out.println("</td>");
                                            out.println("<td>" + s.getInstructor() + "</td>");
                                            out.println("<td>" + s.getDay() + "</td>");
                                            out.println("<td>" + s.getStartDate() + "</td>");
                                            out.println("<td>" + s.getEndDate() + "</td>");
                                            if (roundStatus[1].equals("2")) {
                                                out.println("<td>" + bidDAO.calculateMinBid(s) + "</td>");
                                            }
                                            out.println("<td>" + vacancy + "</td>");
                                            out.println("</tr>");
                                        }
                                    }
                                    out.println("</table>");
                                } else {
                                    //to do logic for search
                                    ArrayList<Section> searchCourses = (ArrayList<Section>) request.getAttribute("searchCourses");
                                    CourseDAO courseDAO = new CourseDAO();
                                    SectionDAO sectionDAO = new SectionDAO();
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
                                    if (roundStatus[1].equals("2")) {
                                        out.println("<th>Min. Bid Amount</th>");
                                    }
                                    out.println("<th>Vacancy</th>");
                                    out.println("</tr>");
                                    for (Section s : searchCourses) {
                                        Course currentCourse = courseDAO.retrieveCourse(s.getCourseID());
                                        int vacancy = s.checkVacancy() - sectionDAO.retrieveNumOfFilledSections(s.getCourseID(), s.getSectionID());
                                        if (vacancy > 0) {
                                            out.println("<tr>");
                                            out.println("<td>");
                                            out.println("<a href='AddToCartController?courseID=" + s.getCourseID() + "&sectionID=" + s.getSectionID() + "&username=" + stu.getStudentID() + "'>");
                                            out.println("<span class='glyphicon glyphicon-plus'></span>");
                                            out.println("</a>");
                                            out.println("</td>");
                                            out.println("<td>" + s.getCourseID() + "</td>");
                                            out.println("<td>" + currentCourse.getTitle() + "</td>");
                                            out.println("<td>" + currentCourse.getSchool() + "</td>");
                                            out.println("<td>");
                                            out.println("<a href='viewSection.jsp?courseID=" + s.getCourseID() + "&sectionID=" + s.getSectionID() + "'>" + s.getSectionID() + "</a>");
                                            out.println("</td>");
                                            out.println("<td>" + s.getInstructor() + "</td>");
                                            out.println("<td>" + s.getDay() + "</td>");
                                            out.println("<td>" + s.getStartDate() + "</td>");
                                            out.println("<td>" + s.getEndDate() + "</td>");
                                            if (roundStatus[1].equals("2")) {

                                                out.println("<td>" + bidDAO.calculateMinBid(s) + "</td>");

                                            }
                                            out.println("<td>" + vacancy + "</td>");
                                            out.println("</tr>");
                                        }
                                    }
                                    out.println("</table>");
                                }


                            %>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </body>
</html>
