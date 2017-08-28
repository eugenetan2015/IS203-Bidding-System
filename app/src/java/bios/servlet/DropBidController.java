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
import bios.entity.Student;
import java.io.IOException;
import java.util.ArrayList;
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
@WebServlet(name = "DropBidController", urlPatterns = {"/DropBidController"})
public class DropBidController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     * Drops the student's bid from the his cart and refunds the full amount to his cart
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Student user = (Student) session.getAttribute("user");
        String studentID = user.getStudentID();
        String course_id = (String) request.getParameter("CourseID");
        BidDAO bidDao = new BidDAO();
        SectionDAO sectionDAO = new SectionDAO();
        String[] roundStatus = bidDao.getCurrentBidRound();
        ArrayList<Bid> allStudentBids = bidDao.viewStudentBids(studentID);
        
        boolean resOfDrop = false;
        double refundAmt = 0;
        StudentDAO stuDao = new StudentDAO();
        boolean refund_success = false;
        String c_id = "";
        String s_id = "";
        
        for(Bid bid : allStudentBids){  
            String compareId = bid.getCourseID();
            if(compareId.equals(course_id)){
                c_id = bid.getCourseID();
                s_id = bid.getSectionID();
                refundAmt =  bid.getBidAmount();
                resOfDrop = bidDao.dropBid(bid, Integer.parseInt(roundStatus[1]));
                refund_success = stuDao.refundStudent(studentID, refundAmt);
            }
        }
        request.setAttribute("course_id", c_id);
        request.setAttribute("section_id", s_id);
        request.setAttribute("bidAmt", refundAmt);
        
        if(resOfDrop && refund_success){
            request.setAttribute("success", true);
            RequestDispatcher view = request.getRequestDispatcher("viewBidStatus.jsp"); // error here
            view.forward(request, response);
        }else{
            request.setAttribute("success", false);
            RequestDispatcher view = request.getRequestDispatcher("viewBidStatus.jsp"); // error here
            view.forward(request, response);
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
