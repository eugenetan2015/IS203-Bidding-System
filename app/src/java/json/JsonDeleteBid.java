/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import bios.bootstrap.util.ValidationUtility;
import bios.dao.*;
import bios.entity.Bid;
import bios.entity.Course;
import bios.entity.Section;
import bios.entity.Student;
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
 * @author Quek Yew Kit And Wilson He
 */
@WebServlet(name = "JsonDeleteBid", urlPatterns = {"/delete-bid", "/json/delete-bid"})
public class JsonDeleteBid extends HttpServlet {

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

            // This check whether the parameter entered by the user is null.
            // if the errorMessageArray size is more than 0, its mean that there is null value in the
            // parameters
            if (map.isEmpty()) {
                //used to retrieve the json file parsed
                JsonParser jp = new JsonParser();
                try {
                    JsonElement root = jp.parse(jsonReq);

                    JsonObject rootObj = root.getAsJsonObject();
                    JsonElement userIDElement = rootObj.get("userid");
                    JsonElement courseElement = rootObj.get("course");
                    JsonElement sectionElement = rootObj.get("section");
                    map = vu.validJSONParameters(userIDElement, courseElement, sectionElement);

                    if (map.isEmpty()) {
                        String userID = userIDElement.getAsString().toLowerCase();
                        String courseCode = courseElement.getAsString().toUpperCase();
                        String sectionID = sectionElement.getAsString().toUpperCase();

                        StudentDAO studentDAO = new StudentDAO();
                        Student student = studentDAO.retrieveStudent(userID);
                        CourseDAO courseDAO = new CourseDAO();
                        Course course = courseDAO.retrieveCourse(courseCode);
                        SectionDAO sectionDAO = new SectionDAO();
                        Section section = sectionDAO.retrieve(courseCode, sectionID);
                        BidDAO bidDAO = new BidDAO();
                        String[] round = bidDAO.getCurrentBidRound();

                        map = vu.checkInvalid(student, course, section, round);

                        if (map.isEmpty()) {
                            ArrayList<Bid> allStudentBids = bidDAO.viewStudentBids(userID);

                            if (allStudentBids.size() > 0) {
                                for (Bid currentBid : allStudentBids) {
                                    String currentSectionID = currentBid.getSectionID();
                                    String currentCourseID = currentBid.getCourseID();
                                    if (currentSectionID.equals(sectionID) && currentCourseID.equals(courseCode)) {
                                        String[] bidRound = bidDAO.getCurrentBidRound();
                                        if (bidRound[0].equals("started")) {
                                            double amountToRefund = currentBid.getBidAmount();
                                            bidDAO.dropBid(currentBid, Integer.parseInt(bidRound[1]));
                                            studentDAO.refundStudent(userID, amountToRefund);
                                            jsonOutput.addProperty("status", "success");
                                        }
                                    } else {
                                        JsonElement element = new JsonPrimitive("no such bid");
                                        errorMessageArray.add(element);
                                        jsonOutput.add("message", errorMessageArray);
                                    }
                                }
                            } else {
                                JsonElement element = new JsonPrimitive("no such bid");
                                errorMessageArray.add(element);
                                jsonOutput.add("message", errorMessageArray);
                            }
                        }
                    }
                } catch (Exception e) {
                    JsonElement element = new JsonPrimitive("invalid r");
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
