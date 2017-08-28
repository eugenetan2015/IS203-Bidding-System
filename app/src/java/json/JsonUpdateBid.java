/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import bios.bootstrap.util.ValidationUtility;
import bios.dao.BidDAO;
import bios.dao.SectionDAO;
import bios.dao.StudentDAO;
import bios.entity.Bid;
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
import java.util.HashMap;
import java.util.TreeMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Wilson
 */
@WebServlet(name = "JsonUpdateBid", urlPatterns = {"/update-bid", "/json/update-bid"})
public class JsonUpdateBid extends HttpServlet {

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
            TreeMap<String, String> map = null;

            JsonArray errorMessageArray = new JsonArray();

            // set json property as error first because the property will override with the latest property set
            jsonOutput.addProperty("status", "error");

            map = vu.checkForParameterNull(jsonReq, token);

            if (map.size() == 0) {
                //used to retrieve the json file parsed
                JsonParser jp = new JsonParser();
                try {
                    JsonElement root = jp.parse(jsonReq);

                    JsonObject rootObj = root.getAsJsonObject();
                    JsonElement userIDElement = rootObj.get("userid");
                    JsonElement amountElement = rootObj.get("amount");
                    JsonElement courseElement = rootObj.get("course");
                    JsonElement sectionElement = rootObj.get("section");
                    map = vu.validJSONParameters(userIDElement, amountElement, courseElement, sectionElement);

                    if (map.isEmpty()) {
                        String userID = userIDElement.getAsString().toLowerCase();
                        String courseID = courseElement.getAsString().toUpperCase();
                        String amount = amountElement.getAsString();
                        String sectionID = sectionElement.getAsString().toUpperCase();

                        map = vu.checkInvalid(userID, amount, courseID, sectionID);

                        if (map.isEmpty()) {
                            double amountDouble = Double.parseDouble(amount);
                            map = vu.checkLogicalValidation(userID, amountDouble, courseID, sectionID);

                            if (map.isEmpty()) {
                                jsonOutput.addProperty("status", "success");
                                BidDAO bidDAO = new BidDAO();
                                StudentDAO studentDAO = new StudentDAO();
                                Student student = studentDAO.retrieveStudent(userID);
                                
                                String[] roundStatus = bidDAO.getCurrentBidRound();
                                int round = Integer.parseInt(roundStatus[1]);

                                boolean bidStatus = false;

                                if (round == 1) {
                                    bidStatus = bidDAO.addBid(new Bid(student, amountDouble, courseID, sectionID, "Pending", round));
                                } else {
                                    bidStatus = bidDAO.addBid(new Bid(student, amountDouble, courseID, sectionID, "Pending", round));
                                    SectionDAO sectDAO = new SectionDAO();
                                    Section section = sectDAO.retrieve(courseID, sectionID);
                                    ArrayList<Bid> allBidsForASection = bidDAO.viewAllBidsForSection(courseID, sectionID, round);
                                    int maxSpace = section.checkVacancy();
                                    int filledSpace = sectDAO.retrieveNumOfFilledSections(section.getCourseID(), section.getSectionID());
                                    int vacancy = maxSpace - filledSpace;
                                    Collections.sort(allBidsForASection);

                                    if (allBidsForASection.size() >= vacancy) {
                                        int index = vacancy - 1;
                                        double minBid = allBidsForASection.get(index).getBidAmount() + 1;
                                        bidDAO.updateMinimumBid(minBid, courseID, sectionID);
                                    }

                                }

                                cancelAndRebid(userID, courseID, sectionID, amountDouble, round);
                                
                                // deduct from new bid
                                if (bidStatus) {
                                    studentDAO.deduct(userID, amountDouble);
                                } else {
                                    // existing bids, update bid amount
                                    // this is the current bid amt in bidding cart
                                    double currentBidAmt = bidDAO.retrieveBidAmountOfUser(userID, courseID, sectionID);
                                    bidDAO.updateStudentBid(userID, courseID, sectionID, currentBidAmt, amountDouble);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    JsonPrimitive element = new JsonPrimitive("invalid r");
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

    /**
     *
     * @param userID Takes in an user_id of String type
     * @param courseID Takes in an courseID of String type
     * @param sectionID Takes in an sectionID of String type
     * @param amount Takes in an amount of Double type
     * @param round Takes in the round of int type
     */
    public static void cancelAndRebid(String userID, String courseID, String sectionID, double amount, int round) {
        // Update studentBid
        SectionDAO sectDAO = new SectionDAO();
        StudentDAO studentDAO = new StudentDAO();
        BidDAO bidDAO = new BidDAO();
        Student student = studentDAO.retrieveStudent(userID);
        ArrayList<Bid> studentBids = bidDAO.viewStudentBids(userID);
        for (Bid b : studentBids) {
            String currentCourseID = b.getCourseID();
            String currentSectionID = b.getSectionID();
            if (currentCourseID.equals(courseID) && !b.getSectionID().equals(sectionID)) {
                double amtToRefund = sectDAO.refundStudentBidAmount2(userID, courseID, sectionID);
                studentDAO.refundStudent(userID, amtToRefund);
                bidDAO.dropBid(b, round);
                bidDAO.addBid(new Bid(student, amount, courseID, sectionID, "Pending", round));
                studentDAO.deduct(userID, amount);
            }
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
