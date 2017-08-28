/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bios.servlet;

import bios.bootstrap.dbcontroller.DBController;
import bios.dao.*;
import bios.entity.*;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Tan Ming Kwang
 */
@WebServlet(name = "EndRoundController", urlPatterns = {"/EndRoundController"})
public class EndRoundController extends HttpServlet {

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
        BidDAO bidDAO = new BidDAO();
        SectionDAO sectionDAO = new SectionDAO();
        //ShoppingCart cart = new ShoppingCart();
        String[] roundStatus = bidDAO.getCurrentBidRound();
        ArrayList<Bid> courseSectionList = bidDAO.retrieveCourseSectionFromBids();

        String round = roundStatus[1];

        if (roundStatus[1].equals("1")) {
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
        // dont clear the cart because we want to preserve user bid in cart
        //cart.clear();
        request.setAttribute("successMsg", "Round " + round + " stopped and clearing of bids is done");
        RequestDispatcher dispatcher = request.getRequestDispatcher("adminHome.jsp");
        dispatcher.forward(request, response);
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
