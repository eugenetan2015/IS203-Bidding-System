/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bios.bootstrap.validation;

import bios.dao.ConnectionManager;
import bios.dao.CourseDAO;
import bios.entity.Course;
import java.util.*;
import java.util.Date;
import com.opencsv.*;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;

/**
 *
 * @author Tan Ming Kwang
 */
public class PrerequisiteValidation {

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

        filePath = filePath + "prerequisite.csv";
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
     * @return an arrayList of integer that contains the column numbers that are blank for the
     * field
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
     * @param errorColumn is an arrayList Integer that contain the column that have errors
     * @param errorType is a string describing the type of error
     * @param rowNum is the row number of the row which the error exists at
     */
    public static void addError(List<Integer> errorColumn, String errorType, int rowNum) {

        if (errorColumn != null && errorColumn.size() > 0) {
            for (Integer i : errorColumn) {
                String columnName = "";
                if (i == 0) {
                    columnName = "course";
                } else if (i == 1) {
                    columnName = "prerequisite";
                }
                errorList.add("prerequisite.csv, row: " + rowNum + ", \"" + errorType + " [" + columnName + "]\"");
            }
        }
    }

    /**
     * Crafts the error message for each field to return.
     *
     * @param errorCol is a string that contain the column which have error
     * @param errorType is a string describing the type of error
     * @param rowNum is the row number of the row which the error exists at
     */
    public static void addError(String errorCol, String errorType, int rowNum) {

        errorList.add("prerequisite.csv, row: " + rowNum + ", \"" + errorType + " [" + errorCol + "]\"");
    }

    /**
     * Checks if the course exists.
     *
     * @param courseID is a string that contains the course ID to be checked
     * @return boolean returns true if course exists, returns false if otherwise
     */
    public static boolean checkIfCourseExist(String courseID) {

        CourseDAO courseDAO = new CourseDAO();
        Course toReturn = courseDAO.retrieveCourse(courseID);
        if (toReturn != null) {
            return true;
        }
        return false;
    }

    /**
     * Check that the secondary validation on entries that are not blank.
     * It checks that the entries are in the correct format.
     *
     * @param entry is a string array containing entries to be checked
     * @param rowNum is a number to identify the row which the entries are from
     */
    public static void secondaryValidation(String[] entry, int rowNum) {

        String course = entry[0].trim();
        String prerequisite = entry[1].trim();

        boolean checkCourseExist = checkIfCourseExist(course);
        boolean checkPrereqExist = checkIfCourseExist(prerequisite);

        if ((checkCourseExist) && (checkPrereqExist)) {
            okayList.add(entry);
        } else {
            if (!checkCourseExist) {
                addError("course", "invalid course", rowNum);
                addErrorToMap("invalid course", rowNum);
            }
            if (!checkPrereqExist) {
                addError("prerequisite", "invalid prerequisite", rowNum);
                addErrorToMap("invalid prerequisite", rowNum);
            }
        }
    }

    /**
     * Get all errors in the file that was checked.
     *
     * @return an ArrayList of String containing the errors
     */
    public static ArrayList<String> getErrorList() {

        return errorList;
    }

    /**
     * This method returns an arrayList of String array of all entries that have
     * passed the validation checks.
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
                    columnName = "course";
                } else if (i == 1) {
                    columnName = "prerequisite";
                }

                String[] key = {"prerequisite.csv", String.valueOf(rowNum)};
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

        String[] key = {"prerequisite.csv", String.valueOf(rowNum)};
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
     * Get the error map containing errors of rows in the file.
     *
     * @return the hashMap that contains all the errors 
     */
    public static HashMap<List<String>, ArrayList<String>> getErrorMap() {

        return errorMap;
    }
}
