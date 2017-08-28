/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bios.servlet;

import bios.dao.*;
import bios.entity.Course;
import bios.entity.Section;
import bios.entity.Student;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Admin
 */
@WebServlet(name = "searchController", urlPatterns = {"/searchController"})
public class SearchController extends HttpServlet {

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

        HttpSession session = request.getSession();
        SectionDAO sectionDAO = new SectionDAO();
        BidDAO bidDAO = new BidDAO();
        String[] roundStatus = bidDAO.getCurrentBidRound();
        //to retrieve course titles
        CourseDAO courseDAO = new CourseDAO();
        //retrieve keyword entered
        String searchWord = (String) request.getParameter("searchBox");
        Student stu = (Student) session.getAttribute("user");
        String errorMsg = "";
        if (searchWord == null || searchWord.equals("")) {
            errorMsg = "Please enter a search value";
            request.setAttribute("errorMsg", errorMsg);
            RequestDispatcher view = request.getRequestDispatcher("viewCourses.jsp");
            view.forward(request, response);
        } else {
            //get rid of blanks
            searchWord = searchWord.trim().toLowerCase();
            //retrieve category it belongs to
            String searchCategory = (String) request.getParameter("searchOption");

            ArrayList<Section> searchCourses = new ArrayList<Section>();
            ArrayList<Section> allSections = new ArrayList<>();
            if (roundStatus[1].equals("2")) {
                allSections = sectionDAO.retrieveAll();
            } else {
                allSections = sectionDAO.retrieveAllBySchool(stu.getSchool());
            }

            //runs only if there are words to be searched
            if (searchWord != null) {
                for (Section s : allSections) {
                    //searching under course code
                    if (searchCategory.equals("coursecode")) {
                        String currentCourseCode = s.getCourseID().toLowerCase();
                        if (searchWord.equals(currentCourseCode)) {
                            searchCourses.add(s);
                            errorMsg = "";
                            //break;
                        } else {
                            errorMsg = "Course code not found";
                        }
                    } else if (searchCategory.equals("coursename")) {
                        //searching under course name
                        //get coursename
                        Course course = courseDAO.retrieveCourse(s.getCourseID());
                        String courseName = course.getTitle();
                        //if user enters the entire title
                        if (courseName.equals(searchWord)) {
                            searchCourses.add(s);
                            errorMsg = "";
                            break;
                        } else {
                            HashMap<String, String> words = new HashMap<String, String>();
                            //break course name down into individual words
                            Scanner scanWords = new Scanner(courseName).useDelimiter(" ");
                            while (scanWords.hasNext()) {
                                words.put(scanWords.next().toLowerCase(), "");
                            }
                            //break search words down individually
                            ArrayList<String> searchWords = new ArrayList<String>();
                            Scanner scanSearchWords = new Scanner(searchWord).useDelimiter(" ");
                            while (scanSearchWords.hasNext()) {
                                searchWords.add(scanSearchWords.next().toLowerCase());
                            }
                            //match words
                            boolean matched = true;
                            for (String search : searchWords) {
                                if (words.get(search) == null) {
                                    matched = false;
                                    break;
                                }
                            }
                            if (matched) {
                                searchCourses.add(s);
                            } else {
                                errorMsg = "Course name does not exist";
                            }
                        }

                    }

                }
            }

            //if sections exist, set the arraylist, if not it is left as null
            if (searchCourses.size() > 0) {
                request.setAttribute("searchCourses", searchCourses);
                RequestDispatcher view = request.getRequestDispatcher("viewCourses.jsp");
                view.forward(request, response);
            } else if (!errorMsg.equals("")) {
                request.setAttribute("errorMsg", errorMsg);
                RequestDispatcher view = request.getRequestDispatcher("viewCourses.jsp");
                view.forward(request, response);
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
