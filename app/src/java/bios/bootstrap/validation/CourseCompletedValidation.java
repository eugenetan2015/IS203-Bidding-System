/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bios.bootstrap.validation;

import bios.dao.ConnectionManager;
import bios.entity.Course;
import java.util.*;
import com.opencsv.*;
import java.io.*;
import java.sql.*;

/**
 *
 * @author Tan Ming Kwang
 */
public class CourseCompletedValidation {

    private static ArrayList<String> errorList = new ArrayList<String>();
    private static ArrayList<String[]> okayList = new ArrayList<String[]>();
    private static HashMap<List<String>, ArrayList<String>> errorMap = new HashMap<List<String>, ArrayList<String>>();

    /**
     * Checks if the file has any blanks in the rows. If blanks are
     * present, it adds the error into an error log to be released containing
     * its row number and column blank.
     *
     * @param filePath the file name to be entered
     */
    public static void validate(String filePath) {

        filePath = filePath + "course_completed.csv";
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(filePath));
            int row = 2;
            String[] nextLine;
            String[] headers = reader.readNext();
            while ((nextLine = reader.readNext()) != null) {
                List<Integer> errorBlanksColumn = checkForBlanks(nextLine);
                if (errorBlanksColumn.size() > 0) {
                    addError(errorBlanksColumn, "blank", row);
                    addErrorToMap(errorBlanksColumn, "blank", row);
                } else {
                    secondaryValidation(nextLine, row);
                }
                row++;
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the entry in each field is blank.
     *
     * @param eachRow is an array storing all entries for one row of the file
     * being checked
     * @return an List of Integer that contains column numbers that are blank
     * for the field
     */
    public static List<Integer> checkForBlanks(String[] eachRow) {

        List<Integer> toReturn = new ArrayList<>();
        for (int i = 0; i < eachRow.length; i++) {
            if (eachRow[i].equals("") || eachRow[i] == null) {
                toReturn.add(i);
            }
        }
        return toReturn;
    }

    /**
     * Crafts the error message for each field to return.
     *
     * @param errorColumn is a list of integer that contains the error columns
     * @param errorType is a string describing the type of error
     * @param rowNum is the row number of the row which the error exists at
     */
    public static void addError(List<Integer> errorColumn, String errorType, int rowNum) {

        if (errorColumn != null && errorColumn.size() > 0) {
            for (Integer i : errorColumn) {
                String columnName = "";
                if (i == 0) {
                    columnName = "userid";
                } else if (i == 1) {
                    columnName = "course";
                }
                errorList.add("course_completed.csv, row: " + rowNum + ", \"" + errorType + " [" + columnName + "]\"");
            }
        }
    }

    /**
     * Crafts the error message for each field to return.
     *
     * @param errorCol is a string of the column that has an error
     * @param errorType is a string of the errorType
     * @param rowNum is the row number of the row which the error exists at
     */
    public static void addError(String errorCol, String errorType, int rowNum) {

        errorList.add("course_completed.csv, row: " + rowNum + ", \"" + errorType + " [" + errorCol + "]\"");
    }

    /**
     * Checks if the course exists.
     *
     * @param courseID is a string that contains the course ID to be checked
     * @return boolean returns true if course exists, returns false if otherwise
     */
    public static boolean checkIfCourseExist(String courseID) {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM Course WHERE course_id=?");
            stmt.setString(1, courseID);
            rs = stmt.executeQuery();

            while (rs.next()) {
                return true;
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }

    /**
     * Checks if the user exists.
     *
     * @param userID is a string that contains the user ID to be checked
     * @return boolean returns true if course exists, returns false if otherwise
     */
    public static boolean checkIfUserExist(String userID) {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM student WHERE user_id=?");
            stmt.setString(1, userID);
            rs = stmt.executeQuery();

            while (rs.next()) {
                return true;
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }

    /**
     * Retrieves the Course based on the course_id.
     *
     * @param course_id is a String that contains the course ID to be retrieved
     * @return Course returns Course that has the course ID, returns null if it
     * does not exist
     */
    public static Course retrieveCourse(String course_id) {

        Course toReturn = null;
        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM course WHERE course_id=?");
            stmt.setString(1, course_id);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String courseId = rs.getString(1);
                String school = rs.getString(2);
                String title = rs.getString(3);
                String desc = rs.getString(4);
                java.sql.Date dbExamDate = rs.getDate(5);
                String examStartTime = rs.getString(6);
                String examEndTime = rs.getString(7);

                java.util.Date examDate = new java.util.Date(dbExamDate.getTime());
                toReturn = new Course(courseId, school, title, desc, examDate, examStartTime, examEndTime);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    /**
     * Retrieves the prerequisites for a Course based on the
     * course_id.
     *
     * @param course_id is a String that contains the course ID for its
     * prerequisites to be retrieved
     * @return an ArrayList of String containing prerequisite of that course ID, returns
     * null the course does not exist
     */
    public static ArrayList<String> retrievePrerequisiteIDs(String course_id) {

        ArrayList<String> toReturn = new ArrayList<>();
        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT prerequisite_id FROM prerequisite WHERE course_id=?");
            stmt.setString(1, course_id);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String pId = rs.getString(1);

                toReturn.add(pId);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    /**
     * Checks if the user has completed the prerequisites for a
     * course.
     *
     * @param userID a String containing the user ID of the user to be checked
     * @param courseToCheckID a String containing the course ID of the course
     * prerequisites to be checked
     * @return boolean returns true if user has taken all prerequisite courses,
     * returns false if otherwise
     */
    public static boolean checkUserCompletesPrerequisite(String userID, String courseToCheckID) {

        ArrayList<String> prerequisiteIDs = retrievePrerequisiteIDs(courseToCheckID);
        for (int i = 0; i < prerequisiteIDs.size(); i++) {
            String pId = prerequisiteIDs.get(i);
            Course currentCourse = retrieveCourse(pId);
            if (currentCourse != null) {
                for (String[] entry : okayList) {
                    String currentUserID = entry[0];
                    if (currentUserID.equals(userID)) {
                        String currentCourseID = entry[1];
                        String cName = currentCourse.getCourseID();
                        System.out.println(currentCourseID + " " + cName);
                        if (currentCourseID.equals(currentCourse.getCourseID())) {
                            prerequisiteIDs.remove(i);
                        }
                    }
                }
            }
        }
        if (prerequisiteIDs.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check that the secondary validation on entries that are not blank.
     * It checks that the entries are in the correct format.
     *
     * @param entry is a string array containing entries to be checked
     * @param rowNum is a number to identify the row which the entries are from
     */
    public static void secondaryValidation(String[] entry, int rowNum) {

        String userID = entry[0].trim();
        String courseID = entry[1].trim();

        boolean checkUserExist = checkIfUserExist(userID);
        boolean checkCourseExist = checkIfCourseExist(courseID);
        boolean checkUserCompletesPrereq = checkUserCompletesPrerequisite(userID, courseID);

        if (checkUserExist && checkCourseExist && checkUserCompletesPrereq) {
            okayList.add(entry);
        } else {
            if (!checkUserExist) {
                addError("userid", "invalid userid", rowNum);
                addErrorToMap("invalid userid", rowNum);
            }
            if (!checkCourseExist) {
                addError("course", "invalid course", rowNum);
                addErrorToMap("invalid course", rowNum);
            }
            if (!checkUserCompletesPrereq) {
                addError("student", "invalid course completed", rowNum);
                addErrorToMap("invalid course completed", rowNum);
            }
        }
    }

    /**
     * Get all errors in the file that was checked.
     *
     * @return an arrayList of String containing the errors
     */
    public static ArrayList<String> getErrorList() {

        return errorList;
    }

    /**
     * Get all entries that have passed the validation checks.
     *
     * @return arrayList of String array containing all valid entries
     */
    public static ArrayList<String[]> getOkayList() {

        return okayList;
    }

    /**
     * Resets the error list, valid entries list, and the error map.
     *
     */
    public static void resetAllList() {

        errorList.clear();
        okayList.clear();
        errorMap.clear();
    }

    /**
     * Adds a list of errors for each row and into a collated map of
     * error logs for all rows.
     *
     * @param errorColumn contains a List of column numbers with errors
     * @param errorType is a String that determines the errorType
     * @param rowNum is an Integer that determines the row number of the errors
     */
    public static void addErrorToMap(List<Integer> errorColumn, String errorType, int rowNum) {

        if (errorColumn != null && errorColumn.size() > 0) {
            for (Integer i : errorColumn) {
                String columnName = "";
                if (i == 0) {
                    columnName = "userid";
                } else if (i == 1) {
                    columnName = "course";
                }

                String[] key = {"course_completed.csv", String.valueOf(rowNum)};
                if (errorMap.containsKey(Arrays.asList(key))) {
                    //row exist in map
                    ArrayList<String> retrievedErrors = errorMap.get(Arrays.asList(key));
                    retrievedErrors.add(errorType + " " + columnName);
                    errorMap.put(Arrays.asList(key), retrievedErrors);
                } else {
                    //row does not exist in map
                    ArrayList<String> errors = new ArrayList<String>();
                    errors.add(errorType + " " + columnName);
                    errorMap.put(Arrays.asList(key), errors);
                }
            }
        }
    }

    /**
     * Adds an individual error into the error map.
     *
     * @param errorType is a String that determines the error type
     * @param rowNum is an Integer that determines the row number for the error
     */
    public static void addErrorToMap(String errorType, int rowNum) {

        String[] key = {"course_completed.csv", String.valueOf(rowNum)};
        if (errorMap.containsKey(Arrays.asList(key))) {
            //row exist in map
            ArrayList<String> retrievedErrors = errorMap.get(Arrays.asList(key));
            retrievedErrors.add(errorType);
            errorMap.put(Arrays.asList(key), retrievedErrors);
        } else {
            //row does not exist in map
            ArrayList<String> errors = new ArrayList<String>();
            errors.add(errorType);
            errorMap.put(Arrays.asList(key), errors);
        }
    }

    /**
     * Get the error Map containing errors of rows in the file.
     *
     * @return the errorMap
     */
    public static HashMap<List<String>, ArrayList<String>> getErrorMap() {

        return errorMap;
    }
}
