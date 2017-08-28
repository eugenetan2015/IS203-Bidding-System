<%-- 
    Document   : viewMyClasses
    Created on : Oct 5, 2016, 13:59:46 PM
    Author     : Eugene Tan
--%>

<%@page import="bios.bootstrap.util.AdminUtility"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.*"%>
<%@page import="java.io.*"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <%
            String date = (String) request.getAttribute("year");
            Student stu = (Student) session.getAttribute("user");
        %>

        <%@include file="Head.jsp" %>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>View My Classes</title>
        <link href="${pageContext.request.contextPath}/css/fullcalendar.min.css" rel="stylesheet" />
        <link href="${pageContext.request.contextPath}/css/fullcalendar.print.css" rel='stylesheet' media='print' />
        <link href="${pageContext.request.contextPath}/css/scheduler.min.css" rel='stylesheet' />
        <script src="${pageContext.request.contextPath}/js/moment.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/fullcalendar.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/scheduler.min.js"></script>
        <script>
            $(function () { // document ready
                var date = '<% if (date != null) {
                        out.print(date);
                    } else {
                        SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-dd");
                        Date d = new Date();
                        out.print(df.format(d));
                    }%>';

                var studentID = '<% if (stu != null) {
                        out.print(AdminUtility.getLocalTimetableDir() + "/" + stu.getStudentID() + "_timetable.json");
                    }%>';

                $('#calendar').fullCalendar({
                    schedulerLicenseKey: 'GPL-My-Project-Is-Open-Source',
                    now: date,
                    editable: false, // enable draggable events
                    aspectRatio: 0.0,
                    //scrollTime: '8:00', // undo default 6am scrollTime
                    height: 800,
                    allDaySlot: false,
                    lazyFetching: true,
                    minTime: '8:00',
                    firstDay: 1,
                    header: {
                        left: 'today prev,next',
                        center: 'title',
                        right: 'prevYear,agendaWeek,month,nextYear'
                    },
                    defaultView: 'agendaWeek',
                    events: {// you can also specify a plain string like 'json/events.json'
                        url: studentID,
                        error: function () {
                            $('#script-warning').show();
                        }
                    }
                });

            });
        </script>
    </head>
    <body>
        <div class='container'>
            <div class="panel panel-default">
                <div class="panel-heading"><h2>View enrolled sections:</h2></div>
                <%
                    ArrayList<Section> name_of_section = (ArrayList<Section>) request.getAttribute("nameSection");
                    if (request.getAttribute("result") != null && (boolean) request.getAttribute("result")) {
                        out.print("<div class='alert alert-success' role='alert'>" + name_of_section.get(0) + " " + name_of_section.get(1) + " dropped successfully" + "</div>");
                    } else if (request.getAttribute("result") != null) {
                        out.print("<div class='alert alert-danger' role='alert'>" + "Error: Cannot drop sections" + "</div>");
                    }
                %>

                <div class="panel-body">
                    <div class ="control-group">
                        <div id="option_controls" class="controls">
                            <form name="DropSectionController" action="DropSection" method = "get">

                                <%
                                    Student student = (Student) session.getAttribute("user");
                                    if (student != null) {
                                        String user_id = student.getStudentID();
                                        SectionDAO secDao = new SectionDAO();
                                        ArrayList<Section> enrolled = secDao.retrieveSectionsOfStudent(user_id);

                                        if (enrolled == null || enrolled.isEmpty()) {
                                            out.println("No enrolled sections yet");
                                        } else {

                                            out.println("To drop a section, press the (-) button<p/>");
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
                                            out.println("<th>Venue</th>");
                                            out.println("<th>Bid Amount</th>");
                                            out.println("</tr>");

                                            CourseDAO cDao = new CourseDAO();
                                            SectionDAO sDao = new SectionDAO();
                                            for (Section s : enrolled) {
                                                Course course = cDao.retrieveCourse(s.getCourseID());
                                                int vacancy = s.checkVacancy() - secDao.retrieveNumOfFilledSections(s.getCourseID(), s.getSectionID());
                                                String courseID = s.getCourseID();
                                                out.println("<tr>");
                                                if (!(roundStatus[1].equals("2") && roundStatus[0].equals("ended"))) {
                                                    out.println("<td>");
                                                    out.println("<a href='DropSection?courseID=" + s.getCourseID() + "&sectionID=" + s.getSectionID() + "' onclick=\"return confirm('Are you sure you want to drop this class: " + s.getCourseID() + " (" + s.getSectionID() + ")" + "?')\">");
                                                    out.println("<span class='glyphicon glyphicon-minus'></span>");
                                                    out.println("</a>");
                                                    out.println("</td>");
                                                } else {
                                                    out.println("<td></td>");
                                                }
                                                out.println("<td>" + s.getCourseID() + "</td>");
                                                out.println("<td>" + course.getTitle() + "</td>");
                                                out.println("<td>" + course.getSchool() + "</td>");
                                                out.println("<td>");
                                                out.println("<a href='viewSection.jsp?courseID=" + s.getCourseID() + "&sectionID=" + s.getSectionID() + "&vacancy=" + vacancy + "'>" + s.getSectionID() + "</a>");
                                                out.println("</td>");
                                                out.println("<td>" + s.getInstructor() + "</td>");
                                                out.println("<td>" + s.getDay() + "</td>");
                                                out.println("<td>" + s.getStartDate() + "</td>");
                                                out.println("<td>" + s.getEndDate() + "</td>");
                                                out.println("<td>" + s.getVenue() + "</td>");
                                                double bidAmt = sDao.retrieveBidAmt(s.getCourseID(), s.getSectionID());
                                                out.println("<td>" + bidAmt + "</td>");
                                                out.println("</tr>");
                                            }
                                            out.println("</table>");
                                        }
                                    }
                                %>
                            </form>   
                        </div>    
                    </div>
                    <hr size="5"/><br/><br/>
                    <div id='calendar'></div>
                    <table width="1138px">
                        <tr>
                            <th colspan='6'><h4 align='center'><b>Legend</b></h4></th>
                        </tr>
                        <tr>
                            <td><div style="width:30px;height:20px;border:1px solid #000; background-color:#378006; margin-left:30px"></div></td>
                            <td><div style="height: 35px"><p style="margin-right: 30px; line-height: 35px">Bid Placed</p></div></td>
                            <td><div style="width:30px;height:20px;border:1px solid #000; background-color:#6495ED; margin-left:30px"></div></td>
                            <td><div style="height: 35px"><p style="margin-right: 30px; line-height: 35px">Course Enrolled</p></div></td>
                            <td><div style="width:30px;height:20px;border:1px solid #000; background-color:red; margin-left:30px"></div></td>
                            <td><div style="height: 35px"><p style="margin-right: 30px; line-height: 35px">Bids in Bidding Cart</p></td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>
    </body>
</html>
