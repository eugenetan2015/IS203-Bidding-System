/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bios.servlet;

import bios.dao.*;
import bios.entity.Student;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Tan Ming Kwang
 */
@WebServlet(name = "LoginController", urlPatterns = {"/LoginController"})
public class LoginController extends HttpServlet {

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
        // initial declarations
        HttpSession session = request.getSession();
        String userName = request.getParameter("username");
        String password = request.getParameter("password");
        LoginDAO loginDAO = new LoginDAO();
        StudentDAO studentDAO = new StudentDAO();
        // more codes here for you to fill

        boolean status = loginDAO.authenticate(userName, password);
        if (loginDAO.verifyAdmin(userName, password)) {
            Student user = studentDAO.retrieveStudent(userName);
            session.setAttribute("user", user);
            session.setAttribute("type", "admin");

            response.sendRedirect("adminHome.jsp");
        }
        else if (status) {
            Student user = studentDAO.retrieveStudent(userName);
            session.setAttribute("user", user);

            response.sendRedirect("userHome.jsp");
        }  else if (userName == null || (userName.equals(""))) {
            String errorMsg = "Please enter your username";
            request.setAttribute("errorMsg", errorMsg);
            RequestDispatcher view = request.getRequestDispatcher("index.jsp");
            view.forward(request, response);

        } else if (password == null || (password.equals(""))) {
            String errorMsg = "Please enter your password";
            request.setAttribute("errorMsg", errorMsg);
            request.setAttribute("user", userName);
            RequestDispatcher view = request.getRequestDispatcher("index.jsp");
            view.forward(request, response);

        } else {
            String errorMsg = "Invalid username/password";
            request.setAttribute("errorMsg", errorMsg);
            RequestDispatcher view = request.getRequestDispatcher("index.jsp");
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
