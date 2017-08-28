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
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Tan Ming Kwang
 */
@WebServlet(name = "JsonBidStatus", urlPatterns = {"/bid-status", "/json/bid-status"})
public class JsonBidStatus extends HttpServlet {

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
        PrintWriter out = response.getWriter();
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

                    StudentDAO studentDAO = new StudentDAO();
                    CourseDAO courseDAO = new CourseDAO();
                    Course course = courseDAO.retrieveCourse(courseCode);
                    SectionDAO sectionDAO = new SectionDAO();
                    Section section = sectionDAO.retrieve(courseCode, sectionIDStr);

                    map = vu.checkInvalid(course, section);
                    if (map.isEmpty()) {
                        BidDAO bidDAO = new BidDAO();
                        String[] roundStatus = bidDAO.getCurrentBidRound();
                        int round = Integer.parseInt(roundStatus[1]);
                        String status = roundStatus[0];
                        ArrayList<Bid> allBidsForASection = bidDAO.viewAllBidsForSection(courseCode, sectionIDStr, round);
                        int totalNumBids = allBidsForASection.size();

                        jsonOutput.addProperty("status", "success");
                        if (allBidsForASection.size() == 0) {
                            jsonOutput.addProperty("vacancy", section.checkVacancy());
                            jsonOutput.addProperty("min-bid-amount", 10.0);
                            JsonArray emptyArray = new JsonArray();
                            jsonOutput.add("students", emptyArray);
                        } else {
                            if (round == 1) {
                                int totalSpace = section.checkVacancy();
                                int vacancy = 0;
                                if (status.equals("started")) {

                                    vacancy = totalSpace;
                                    double minBid = 0.0;
                                    String bidStatus = "pending";

                                    if (totalNumBids < vacancy) {
                                        minBid = allBidsForASection.get(totalNumBids - 1).getBidAmount();
                                    } else if (totalNumBids == 0) {
                                        minBid = 10.0;
                                    } else {
                                        int index = totalNumBids - vacancy;
                                        minBid = allBidsForASection.get(index).getBidAmount();
                                    }

                                    jsonOutput.addProperty("vacancy", vacancy);
                                    jsonOutput.addProperty("min-bid-amount", minBid);
                                    JsonArray studentArray = new JsonArray();
                                    for (Bid b : allBidsForASection) {
                                        JsonObject studentObject = new JsonObject();
                                        Student currentStudent = b.getStudent();
                                        String userid = currentStudent.getStudentID();
                                        double bidAmount = b.getBidAmount();
                                        //double balance = currentStudent.geteDollars() - bidAmount;
                                        double balance = currentStudent.geteDollars();
                                        studentObject.addProperty("userid", userid);
                                        studentObject.addProperty("amount", bidAmount);
                                        studentObject.addProperty("balance", balance);
                                        studentObject.addProperty("status", bidStatus);
                                        studentArray.add(studentObject);
                                    }
                                    jsonOutput.add("students", studentArray);
                                } else {
                                    //status = ended
                                    ArrayList<Bid> numOfOccupiedSections = bidDAO.retrieveNumOfSuccessfulBids(courseCode, sectionIDStr);
                                    vacancy = totalSpace - numOfOccupiedSections.size();
                                    double minBid = 10.0;
                                    if (numOfOccupiedSections.size() > 0) {
                                        minBid = numOfOccupiedSections.get(numOfOccupiedSections.size() - 1).getBidAmount();
                                    }

                                    jsonOutput.addProperty("vacancy", vacancy);
                                    jsonOutput.addProperty("min-bid-amount", minBid);
                                    JsonArray studentArray = new JsonArray();

                                    for (Bid b : allBidsForASection) {
                                        JsonObject studentObject = new JsonObject();
                                        Student currentStudent = b.getStudent();
                                        String userid = currentStudent.getStudentID();
                                        double bidAmount = b.getBidAmount();
                                        double balance = currentStudent.geteDollars();
                                        String bidStatus = b.getStatus().toLowerCase();
                                        studentObject.addProperty("userid", userid);
                                        studentObject.addProperty("amount", bidAmount);
                                        studentObject.addProperty("balance", balance);
                                        studentObject.addProperty("status", bidStatus);
                                        studentArray.add(studentObject);
                                    }
                                    jsonOutput.add("students", studentArray);
                                }
                            } else {
                                //if round 2
                                int totalSpace = section.checkVacancy();
                                int vacancy = 0;
                                if (status.equals("started")) {
                                    ArrayList<Bid> numOfOccupiedSections = bidDAO.retrieveNumOfSuccessfulBids(courseCode, sectionIDStr);
                                    vacancy = totalSpace - numOfOccupiedSections.size();
                                    double minBid = bidDAO.calculateMinBid(section);
                                    JsonArray studentArray = new JsonArray();
                                    jsonOutput.addProperty("vacancy", vacancy);
                                    jsonOutput.addProperty("min-bid-amount", minBid);
                                    
                                    for (Bid b : allBidsForASection) {
                                        JsonObject studentObject = new JsonObject();
                                        Student currentStudent = b.getStudent();
                                        String userid = currentStudent.getStudentID();
                                        double bidAmount = b.getBidAmount();
                                        double balance = currentStudent.geteDollars();
                                        boolean failpass = bidDAO.stillInRange(b, section);
                                        String bidStatus = "";
                                        if (failpass) {
                                            bidStatus = "success";
                                        } else {
                                            bidStatus = "fail";
                                        }
                                        studentObject.addProperty("userid", userid);
                                        studentObject.addProperty("amount", bidAmount);
                                        studentObject.addProperty("balance", balance);
                                        studentObject.addProperty("status", bidStatus);
                                        studentArray.add(studentObject);
                                    }
                                    jsonOutput.add("students", studentArray);
                                } else {
                                    //round is closed
                                    ArrayList<Bid> numOfOccupiedSections = bidDAO.retrieveNumOfSuccessfulBids(courseCode, sectionIDStr);
                                    vacancy = totalSpace - numOfOccupiedSections.size();
                                    ArrayList<Bid> round2SuccessfulBids = bidDAO.retrieveAllSuccessfulBidsinRound2();
                                    ArrayList<Bid> occupiedSections = bidDAO.retrieveNumOfSuccessfulBids(courseCode, sectionIDStr);
                                    double minBid = round2SuccessfulBids.get(0).getBidAmount();
                                    JsonArray studentArray = new JsonArray();
                                    jsonOutput.addProperty("vacancy", vacancy);
                                    jsonOutput.addProperty("min-bid-amount", minBid);
                                    
                                    for (Bid b : occupiedSections) {
                                        JsonObject studentObject = new JsonObject();
                                        Student currentStudent = b.getStudent();
                                        String userid = currentStudent.getStudentID();
                                        double bidAmount = b.getBidAmount();
                                        double balance = currentStudent.geteDollars();
                                        String bidStatus = "success";
                                        
                                        studentObject.addProperty("userid", userid);
                                        studentObject.addProperty("amount", bidAmount);
                                        studentObject.addProperty("balance", balance);
                                        studentObject.addProperty("status", bidStatus);
                                        studentArray.add(studentObject);
                                    }
                                    jsonOutput.add("students", studentArray);
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                JsonElement element = new JsonPrimitive(e.getMessage());
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
