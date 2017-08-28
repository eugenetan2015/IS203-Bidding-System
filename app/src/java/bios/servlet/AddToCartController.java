/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bios.servlet;

import bios.dao.BidDAO;
import bios.dao.SectionDAO;
import bios.entity.Bid;
import bios.entity.Section;
import bios.entity.ShoppingCart;
import bios.entity.ShoppingCartItem;
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
@WebServlet(name = "AddToCartController", urlPatterns = {"/AddToCartController"})
public class AddToCartController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     * Checks if student ID, course and section and Bid exists
     * Creates and adds a Shopping cart item to the shopping cart object should the if the following validations are valid:
     * Course does not exist in cart, Student is not Already enrolled in course, Student has not reached maximum limit in cart, Student has not completed course, Student has completed prerequisite, Class timetable does not clash
     * Course exam dates does not clash, Bidded course timetable does not clash, Bidded course exam dates does not clash, Number of bids/items in cart has not reach 5
     * Checks the following validation: wWHet
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //response.setContentType("text/html;charset=UTF-8");
        //int n = 0;
        try {
            HttpSession session = request.getSession();
            String course_id = (String) request.getParameter("courseID");
            String section_id = (String) request.getParameter("sectionID");
            Student student = (Student) session.getAttribute("user");
            if (course_id != null && section_id != null && student != null) {
                SectionDAO secDao = new SectionDAO();
                BidDAO bidDAO = new BidDAO();
                ArrayList<String> errorMsg = new ArrayList<>();

                Section sectionToAdd = secDao.retrieve(course_id, section_id);

                ArrayList<Bid> studentBids = bidDAO.viewStudentBids(student.getStudentID());
                boolean checkIfBidExist = false;
                for (Bid b : studentBids) {
                    String bidSectionID = b.getSectionID();
                    String bidCourseID = b.getCourseID();
                    if (bidSectionID.equals(section_id) && bidCourseID.equals(course_id) && b.getStatus().equals("Pending")) {
                        checkIfBidExist = true;
                        errorMsg.add("Error: Bid already exist");
                    }
                }
                ShoppingCartItem li = new ShoppingCartItem(student, sectionToAdd);
                ShoppingCart shopCart = new ShoppingCart();
                boolean checkCourseInCart = shopCart.checkCourseInCart(li); //true if exist in cart
                boolean checkNum = shopCart.checkCart(li);//true if less than 5
                boolean checkCourseCompleted = shopCart.checkCourseCompleted(li);//true = complete
                boolean checkPreReq = shopCart.checkPreReq(li);//true = user completed all prereq
                boolean checkIfTimeTableClash = shopCart.checkIfTimeTableClash(student.getStudentID(), section_id, course_id,true); //true if clash
                boolean checkIfExamDateClash = shopCart.checkIfExamClash(student.getStudentID(), course_id, true); //true if clash
                boolean checkIfTimeTableClashWithBids = bidDAO.checkIfTimeTableClash(student.getStudentID(), section_id, course_id); //true if clash
                boolean checkIfExamDateClashWithBids = bidDAO.checkIfExamClash(student.getStudentID(), course_id); //true if clash
                boolean checkNumBids = shopCart.countBidsAndCart(student.getStudentID()); //true if <= 5
                boolean checkIfSectionExists = shopCart.checkIfSectionEnrolled(li);
                boolean checkIfSectionTimetableClash = shopCart.checkIfTimeTableClash(student.getStudentID(), section_id, course_id, false);
                boolean checkIfSectionExamDateClash = shopCart.checkIfExamClash(student.getStudentID(), course_id, false);
                boolean check = false;
                if (!checkCourseInCart && checkNum && !checkCourseCompleted && checkPreReq && 
                        !checkIfBidExist && !checkIfTimeTableClash && !checkIfExamDateClash && 
                        !checkIfTimeTableClashWithBids && !checkIfExamDateClashWithBids && checkNumBids && 
                        !checkIfSectionExists && !checkIfSectionTimetableClash && !checkIfSectionExamDateClash) {
                    check = shopCart.addShoppingCartItem(li); 
                }
                if (checkCourseInCart) {
                    errorMsg.add("Error: Course already exist in cart");
                }
                if (checkIfSectionExists) {
                    errorMsg.add("Error: Already enrolled in course");
                }
                if (!checkNum) {
                    errorMsg.add("Error: Maximum limit in cart");
                }
                if (checkCourseCompleted) {
                    errorMsg.add("Error: Already completed course");
                }
                if (!checkPreReq) {
                    errorMsg.add("Error: Prerequisite not completed yet");
                }
                if (checkIfTimeTableClash || checkIfSectionTimetableClash) {
                    errorMsg.add("Error: Class timetable clash");
                }
                if (checkIfExamDateClash || checkIfSectionExamDateClash) {
                    errorMsg.add("Error: Course exam dates clash");
                }
                if (checkIfTimeTableClashWithBids) {
                    errorMsg.add("Error: Bidded course timetable clash");
                }
                if (checkIfExamDateClashWithBids) {
                    errorMsg.add("Error: Bidded course exam dates clash");
                }
                if (!checkNumBids) {
                    errorMsg.add("Error: Maximum of 5 section reached");
                }
                if (check) {
                    request.setAttribute("addItem", li);
                }
                request.setAttribute("errorMsgs", errorMsg);
            }
            RequestDispatcher view = request.getRequestDispatcher("viewCourses.jsp"); // error here
            view.forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
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
