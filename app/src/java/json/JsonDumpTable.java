/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import bios.bootstrap.util.ValidationUtility;
import bios.dao.BidDAO;
import bios.dao.CourseDAO;
import bios.dao.SectionDAO;
import bios.dao.StudentDAO;
import bios.entity.Bid;
import bios.entity.Course;
import bios.entity.Section;
import bios.entity.Student;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.TreeMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Admin
 */
@WebServlet(name = "JsonDumpTable", urlPatterns = {"/dump", "/json/dump"})
public class JsonDumpTable extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ValidationUtility vu = new ValidationUtility();
        PrintWriter out = response.getWriter();
        String token = request.getParameter("token");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jsonOutput = new JsonObject();
        JsonArray errorMessageArray = new JsonArray();
        TreeMap<String, String> map = null;

        // set json property as error first because the property will override with the latest property set
        jsonOutput.addProperty("status", "error");

        map = vu.checkForParameterNull(token);

        if (map.isEmpty()) {
            jsonOutput.addProperty("status", "success");

            BidDAO bidDAO = new BidDAO();
            CourseDAO courseDAO = new CourseDAO();
            SectionDAO sectionDAO = new SectionDAO();
            StudentDAO studentDAO = new StudentDAO();

            //retrieve the items from the database through the DAOs
            //array - json object for each object
            JsonArray courses = new JsonArray();
            ArrayList<Course> courseList = courseDAO.retrieveAllCourses();
            Comparator<Course> courseComparator = new Comparator<Course>() {
                @Override
                public int compare(Course a, Course b) {
                    String titleA = a.getCourseID();
                    String titleB = b.getCourseID();
                    return titleA.compareTo(titleB);
                }
            };
            Collections.sort(courseList, courseComparator);

            try {
                for (int i = 0; i < courseList.size(); i++) {
                    Course c = courseList.get(i);
                    JsonObject temp = new JsonObject();
                    //addProperty allows you to add Strings
                    temp.addProperty("course", c.getCourseID());
                    temp.addProperty("school", c.getSchool());
                    temp.addProperty("title", c.getTitle());
                    temp.addProperty("description", c.getDescription());
                    Date date = c.getExamDate();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                    SimpleDateFormat formatterTime = new SimpleDateFormat("h:mm");
                    Date examStart = formatterTime.parse(c.getExamStart());
                    Date examEnd = formatterTime.parse(c.getExamEnd());
                    temp.addProperty("exam date", formatter.format(date));

                    String examStartStr = formatterTime.format(examStart);
                    String examEndStr = formatterTime.format(examEnd);

                    examStartStr = examStartStr.replace(":", "");
                    examEndStr = examEndStr.replace(":", "");

                    temp.addProperty("exam start", examStartStr);
                    temp.addProperty("exam end", examEndStr);
                    courses.add(temp);
                }
                jsonOutput.add("course", courses);

                JsonArray sections = new JsonArray();
                ArrayList<Section> sectionList = sectionDAO.retrieveAll();

                for (int i = 0; i < sectionList.size(); i++) {
                    Section s = sectionList.get(i);
                    JsonObject temp = new JsonObject();
                    temp.addProperty("course", s.getCourseID());
                    temp.addProperty("section", s.getSectionID());
                    temp.addProperty("day", s.getDay());
                    //SimpleDateFormat formatterTime = new SimpleDateFormat("hmm");
                    String temp1 = s.getStartDate();
                    String temp2 = s.getEndDate();
                    String startHr = "";
                    String endHr = "";
                    if (temp1.charAt(0) != '0') {
                        startHr = temp1.substring(0, temp1.indexOf(":"));
                    } else {
                        startHr = temp1.substring(1, temp1.indexOf(":"));
                    }
                    if (temp2.charAt(0) != '0') {
                        endHr = temp2.substring(0, temp2.indexOf(":"));
                    } else {
                        endHr = temp2.substring(1, temp2.indexOf(":"));
                    }
                    String startMin = temp1.substring(temp1.indexOf(":") + 1, temp1.lastIndexOf(":"));
                    String endMin = temp2.substring(temp2.indexOf(":") + 1, temp2.lastIndexOf(":"));
                    String startStr = startHr + startMin;
                    String endStr = endHr + endMin;
                    temp.addProperty("start", startStr);
                    temp.addProperty("end", endStr);
                    temp.addProperty("instructor", s.getInstructor());
                    temp.addProperty("venue", s.getVenue());
                    temp.addProperty("size", s.getClassSize());
                    sections.add(temp);
                }
                jsonOutput.add("section", sections);

                JsonArray students = new JsonArray();
                ArrayList<Student> studentList = studentDAO.retrieveAll();
                Comparator<Student> studentComparator = new Comparator<Student>() {
                    @Override
                    public int compare(Student a, Student b) {
                        String titleA = a.getStudentID();
                        String titleB = b.getStudentID();
                        return titleA.compareTo(titleB);
                    }
                };
                Collections.sort(studentList, studentComparator);

                for (int i = 0; i < studentList.size(); i++) {
                    Student s = studentList.get(i);
                    JsonObject temp = new JsonObject();
                    temp.addProperty("userid", s.getStudentID());
                    temp.addProperty("password", s.getPassword());
                    temp.addProperty("name", s.getName());
                    temp.addProperty("school", s.getSchool());
                    temp.addProperty("edollar", s.geteDollars());
                    students.add(temp);
                }
                jsonOutput.add("student", students);

                JsonArray prerequisites = new JsonArray();
                ArrayList<String[]> prerequisiteList = courseDAO.retrieveAllPrerequisites();
                for (int i = 0; i < prerequisiteList.size(); i++) {
                    JsonObject temp = new JsonObject();
                    temp.addProperty("course", prerequisiteList.get(i)[0]);
                    temp.addProperty("prerequisite", prerequisiteList.get(i)[1]);
                    prerequisites.add(temp);
                }
                jsonOutput.add("prerequisite", prerequisites);

                JsonArray bids = new JsonArray();
                ArrayList<Bid> bidList = bidDAO.retrieveAllBids();
                for (int i = 0; i < bidList.size(); i++) {
                    Bid b = bidList.get(i);
                    JsonObject temp = new JsonObject();
                    temp.addProperty("userid", b.getStudent().getStudentID());
                    temp.addProperty("amount", b.getBidAmount());
                    temp.addProperty("course", b.getCourseID());
                    temp.addProperty("section", b.getSectionID());
                    bids.add(temp);
                }
                jsonOutput.add("bid", bids);

                JsonArray completedCourses = new JsonArray();
                ArrayList<String[]> completedCoursesList = courseDAO.retrieveCourseCompleted();
                for (int i = 0; i < completedCoursesList.size(); i++) {
                    JsonObject temp = new JsonObject();
                    temp.addProperty("userid", completedCoursesList.get(i)[0]);
                    temp.addProperty("code", completedCoursesList.get(i)[1]);
                    completedCourses.add(temp);
                }

                if (completedCourses.size() == 0) {
                    completedCourses = gson.toJsonTree(completedCoursesList).getAsJsonArray();
                }
                jsonOutput.add("completed-course", completedCourses);

                JsonArray sectionStudents = new JsonArray();
                ArrayList<String[]> sectionStudentList = sectionDAO.retrieveAllSectionStudent();
                for (int i = 0; i < sectionStudentList.size(); i++) {
                    JsonObject temp = new JsonObject();
                    temp.addProperty("userid", sectionStudentList.get(i)[0]);
                    temp.addProperty("course", sectionStudentList.get(i)[1]);
                    temp.addProperty("section", sectionStudentList.get(i)[2]);
                    temp.addProperty("amount", sectionStudentList.get(i)[3]);
                    sectionStudents.add(temp);
                }
                jsonOutput.add("section-student", sectionStudents);
            } catch (Exception e) {
                JsonElement element = new JsonPrimitive("invalid r");
                errorMessageArray.add(element);
                jsonOutput.add("message", errorMessageArray);
            }
        }

        out.print(gson.toJson(jsonOutput));
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
