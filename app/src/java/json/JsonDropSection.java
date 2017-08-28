/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import bios.bootstrap.util.ValidationUtility;
import bios.dao.*;
import bios.entity.*;
import com.google.gson.*;
import java.io.*;
import java.util.ArrayList;
import java.util.TreeMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Tan Ming Kwang And Cristabel
 */
@WebServlet(name = "JsonDropSection", urlPatterns = {"/drop-section", "/json/drop-section"})
public class JsonDropSection extends HttpServlet {

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
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jsonOutput = new JsonObject();
        String jsonReq = request.getParameter("r");
        String token = request.getParameter("token");
        JsonArray errorMessageArray = new JsonArray();

        TreeMap<String, String> map = vu.checkForParameterNull(jsonReq, token);

        // set json property as error first because the property will override with the latest property set
        jsonOutput.addProperty("status", "error");

        if (map.isEmpty()) {
            //used to retrieve the json file parsed
            JsonParser jp = new JsonParser();
            try {
                JsonElement root = jp.parse(jsonReq);
                JsonObject rootobj = root.getAsJsonObject();
                JsonElement userIDElement = rootobj.get("userid");
                JsonElement courseElement = rootobj.get("course");
                JsonElement sectionElement = rootobj.get("section");

                map = vu.validJSONParameters(userIDElement, courseElement, sectionElement);

                if (map.isEmpty()) {
                    String userID = userIDElement.getAsString().toLowerCase();
                    String courseID = courseElement.getAsString().toUpperCase();
                    String sectionID = sectionElement.getAsString().toUpperCase();

                    BidDAO bidDAO = new BidDAO();
                    StudentDAO studentDAO = new StudentDAO();
                    Student student = studentDAO.retrieveStudent(userID);
                    CourseDAO courseDAO = new CourseDAO();
                    Course course = courseDAO.retrieveCourse(courseID);
                    SectionDAO sectionDAO = new SectionDAO();
                    Section section = sectionDAO.retrieve(courseID, sectionID);
                    String[] roundStatus = bidDAO.getCurrentBidRound();
                    map = vu.checkInvalid(student, course, section, roundStatus[0]);

                    if (map.isEmpty()) {
                        //validate checks
                        ArrayList<Section> allStudentSections = sectionDAO.retrieveSectionsOfStudent(userID);
                        if (allStudentSections.size() > 0) {
                            for (Section currentSection : allStudentSections) {
                                String currentSectionID = currentSection.getSectionID();
                                String currentCourseID = currentSection.getCourseID();
                                if (currentSectionID.equals(sectionID) && currentCourseID.equals(courseID)) {
                                    double amountToRefund = sectionDAO.refundStudentBidAmount(userID, courseID, sectionID);
                                    sectionDAO.dropSection(userID, courseID, sectionID);
                                    studentDAO.refundStudent(userID, amountToRefund);
                                    jsonOutput.addProperty("status", "success");
                                }
                            }
                        } else {
                            JsonPrimitive element = new JsonPrimitive("no such enrollment record");
                            errorMessageArray.add(element);
                            jsonOutput.add("message", errorMessageArray);

                        }
                    }
                }
            } catch (Exception e) {
                JsonPrimitive element = new JsonPrimitive("invalid r");
                errorMessageArray.add(element);
                jsonOutput.add("message", errorMessageArray);
            }
        }

        if (map != null) {
            if (!map.isEmpty()) {
                jsonOutput.add("message", gson.toJsonTree(map.values()));
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
