/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import bios.bootstrap.util.ValidationUtility;
import bios.dao.CourseDAO;
import bios.dao.SectionDAO;
import bios.entity.Course;
import bios.entity.Section;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Wilson and Yew Kit
 */
@WebServlet(name = "JsonDumpSection", urlPatterns = {"/section-dump", "/json/section-dump"})
public class JsonDumpSection extends HttpServlet {

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

        try (PrintWriter out = response.getWriter()) {
            ValidationUtility vu = new ValidationUtility();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject jsonOutput = new JsonObject();
            String jsonReq = request.getParameter("r");
            String token = request.getParameter("token");
            TreeMap<String, String> map;
            JsonArray errorMessageArray = new JsonArray();

            // set json property as error first because the property will override with the latest property set
            jsonOutput.addProperty("status", "error");

            map = vu.checkForParameterNull(jsonReq, token);

            // This check whether the parameter entered by the user is null.
            // if the messageArray size is more than 0, its mean that there is null value in the
            // parameters
            if (map.isEmpty()) {
                //used to retrieve the json file parsed
                JsonParser jp = new JsonParser();
                try {
                    JsonElement root = jp.parse(jsonReq);

                    JsonObject rootObj = root.getAsJsonObject();
                    JsonElement courseElement = rootObj.get("course");
                    JsonElement sectionElement = rootObj.get("section");

                    map = vu.validJSONParameters(courseElement, sectionElement);

                    if (map.isEmpty()) {
                        String courseID = courseElement.getAsString().toUpperCase();
                        String sectionID = sectionElement.getAsString().toUpperCase();

                        CourseDAO courseDAO = new CourseDAO();
                        Course course = courseDAO.retrieveCourse(courseID);

                        SectionDAO sectDAO = new SectionDAO();
                        Section section = sectDAO.retrieve(courseID, sectionID);

                        map = vu.checkInvalid(course, section);

                        if (map.isEmpty()) {
                            JsonArray jsonArray = new JsonArray();

                            ArrayList<Section> studentEnrolledList = sectDAO.retrieveEnrolledCourse(courseID, sectionID);
                            for (Section s : studentEnrolledList) {
                                JsonObject output = new JsonObject();
                                output.addProperty("userid", s.getUserID());
                                output.addProperty("amount", s.geteDollars());
                                jsonArray.add(output);
                            }

                            jsonOutput.addProperty("status", "success");
                            jsonOutput.add("students", jsonArray);
                        }
                    }
                } catch (Exception e) {
                    JsonElement element = new JsonPrimitive("invalid r");
                    errorMessageArray.add(element);
                    jsonOutput.add("message", errorMessageArray);
                }
            }

            // if messageArray is more than 0 then add this array to Json Object
            if (map != null) {
                if (!map.isEmpty()) {
                    jsonOutput.add("message", gson.toJsonTree(map.values()));
                }
            }

            out.print(gson.toJson(jsonOutput));
        }
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
