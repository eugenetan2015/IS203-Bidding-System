<%-- 
    Document   : BootStrapUI
    Created on : Sep 26, 2016, 10:02 AM
    Pair Programmer : Cristabel Lau / Wilson He
--%>

<%@page import="java.util.List"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<html>
    <head>
        <%@include file="Head_Admin.jsp" %>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Boot Strap</title>
        <script>
            $(function () {

                // We can attach the `fileselect` event to all file inputs on the page
                $(document).on('change', ':file', function () {
                    var input = $(this),
                            numFiles = input.get(0).files ? input.get(0).files.length : 1,
                            label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
                    input.trigger('fileselect', [numFiles, label]);
                });

                // We can watch for our custom `fileselect` event like this
                $(document).ready(function () {
                    $(':file').on('fileselect', function (event, numFiles, label) {

                        var input = $(this).parents('.input-group').find(':text'),
                                log = numFiles > 1 ? numFiles + ' files selected' : label;

                        if (input.length) {
                            input.val(log);
                        } else {
                            if (log)
                                alert(log);
                        }

                    });
                });

            });
        </script>
    </head>
    <body>
        <div class='container'>
            <div class="panel panel-default">
                <div class="panel-heading"><h2>Bootstrap</h2></div>
                <div class="panel-body">
                    <%                        String errorMsg = (String) request.getAttribute("errorMsg");
                        if (errorMsg != null) {
                            out.print("<div class='alert alert-danger' role='alert'>" + errorMsg + "</div>");
                        }

                        String successMsg = (String) request.getAttribute("successMsg");
                        if (successMsg != null) {
                            out.print("<div class='alert alert-success' role='alert'>" + successMsg + "</div>");
                        }
                    %>
                    <form action="upload" method="post" name="formUpload" enctype="multipart/form-data">

                        <div class="input-group col-md-4 col-lg-offset-4">
                            <label class="input-group-btn">
                                <span class="btn btn-primary">
                                    Browse <input type="file" name="fileToUpload" value="Upload" style="display: none;length:100px" multiple>
                                </span>
                            </label>
                            <input type="text" class="form-control" readonly>
                        </div>
                        <br />
                        <div class='row'>
                            <div class="row col-md-12" align="center">
                                <button type="submit" class="btn btn-default">Bootstrap</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            <%
                if ((request.getAttribute("studErrorList") != null) && (request.getAttribute("courseErrorList") != null) && (request.getAttribute("sectionErrorList") != null) && (request.getAttribute("prerequisiteErrorList") != null) && (request.getAttribute("courseCompletedErrorList") != null) && (request.getAttribute("bidErrorList") != null)) {
            %>                
            <div class="container"><h3>Errors Found</h3></div>
            <div id="exTab1" class="container">	
                <ul  class="nav nav-pills">
                    <li class="active">
                        <a  href="#student" data-toggle="tab">student.csv</a>
                    </li>
                    <li><a href="#course" data-toggle="tab">course.csv</a>
                    </li>
                    <li><a href="#section" data-toggle="tab">section.csv</a>
                    </li>
                    <li><a href="#prerequisite" data-toggle="tab">prerequisite.csv</a>
                    </li>
                    <li><a href="#course_completed" data-toggle="tab">course_completed.csv</a>
                    </li>
                    <li><a href="#bid" data-toggle="tab">bid.csv</a>
                    </li>
                </ul>
                <div class="tab-content clearfix">
                    <div class="tab-pane active" id="student">
                        <%
                            ArrayList<String> studErrorList = (ArrayList<String>) request.getAttribute("studErrorList");
                            int numOfSuccessStudent = (int) request.getAttribute("numOfSuccessStudent");
                            out.println("<div class='alert alert-warning alert-dismissible' role='alert'>");
                            out.println("<button type='button' class='close' data-dismiss='alert' aria-label='Close'><span aria-hidden='true'>&times;</span></button>");
                            HashMap<List<String>, ArrayList<String>> errorMapStudent = (HashMap<List<String>, ArrayList<String>>) request.getAttribute("StudentErrorMap");
                            out.println("<strong>" + numOfSuccessStudent + "</strong>" + " records added successfully. <strong>" + errorMapStudent.size() + "</strong> errors found." + "</div>");
                            out.println("<h4>Student Error List:</h4>");
                            out.println("<table class='table table-hover'>");
                            for (String s : studErrorList) {
                                out.println("<tr>");
                                out.println("<td>");
                                out.println(s);
                                out.println("</td>");
                                out.println("</tr>");
                            }
                            out.println("</table>");
                        %>
                    </div>
                    <div class="tab-pane" id="course">
                        <%
                            ArrayList<String> courseErrorList = (ArrayList<String>) request.getAttribute("courseErrorList");
                            int numOfSuccessCourse = (int) request.getAttribute("numOfSuccessCourse");
                            out.println("<div class='alert alert-warning alert-dismissible' role='alert'>");
                            out.println("<button type='button' class='close' data-dismiss='alert' aria-label='Close'><span aria-hidden='true'>&times;</span></button>");
                            HashMap<List<String>, ArrayList<String>> errorMapCourse = (HashMap<List<String>, ArrayList<String>>) request.getAttribute("CourseErrorMap");
                            out.println("<strong>" + numOfSuccessCourse + "</strong>" + " records added successfully. <strong>" + errorMapCourse.size() + "</strong> errors found." + "</div>");
                            out.println("<h4>Course Error List:</h4>");
                            out.println("<table class='table table-hover'>");
                            for (String s : courseErrorList) {
                                out.println("<tr>");
                                out.println("<td>");
                                out.println(s);
                                out.println("</td>");
                                out.println("</tr>");
                            }
                            out.println("</table>");
                        %>
                    </div>
                    <div class="tab-pane" id="section">
                        <%
                            ArrayList<String> sectionErrorList = (ArrayList<String>) request.getAttribute("sectionErrorList");
                            int numOfSuccessSection = (int) request.getAttribute("numOfSuccessSection");
                            out.println("<div class='alert alert-warning alert-dismissible' role='alert'>");
                            out.println("<button type='button' class='close' data-dismiss='alert' aria-label='Close'><span aria-hidden='true'>&times;</span></button>");
                            HashMap<List<String>, ArrayList<String>> errorMapSection = (HashMap<List<String>, ArrayList<String>>) request.getAttribute("SectionErrorMap");
                            out.println("<strong>" + numOfSuccessSection + "</strong>" + " records added successfully. <strong>" + errorMapSection.size() + "</strong> errors found." + "</div>");
                            out.println("<h4>Section Error List:</h4>");
                            out.println("<table class='table table-hover'>");
                            for (String s : sectionErrorList) {
                                out.println("<tr>");
                                out.println("<td>");
                                out.println(s);
                                out.println("</td>");
                                out.println("</tr>");
                            }
                            out.println("</table>");
                        %>
                    </div>
                    <div class="tab-pane" id="prerequisite">
                        <%
                            ArrayList<String> prerequisiteErrorList = (ArrayList<String>) request.getAttribute("prerequisiteErrorList");
                            int numOfSuccessPrerequisite = (int) request.getAttribute("numOfSuccessPrerequisite");
                            out.println("<div class='alert alert-warning alert-dismissible' role='alert'>");
                            out.println("<button type='button' class='close' data-dismiss='alert' aria-label='Close'><span aria-hidden='true'>&times;</span></button>");
                            HashMap<List<String>, ArrayList<String>> errorMapPreReq = (HashMap<List<String>, ArrayList<String>>) request.getAttribute("PreReqErrorMap");
                            out.println("<strong>" + numOfSuccessPrerequisite + "</strong>" + " records added successfully. <strong>" + errorMapPreReq.size() + "</strong> errors found." + "</div>");
                            out.println("<h4>Prerequisite Error List:</h4>");
                            out.println("<table class='table table-hover'>");
                            for (String s : prerequisiteErrorList) {
                                out.println("<tr>");
                                out.println("<td>");
                                out.println(s);
                                out.println("</td>");
                                out.println("</tr>");
                            }
                            out.println("</table>");
                        %>
                    </div>
                    <div class="tab-pane" id="course_completed">
                        <%
                            ArrayList<String> courseCompletedErrorList = (ArrayList<String>) request.getAttribute("courseCompletedErrorList");
                            int numOfSuccessCourseCompleted = (int) request.getAttribute("numOfSuccessCourseCompleted");
                            out.println("<div class='alert alert-warning alert-dismissible' role='alert'>");
                            out.println("<button type='button' class='close' data-dismiss='alert' aria-label='Close'><span aria-hidden='true'>&times;</span></button>");
                            HashMap<List<String>, ArrayList<String>> errorMapCC = (HashMap<List<String>, ArrayList<String>>) request.getAttribute("CCErrorMap");
                            out.println("<strong>" + numOfSuccessCourseCompleted + "</strong>" + " records added successfully. <strong>" + errorMapCC.size() + "</strong> errors found." + "</div>");
                            out.println("<h4>Coure Completed Error List:</h4>");
                            out.println("<table class='table table-hover'>");
                            for (String s : courseCompletedErrorList) {
                                out.println("<tr>");
                                out.println("<td>");
                                out.println(s);
                                out.println("</td>");
                                out.println("</tr>");
                            }
                            out.println("</table>");
                        %>
                    </div>
                    <div class="tab-pane" id="bid">
                        <%
                            ArrayList<String> bidErrorList = (ArrayList<String>) request.getAttribute("bidErrorList");
                            int numOfSuccessBid = (int) request.getAttribute("numOfSuccessBid");
                            out.println("<div class='alert alert-warning alert-dismissible' role='alert'>");
                            out.println("<button type='button' class='close' data-dismiss='alert' aria-label='Close'><span aria-hidden='true'>&times;</span></button>");
                            HashMap<List<String>, ArrayList<String>> errorMapBid = (HashMap<List<String>, ArrayList<String>>) request.getAttribute("BidErrorMap");
                            out.println("<strong>" + numOfSuccessBid + "</strong>" + " records added successfully. <strong>" + errorMapBid.size() + "</strong> errors found." + "</div>");
                            out.println("<h4>Bid Error List:</h4>");
                            out.println("<table class='table table-hover'>");
                            for (String s : bidErrorList) {
                                out.println("<tr>");
                                out.println("<td>");
                                out.println(s);
                                out.println("</td>");
                                out.println("</tr>");
                            }
                            out.println("</table>");
                        %>
                    </div>
                </div>
            </div>
            <% }%>
        </div>
    </body>
</html>
