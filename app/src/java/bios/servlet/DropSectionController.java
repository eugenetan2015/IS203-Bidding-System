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
@WebServlet(name = "DropSection", urlPatterns = {"/DropSection"})
public class DropSectionController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     * Drops a specific section from the student's cart, displays a message if section is dropped successfully
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Student user = (Student) session.getAttribute("user");
        String username = user.getStudentID();
        String course_id = request.getParameter("courseID");
        String section_id = request.getParameter("sectionID");
        SectionDAO secDao = new SectionDAO();
        double refundAmt = secDao.refundStudentBidAmount(username, course_id, section_id);
        StudentDAO stuDao = new StudentDAO();
        boolean refundSuccess = stuDao.refundStudent(username, refundAmt);
        boolean drop_success = secDao.dropSection(username, course_id, section_id);

        BidDAO bidDAO = new BidDAO();
        String[] roundStatus = bidDAO.getCurrentBidRound();
        String roundStatusStr = roundStatus[1];
        if (roundStatus[0].equals("started") && roundStatus[1].equals("2")) {
            roundStatusStr = "1";
        }
        Bid bid = bidDAO.retrieveABids(username, course_id, section_id, Integer.parseInt(roundStatusStr));
        bidDAO.dropBid(bid, Integer.parseInt(roundStatusStr));
        ArrayList<String> name_of_section = new ArrayList<>();
        name_of_section.add(course_id);
        name_of_section.add(section_id);
        request.setAttribute("nameSection", name_of_section);
        request.setAttribute("result", drop_success);
        RequestDispatcher view = request.getRequestDispatcher("viewMyClasses.jsp");
        view.forward(request, response);
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
