/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import bios.bootstrap.dbcontroller.DBController;
import bios.bootstrap.util.ValidationUtility;
import bios.dao.BidDAO;
import bios.dao.SectionDAO;
import bios.entity.Bid;
import bios.entity.Section;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
 * @author Tan Ming Kwang
 */
@WebServlet(name = "JsonStopRound", urlPatterns = {"/stop", "/json/stop"})
public class JsonStopRound extends HttpServlet {

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
        String token = request.getParameter("token");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jsonOutput = new JsonObject();
        JsonArray errorMessageArray = new JsonArray();

        ValidationUtility vu = new ValidationUtility();

        TreeMap<String, String> map = vu.checkForParameterNull(token);
        jsonOutput.addProperty("status", "error");

        if (map.isEmpty()) {
            BidDAO bidDAO = new BidDAO();
            String[] roundStatus = bidDAO.getCurrentBidRound();
            String status = roundStatus[0];
            String round = roundStatus[1];

            if (status.equals("started")) {
                SectionDAO sectionDAO = new SectionDAO();
                //ShoppingCart cart = new ShoppingCart();
                ArrayList<Bid> courseSectionList = bidDAO.retrieveCourseSectionFromBids();

                if (round.equals("1")) {
                    for (Bid bid : courseSectionList) {
                        String sectionID = bid.getSectionID();
                        String courseID = bid.getCourseID();
                        ArrayList<Bid> allBids = bidDAO.viewAllBidsForSection(courseID, sectionID, Integer.parseInt(round));
                        Section sect = sectionDAO.retrieve(courseID, sectionID);
                        bidDAO.generateBidsRound(allBids, sect, Integer.parseInt(round));
                    }
                    DBController.updateRoundStatus(1, "ended");
                } else {
                    for (Bid bid : courseSectionList) {
                        String sectionID = bid.getSectionID();
                        String courseID = bid.getCourseID();
                        ArrayList<Bid> allBids = bidDAO.viewAllBidsForSection(courseID, sectionID, Integer.parseInt(round));
                        Section sect = sectionDAO.retrieve(courseID, sectionID);
                        bidDAO.generateBidsRound(allBids, sect, Integer.parseInt(round));
                    }
                    DBController.updateRoundStatus(2, "ended");
                }
                //cart.clear();
                jsonOutput.addProperty("status", "success");
            } else {
                JsonPrimitive element = new JsonPrimitive("round already ended");
                errorMessageArray.add(element);
                jsonOutput.add("message", errorMessageArray);
            }

            if (map != null) {
                if (map.size() > 0) {
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
