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
import bios.entity.Bid;
import bios.entity.Course;
import bios.entity.Section;
import bios.entity.Student;
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
@WebServlet(name = "JsonDumpBid", urlPatterns = {"/bid-dump", "/json/bid-dump"})
public class JsonDumpBid extends HttpServlet {

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
            JsonArray errorMessageArray = new JsonArray();

            // set json property as error first because the property will override with the latest property set
            jsonOutput.addProperty("status", "error");

            TreeMap<String, String> map = vu.checkForParameterNull(jsonReq, token);

            if (map.isEmpty()) {
                //used to retrieve the json file parsed
                JsonParser jp = new JsonParser();
                try {
                    JsonElement root = jp.parse(jsonReq);

                    JsonObject rootObj = root.getAsJsonObject();
                    JsonElement courseElement = rootObj.get("course");
                    JsonElement sectionIDElement = rootObj.get("section");
                    map = vu.validJSONParameters(courseElement, sectionIDElement);

                    if (map.isEmpty()) {
                        String courseCode = courseElement.getAsString().toUpperCase();
                        String sectionIDStr = sectionIDElement.getAsString().toUpperCase();

                        CourseDAO courseDAO = new CourseDAO();
                        Course course = courseDAO.retrieveCourse(courseCode);
                        SectionDAO sectionDAO = new SectionDAO();
                        Section section = sectionDAO.retrieve(courseCode, sectionIDStr);

                        map = vu.checkInvalid(course, section);

                        if (map.isEmpty()) {
                            JsonArray jsonArray = new JsonArray();
                            BidDAO bidDAO = new BidDAO();
                            String[] roundStatus = bidDAO.getCurrentBidRound();
                            int round = Integer.parseInt(roundStatus[1]);
                            ArrayList<Bid> allBidsForASection = bidDAO.viewAllBidsForSection(courseCode, sectionIDStr, round);

                            int count = 1;

                            //if (!allBidsForASection.isEmpty()) {
                                jsonOutput.addProperty("status", "success");

                                for (Bid bid : allBidsForASection) {
                                    JsonObject output = new JsonObject();
                                    Student currentStudent = bid.getStudent();
                                    String studentID = currentStudent.getStudentID();
                                    String status = bid.getStatus();
                                    double bidAmount = bid.getBidAmount();
                                    output.addProperty("row", count);
                                    output.addProperty("userid", studentID);
                                    output.addProperty("amount", bidAmount);
                                    if (status.equals("Success")) {
                                        status = "in";
                                    } else if (status.equals("Fail")) {
                                        status = "out";
                                    } else {
                                        status = "-";
                                    }
                                    output.addProperty("result", status);
                                    jsonArray.add(output);
                                    count++;
                                }
                                jsonOutput.add("bids", jsonArray);
                            /*} else {
                                JsonElement element = new JsonPrimitive("no student bidded for the course");
                                errorMessageArray.add(element);
                                jsonOutput.add("message", errorMessageArray);
                            }*/

                        }
                    }
                } catch (Exception e) {
                    JsonElement element = new JsonPrimitive(e.getMessage());
                    errorMessageArray.add(element);
                    jsonOutput.add("message", errorMessageArray);
                }
            }

            // if errorMessageArray is more than 0 then add this array to Json Object
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
