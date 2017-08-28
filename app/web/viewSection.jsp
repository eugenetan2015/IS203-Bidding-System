<%-- 
    Document   : viewSection
    Created on : Sep 21, 2016, 7:51:52 PM
    Author     : Tan Ming Kwang
--%>

<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <%@include file="Head.jsp" %>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>View Section</title>
    </head>
    <body>
        <%            
            String courseID = (String) request.getParameter("courseID");
            String sectionID = (String) request.getParameter("sectionID");
            String vacancy = (String)request.getParameter("vacancy");
            
            if (courseID == null && sectionID == null && vacancy == null) {
                return;
            }
            
            CourseDAO courseDAO = new CourseDAO();
            SectionDAO sectionDAO = new SectionDAO();
            Section currentSection = sectionDAO.retrieve(courseID, sectionID);
            Course currentCourse = courseDAO.retrieveCourse(courseID);
        %>
        <div class='container'>
            <h2><%=courseID%> - <%=sectionID%></h2>
            <div class="panel panel-default">
                <div class="panel-heading"><h3>Class Details</h3></div>
                <div class="panel-body">
                    <div class='row'>
                        <table class="col-md-10 col-md-offset-1 text-left">
                            <tr>
                                <td>Day:</td>
                                <td><%=currentSection.getDay()%></td>
                                <td>Instructor:</td>
                                <td><%=currentSection.getInstructor()%></td>
                            </tr>
                            <tr>
                                <td>Class Schedule:</td>
                                <td><%=currentSection.getStartDate()%> to <%=currentSection.getEndDate()%></td>
                                <td>Vacancy:</td>
                                <td><%=vacancy%>/<%=currentSection.getClassSize()%></td>
                            </tr>
                        </table>
                    </div>
                </div>
            </div>
            <div class="panel panel-default">
                <div class="panel-heading"><h3>Course Description</h3></div>
                <div class="panel-body">
                    <div class='row'>
                        <p class="col-md-10 col-md-offset-1 text-left">
                            <%=currentCourse.getDescription()%>
                        </p>
                    </div>
                </div>
            </div>
            <div class="panel panel-default">
                <div class="panel-heading"><h3>Exam Details</h3></div>
                <div class="panel-body">
                    <div class='row'>
                        <table class="col-md-10 col-md-offset-1 text-left">
                            <tr>
                                <td>Date: 
                                <%
                                    Date d = currentCourse.getExamDate();
                                    SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
                                    out.println(sdf.format(d));
                                %>
                                </td>
                            </tr>
                            <tr>
                                <td>Exam Schedule:</td>
                                <td><%=currentCourse.getExamStart()%> to <%=currentCourse.getExamEnd()%></td>
                            </tr>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
