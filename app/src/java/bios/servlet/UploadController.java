/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bios.servlet;

import bios.bootstrap.dbcontroller.DBController;
import bios.bootstrap.util.AdminUtility;
import bios.bootstrap.util.FileUtility;
import bios.bootstrap.validation.*;
import bios.dao.BidDAO;
import bios.dao.StudentTimeTableDAO;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Wilson
 */
@WebServlet(name = "UploadController", urlPatterns = {"/upload", "/app/upload"})
@MultipartConfig
public class UploadController extends HttpServlet {

    FileUtility fileUtility = null;

    /**
     *
     */
    public void init() {
        fileUtility = new FileUtility();
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods. Displays error message if user does not enter any input if
     * object is null - displays "Please select a zip file to upload" if object
     * is in the wrong file format - displays "Invalid file format (Acceptable
     * file format: .zip)" else clears the database and uploads the zip folder
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // if user did not enter any input, display error
        // pass in the request object, rename uploads to change directory name
        try {
            String obj = fileUtility.uploadFileForUI(request, "uploads");

            if (obj == null) {
                request.setAttribute("errorMsg", "Please select a zip file to upload");
            } else if (obj.equals("invalid")) {
                request.setAttribute("errorMsg", "Invalid file format (Acceptable file format: .zip)");
            } else {
                // clearDB();
                resetAllLists();
                DBController.initialize();
                DBController.updateRoundStatus(1, "started");

                ArrayList<String> studentErrorList = startValidationStudent(request);
                request.setAttribute("studErrorList", studentErrorList);
                request.setAttribute("StudentErrorMap", StudentValidation.getErrorMap());

                ArrayList<String> courseErrorList = startValidationCourse(request);
                request.setAttribute("courseErrorList", courseErrorList);
                request.setAttribute("CourseErrorMap", CourseValidation.getErrorMap());

                ArrayList<String> sectionErrorList = startValidationSection(request);
                request.setAttribute("sectionErrorList", sectionErrorList);
                request.setAttribute("SectionErrorMap", SectionValidation.getErrorMap());

                ArrayList<String> prerequisiteErrorList = startValidationPrerequisite(request);
                request.setAttribute("prerequisiteErrorList", prerequisiteErrorList);
                request.setAttribute("PreReqErrorMap", PrerequisiteValidation.getErrorMap());

                ArrayList<String> courseCompletedErrorList = startValidationCourseCompleted(request);
                request.setAttribute("courseCompletedErrorList", courseCompletedErrorList);
                request.setAttribute("CCErrorMap", CourseCompletedValidation.getErrorMap());

                ArrayList<String> bidErrorList = startValidationBid(request);
                request.setAttribute("bidErrorList", bidErrorList);
                request.setAttribute("BidErrorMap", BidValidation.getErrorMap());

                createStartDate(request);
                
                // Remove all the folders and csv files
                fileUtility.cleanUpAfterBootstrap(fileUtility.getFilePath(), ".csv");
                request.setAttribute("successMsg", "Successfully uploaded zip file onto server. Round 1 has started.");
            }
        } catch (Exception ex) {
            request.setAttribute("errorMsg", ex.getMessage());
        }

        RequestDispatcher view = request.getRequestDispatcher("BootStrapUI.jsp");

        view.forward(request, response);
    }

    /**
     * Validates all the courses within the zip folder for bootstrapping Inserts
     * all the courses without any errors into the database
     *
     * @param request - HttpServletRequest
     * @return list of courses with error during validation
     */
    public ArrayList<String> startValidationCourse(HttpServletRequest request) {
        CourseValidation.validate(fileUtility.getFilePath());
        ArrayList<String[]> okayCourseList = CourseValidation.getOkayList();
        ArrayList<String> errorCourseList = CourseValidation.getErrorList();
        //insert into DB & create error log
        DBController.insertCourse(okayCourseList);
        request.setAttribute("numOfSuccessCourse", okayCourseList.size());
        return errorCourseList;
    }

    /**
     * Validates all the Students within the zip folder for bootstrapping
     * Inserts all the students without any errors into the database
     *
     * @param request - HttpServletRequest
     * @return - list of student with error during validation
     */
    public ArrayList<String> startValidationStudent(HttpServletRequest request) {
        StudentValidation.validate(fileUtility.getFilePath());
        ArrayList<String[]> okayStudentList = StudentValidation.getOkayList();
        ArrayList<String> errorStudentList = StudentValidation.getErrorList();
        //insert into DB & create error log
        DBController.insertStudent(okayStudentList);
        request.setAttribute("numOfSuccessStudent", okayStudentList.size());
        return errorStudentList;
    }

    /**
     * Validates all the Sections within the zip folder for bootstrapping
     * Inserts all the sections without any errors into the database
     *
     * @param request - HttpServletRequest
     * @return list of sections with errors during validation
     */
    public ArrayList<String> startValidationSection(HttpServletRequest request) {
        SectionValidation.validate(fileUtility.getFilePath());
        ArrayList<String[]> okaySectionList = SectionValidation.getOkayList();
        ArrayList<String> errorSectionList = SectionValidation.getErrorList();
        //insert into DB & create error log
        DBController.insertSection(okaySectionList);
        request.setAttribute("numOfSuccessSection", okaySectionList.size());
        return errorSectionList;
    }

    /**
     * Validates all the Prerequisite within the zip folder for bootstrapping
     * Inserts all the prerequisites without any errors into the database
     *
     * @param request - HttpServletRequest
     * @return list of prerequisite with errors during validation
     */
    public ArrayList<String> startValidationPrerequisite(HttpServletRequest request) {
        PrerequisiteValidation.validate(fileUtility.getFilePath());
        ArrayList<String[]> okayPrerequisiteList = PrerequisiteValidation.getOkayList();
        ArrayList<String> errorPrerequisiteList = PrerequisiteValidation.getErrorList();
        //insert into DB & create error log
        DBController.insertPrerequisite(okayPrerequisiteList);
        request.setAttribute("numOfSuccessPrerequisite", okayPrerequisiteList.size());
        return errorPrerequisiteList;
    }

    /**
     * Validates all the Bids within the zip folder for bootstrapping Inserts
     * all the Bids without any errors into the database
     *
     * @param request - HttpServletRequest
     * @return list of bid with errors during validation
     */
    public ArrayList<String> startValidationBid(HttpServletRequest request) {
        BidValidation.validate(fileUtility.getFilePath());
        ArrayList<String[]> okayBidList = BidValidation.getOkayList();
        ArrayList<String> errorBidList = BidValidation.getErrorList();
        BidDAO bidDAO = new BidDAO();
        String[] currentRound = bidDAO.getCurrentBidRound();
        //insert into DB & create error log
        DBController.insertBid(okayBidList, Integer.parseInt(currentRound[1]));
        request.setAttribute("numOfSuccessBid", BidValidation.getNumSuccessfulBids());
        return errorBidList;
    }

    /**
     * Validates all the completed courses within the zip folder for
     * bootstrapping Inserts all the completed courses without any errors into
     * the database
     *
     * @param request - HttpServletRequest
     * @return list of course completed with errors during validation
     */
    public ArrayList<String> startValidationCourseCompleted(HttpServletRequest request) {
        CourseCompletedValidation.validate(fileUtility.getFilePath());
        ArrayList<String[]> okayCourseCompletedList = CourseCompletedValidation.getOkayList();
        ArrayList<String> errorCourseCompletedList = CourseCompletedValidation.getErrorList();
        //insert into DB & create error log
        DBController.insertCourseCompleted(okayCourseCompletedList);
        request.setAttribute("numOfSuccessCourseCompleted", okayCourseCompletedList.size());
        return errorCourseCompletedList;
    }

    /**
     * Create Start Date for the current semester (A semester last until the
     * last exam date) Start Date is determined by subtracting 14 weeks from the
     * earliest examDate.
     * After the start date have been inserted into database, create the directory to
     * store JSON files for each student.
     */
    private void createStartDate(HttpServletRequest request) {
        StudentTimeTableDAO studentTimeTableDAO = new StudentTimeTableDAO();
        ArrayList<Date> examDateList = studentTimeTableDAO.getExamDates();
        Date examDate = studentTimeTableDAO.findEarliestExamDate(examDateList);
        Calendar c = Calendar.getInstance();
        c.setTime(examDate);
        Date latest = studentTimeTableDAO.createFromDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        c.setTime(latest);
        c.add(Calendar.WEEK_OF_YEAR, -14);
        StudentTimeTableDAO studentTimetableDAO = new StudentTimeTableDAO();
        studentTimetableDAO.addDate(c.getTime());
        
        String rootPath = request.getServletContext().getRealPath("/");
        fileUtility.createDirectory(rootPath + File.separator + AdminUtility.getLocalTimetableDir());

    }

    /**
     * Clears all the list in StudentValidation, CourseValidation,
     * SectionValidation, PrerequisiteValidation, CourseCompletedValidation and
     * BidValidation
     */
    public void resetAllLists() {
        StudentValidation.resetAllList();
        CourseValidation.resetAllList();
        SectionValidation.resetAllList();
        PrerequisiteValidation.resetAllList();
        CourseCompletedValidation.resetAllList();
        BidValidation.resetAllList();
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
