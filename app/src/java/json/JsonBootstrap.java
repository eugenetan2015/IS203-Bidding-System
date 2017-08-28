/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import bios.bootstrap.dbcontroller.DBController;
import bios.bootstrap.util.FileUtility;
import bios.bootstrap.validation.BidValidation;
import bios.bootstrap.validation.CourseCompletedValidation;
import bios.bootstrap.validation.CourseValidation;
import bios.bootstrap.validation.PrerequisiteValidation;
import bios.bootstrap.validation.SectionValidation;
import bios.bootstrap.validation.StudentValidation;
import bios.dao.BidDAO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
@WebServlet(name = "JsonBootstrap", urlPatterns = {"/bootstrap", "/json/bootstrap"})

public class JsonBootstrap extends HttpServlet {

    FileUtility fileUtility = null;

    /**
     *
     */
    public void init() {
        fileUtility = new FileUtility();
    }

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
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject output = new JsonObject();

        JsonArray messageArray = fileUtility.uploadFile(request, "uploads");

        if (messageArray.size() == 0) {
            resetAllLists();
            DBController.initialize();
            //start round
            DBController.updateRoundStatus(1, "started");
            
            HashMap<List<String>, ArrayList<String>> studentMap = startValidationStudent();
            HashMap<List<String>, ArrayList<String>> courseMap = startValidationCourse();
            HashMap<List<String>, ArrayList<String>> sectionMap = startValidationSection();
            HashMap<List<String>, ArrayList<String>> prerequisiteMap = startValidationPrerequisite();
            HashMap<List<String>, ArrayList<String>> courseCompletedMap = startValidationCourseCompleted();
            HashMap<List<String>, ArrayList<String>> bidMap = startValidationBid();

            HashMap<List<String>, ArrayList<String>> errorMap = new HashMap<>();
            errorMap.putAll(bidMap);
            errorMap.putAll(courseCompletedMap);
            errorMap.putAll(prerequisiteMap);
            errorMap.putAll(sectionMap);
            errorMap.putAll(courseMap);
            errorMap.putAll(studentMap);

            //to sort errorMap -> alphabetical order for filenames, row number next
            Comparator<List<String>> bootstrapComparator = new Comparator<List<String>>() {
                @Override
                public int compare(List<String> a, List<String> b) {
                    String[] aArray = new String[a.size()];
                    String[] bArray = new String[b.size()];

                    aArray = a.toArray(aArray);
                    bArray = b.toArray(bArray);

                    String aFilename = aArray[0];
                    String bFilename = bArray[0];

                    if (aFilename.compareTo(bFilename) != 0) {
                        return aFilename.compareTo(bFilename);
                    } else {
                        Integer aRowNum = Integer.parseInt(aArray[1]);
                        Integer bRowNum = Integer.parseInt(bArray[1]);

                        return aRowNum.compareTo(bRowNum);
                    }
                }
            };

            Map<List<String>, ArrayList<String>> sortedMap = new TreeMap<List<String>, ArrayList<String>>(bootstrapComparator);
            sortedMap.putAll(errorMap);

            boolean checkIfAnyError = sortedMap.isEmpty();
            if (!checkIfAnyError) {
                //got error
                output.addProperty("status", "error");
                //add in successful records
                JsonArray successArray = addToJSONOutputInsert();
                output.add("num-record-loaded", successArray);
                //add in errors
                JsonArray errorArray = addToJSONOutput(sortedMap);
                output.add("error", errorArray);

            } else {
                //no error
                output.addProperty("status", "success");
                //add in successful records
                JsonArray successArray = addToJSONOutputInsert();
                output.add("num-record-loaded", successArray);
            }

            // Remove all the folders and csv files
            fileUtility.cleanUpAfterBootstrap(fileUtility.getFilePath(), ".csv");
        }
        
        if (messageArray.size() > 0) {
            output.add("message", messageArray);
        }

        out.print(gson.toJson(output));
    }

    /**
     *
     * @return the HashMap which contained all the errors for Course during validation
     */
    public HashMap<List<String>, ArrayList<String>> startValidationCourse() {
        CourseValidation.validate(fileUtility.getFilePath());
        ArrayList<String[]> okayCourseList = CourseValidation.getOkayList();
        HashMap<List<String>, ArrayList<String>> errorCourseMap = CourseValidation.getErrorMap();
        //insert into DB & create error log
        DBController.insertCourse(okayCourseList);
        return errorCourseMap;
    }

    /**
     *
     * @return the HashMap which contained all the errors for Student during validation
     */
    public HashMap<List<String>, ArrayList<String>> startValidationStudent() {
        StudentValidation.validate(fileUtility.getFilePath());
        ArrayList<String[]> okayStudentList = StudentValidation.getOkayList();
        HashMap<List<String>, ArrayList<String>> errorStudentMap = StudentValidation.getErrorMap();
        //insert into DB & create error log
        DBController.insertStudent(okayStudentList);
        return errorStudentMap;
    }

    /**
     *
     * @return the HashMap which contained all the errors for Section during validation
     */
    public HashMap<List<String>, ArrayList<String>> startValidationSection() {
        SectionValidation.validate(fileUtility.getFilePath());
        ArrayList<String[]> okaySectionList = SectionValidation.getOkayList();
        HashMap<List<String>, ArrayList<String>> errorSectionMap = SectionValidation.getErrorMap();
        //insert into DB & create error log
        DBController.insertSection(okaySectionList);
        return errorSectionMap;
    }

    /**
     *
     * @return the HashMap which contained all the errors for Prerequisite during validation
     */
    public HashMap<List<String>, ArrayList<String>> startValidationPrerequisite() {
        PrerequisiteValidation.validate(fileUtility.getFilePath());
        ArrayList<String[]> okayPrerequisiteList = PrerequisiteValidation.getOkayList();
        HashMap<List<String>, ArrayList<String>> errorPrerequisiteMap = PrerequisiteValidation.getErrorMap();
        //insert into DB & create error log
        DBController.insertPrerequisite(okayPrerequisiteList);
        return errorPrerequisiteMap;
    }

    /**
     *
     * @return the HashMap which contained all the errors for Bid during validation
     */
    public HashMap<List<String>, ArrayList<String>> startValidationBid() {
        BidValidation.validate(fileUtility.getFilePath());
        ArrayList<String[]> okayBidList = BidValidation.getOkayList();
        HashMap<List<String>, ArrayList<String>> errorBidMap = BidValidation.getErrorMap();
        BidDAO bidDAO = new BidDAO();
        String[] currentRound = bidDAO.getCurrentBidRound();
        //insert into DB & create error log
        DBController.insertBid(okayBidList, Integer.parseInt(currentRound[1]));
        return errorBidMap;
    }

    /**
     *
     * @return the HashMap which contained all the errors for Course_Completed during validation
     */
    public HashMap<List<String>, ArrayList<String>> startValidationCourseCompleted() {
        CourseCompletedValidation.validate(fileUtility.getFilePath());
        ArrayList<String[]> okayCourseCompletedList = CourseCompletedValidation.getOkayList();
        HashMap<List<String>, ArrayList<String>> errorCourseCompletedMap = CourseCompletedValidation.getErrorMap();
        //insert into DB & create error log
        DBController.insertCourseCompleted(okayCourseCompletedList);
        return errorCourseCompletedMap;
    }

    /**
     *
     */
    public void resetAllLists() {
        StudentValidation.resetAllList();
        CourseValidation.resetAllList();
        SectionValidation.resetAllList();
        PrerequisiteValidation.resetAllList();
        CourseCompletedValidation.resetAllList();
        BidValidation.resetAllList();
    }

    /**
     *
     * @param map Takes in a Map with parameter (ArrayList, ArrayList)
     * @return a JsonArray of errors added to the Array
     */
    public JsonArray addToJSONOutput(Map<List<String>, ArrayList<String>> map) {
        Iterator iter = map.entrySet().iterator();
        JsonArray toReturn = new JsonArray();

        while (iter.hasNext()) {
            JsonObject output = new JsonObject();
            Map.Entry pair = (Map.Entry) iter.next();
            List<String> keyList = (List<String>) pair.getKey();
            String[] key = new String[keyList.size()];
            key = keyList.toArray(key);
            output.addProperty("file", key[0]);
            output.addProperty("line", Integer.parseInt(key[1]));
            ArrayList<String> errors = (ArrayList<String>) pair.getValue();
            //Collections.sort(errors);
            JsonArray messageArray = new JsonArray();
            for (String error : errors) {
                JsonPrimitive element = new JsonPrimitive(error);
                messageArray.add(element);
            }
            output.add("message", messageArray);
            toReturn.add(output);
        }
        return toReturn;
    }

    /**
     *
     * @return a JsonArray that contained all the Json Object which included all the number of Successful bids
     */
    public JsonArray addToJSONOutputInsert() {
        JsonArray toReturn = new JsonArray();
        ArrayList<String[]> okayStudentList = StudentValidation.getOkayList();
        ArrayList<String[]> okayCourseList = CourseValidation.getOkayList();
        ArrayList<String[]> okaySectionList = SectionValidation.getOkayList();
        ArrayList<String[]> okayPrerequisiteList = PrerequisiteValidation.getOkayList();
        ArrayList<String[]> okayCourseCompletedList = CourseCompletedValidation.getOkayList();
        ArrayList<String[]> okayBidList = BidValidation.getOkayList();

        int numOfBidsAdded = okayBidList.size();
        int numOfCourseAdded = okayCourseList.size();
        int numOfCourseCompletedAdded = okayCourseCompletedList.size();
        int numOfPrerequisiteAdded = okayPrerequisiteList.size();
        int numOfSectionAdded = okaySectionList.size();
        int numOfStudentAdded = okayStudentList.size();

        JsonObject bids = new JsonObject();
        JsonObject courses = new JsonObject();
        JsonObject coursescompleted = new JsonObject();
        JsonObject prerequisites = new JsonObject();
        JsonObject sections = new JsonObject();
        JsonObject students = new JsonObject();

        bids.addProperty("bid.csv", BidValidation.getNumSuccessfulBids());
        courses.addProperty("course.csv", numOfCourseAdded);
        coursescompleted.addProperty("course_completed.csv", numOfCourseCompletedAdded);
        prerequisites.addProperty("prerequisite.csv", numOfPrerequisiteAdded);
        sections.addProperty("section.csv", numOfSectionAdded);
        students.addProperty("student.csv", numOfStudentAdded);

        toReturn.add(bids);
        toReturn.add(courses);
        toReturn.add(coursescompleted);
        toReturn.add(prerequisites);
        toReturn.add(sections);
        toReturn.add(students);

        return toReturn;
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
        //processRequest(request, response);
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
