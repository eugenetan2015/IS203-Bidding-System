/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bios.servlet;

import bios.dao.BidDAO;
import bios.dao.SectionDAO;
import bios.dao.StudentDAO;
import bios.entity.Bid;
import bios.entity.Section;
import bios.entity.ShoppingCart;
import bios.entity.Student;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Eugene Tan
 */
@WebServlet(name = "CheckOutCart", urlPatterns = {"/CheckOutCart"})
public class CheckOutCartController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods. Checks out all the items present in the student's cart, displays
     * error message "Error: Please enter a valid number" if user enters an
     * incorrect number,"deductFailed", "Error: Not enough eDollars, please
     * re-bid" if user does not have enough EDollars. Places all the bids in the
     * checkout cart
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int n = 0;
        //for loop to look through bidAmount1 to 5
        //store as string outside declared var bidAmt 1 to 5
        //create the items for numOfBids 
        ShoppingCart sc = new ShoppingCart();
        String[] bid_values = request.getParameterValues("bidDollars");
        BidDAO bidDao = new BidDAO();
        HttpSession session = request.getSession();
        String[] roundStatus = bidDao.getCurrentBidRound();
        String status = roundStatus[0];
        int currentRound = Integer.parseInt(roundStatus[1]);
        //request.setAttribute("nb", bid_values);
        Student student = (Student) session.getAttribute("user");
        String user_id = student.getStudentID();
        ArrayList<Section> sections = sc.displayShoppingCartItems(user_id);

        ArrayList<Bid> bidsCheckedOut = new ArrayList<>();
        n++;
        StudentDAO stuDao = new StudentDAO();

        double total_in_shopCart = 0;

        for (int i = 0; i < sections.size(); i++) {
            String bidAmtString = bid_values[i]; // check here, error
            try {
                double bidAmt = Double.parseDouble(bidAmtString);

                total_in_shopCart += bidAmt;

            } catch (Exception e) {
                request.setAttribute("deductFailed", "Error: Please enter a valid number");
                RequestDispatcher view = request.getRequestDispatcher("viewMyCart.jsp"); // error here
                view.forward(request, response);
            }
        }
        double stu_balance = stuDao.retrieveEdollars(user_id);
        double if_enough_cash = stu_balance - total_in_shopCart;

        if (if_enough_cash < 0) {
            request.setAttribute("deductFailed", "Error: Not enough eDollars, please re-bid");
            RequestDispatcher view = request.getRequestDispatcher("viewMyCart.jsp"); // error here
            view.forward(request, response);
            return;
        }

        for (int i = 0; i < sections.size(); i++) {
            Section section = sections.get(i);
            String bidAmtString = bid_values[i]; // check here, error
            double bidAmt = Double.parseDouble(bidAmtString);
            stuDao.deduct(user_id, bidAmt);
            String sectID = section.getSectionID();
            String courseID = section.getCourseID();
            bidsCheckedOut.add(new Bid(stuDao.retrieveStudent(user_id), bidAmt, courseID, sectID, "Pending", currentRound));

        }

        boolean bidAdded = false;
        boolean round2Check = false;
        ArrayList<Bid> errorBids = new ArrayList<>();

        for (Bid created_bid : bidsCheckedOut) {
            if (currentRound != created_bid.getBiddingRound()) {
                round2Check = true;
            }
            SectionDAO sectionDAO = new SectionDAO();
            String courseID = created_bid.getCourseID();
            String sectID = created_bid.getSectionID();
            bidAdded = bidDao.addBid(created_bid);
            Section section = sectionDAO.retrieve(courseID, sectID);
            if (currentRound == 2) {
                ArrayList<Bid> allBidsForASection = bidDao.viewAllBidsForSection(courseID, sectID, currentRound);
                int maxSpace = section.checkVacancy();
                int filledSpace = sectionDAO.retrieveNumOfFilledSections(section.getCourseID(), section.getSectionID());
                int vacancy = maxSpace - filledSpace;
                Collections.sort(allBidsForASection);

                if (allBidsForASection.size() >= vacancy) {
                    int index = vacancy - 1;
                    double minBid = allBidsForASection.get(index).getBidAmount() + 1;
                    bidDao.updateMinimumBid(minBid, courseID, sectID);
                }
            }
            if (bidAdded == false) {
                errorBids.add(created_bid);
            }
        }
        request.setAttribute("BA", bidAdded);
        if (!round2Check) {
            request.setAttribute("errorBids", errorBids);
        }
        if (bidAdded == false) {
            RequestDispatcher view = request.getRequestDispatcher("viewMyCart.jsp"); // error here
            view.forward(request, response);
        } else {
            RequestDispatcher view = request.getRequestDispatcher("viewBidStatus.jsp"); // error here
            view.forward(request, response);
            boolean clearRes = sc.clear(user_id);
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
