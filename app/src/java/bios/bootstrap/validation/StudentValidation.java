/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bios.bootstrap.validation;

import java.util.*;
import com.opencsv.*;
import java.io.*;

/**
 *
 * @author Tan Ming Kwang
 */
public class StudentValidation {

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

        filePath = filePath + "student.csv";
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
     * @return list of Integer that contains the column numbers that are blank
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
     * @param errorColumn is an arrayList of Integer that contain the column
     * with error
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
                    columnName = "password";
                } else if (i == 2) {
                    columnName = "name";
                } else if (i == 3) {
                    columnName = "school";
                } else if (i == 4) {
                    columnName = "edollar";
                }

                errorList.add("student.csv, row: " + rowNum + ", \"" + errorType + " [" + columnName + "]\"");
            }
        }
    }

    /**
     * Crafts the error message for each field to return.
     *
     * @param errorCol is a String that contains the errorCol 
     * @param errorType is a string describing the type of error
     * @param rowNum is the row number of the row which the error exists at
     */
    public static void addError(String errorCol, String errorType, int rowNum) {

        errorList.add("student.csv, row: " + rowNum + ", \"" + errorType + " [" + errorCol + "]\"");
    }

    /**
     * Checks if the entered string's length exceeds the maximum
     * length.
     *
     * @param s is a string to be checked for its length
     * @param maxLength is the maximum numbers of characters the string is
     * supposed to have
     * @return boolean returns true if the string has lesser characters than the
     * maximum allowed, returns false if otherwise
     */
    public static boolean checkStringLength(String s, int maxLength) {

        if (s.length() <= maxLength) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the user exists (is already accounted for).
     *
     * @param userID is a string that contains the user ID to be checked
     * @return boolean returns true if user has already been accounted for,
     * returns false if otherwise
     */
    public static boolean checkUserIDExist(String userID) {

        for (String[] entry : okayList) {
            if (userID.equals(entry[0])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the e dollar amount entered is a positive double
     * amount.
     *
     * @param stringEDollar Takes in stringEDollar of String type
     * @return boolean returns true if stringEdollar is in the correct format,
     * returns false if otherwise
     */
    public static boolean checkEDollar(String stringEDollar) {

        try {
            double eDollar = Double.parseDouble(stringEDollar);
            if (eDollar >= 0.0) {
                return true;
            }
        } catch (Exception e) {
            return false;
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

        String userId = entry[0].trim();
        String password = entry[1].trim();
        String name = entry[2].trim();
        String school = entry[3].trim();
        String eDollars = entry[4].trim();

        boolean checkUserIDLength = checkStringLength(userId, 128);
        boolean checkUserIDExist = checkUserIDExist(userId);
        boolean checkUserEDollar = checkEDollar(eDollars);
        boolean checkUserPWLength = checkStringLength(password, 128);
        boolean checkNameLength = checkStringLength(name, 100);

        if ((checkUserIDLength) && (!checkUserIDExist) && (checkUserEDollar) && (checkUserPWLength) && (checkNameLength)) {
            //add into okayList
            okayList.add(entry);
        } else {
            if (!checkUserIDLength) {
                addError("user_id", "invalid userid", rowNum);
                addErrorToMap("invalid userid", rowNum);
            }
            if (checkUserIDExist) {
                addError("user_id", "duplicate userid", rowNum);
                addErrorToMap("duplicate userid", rowNum);
            }
            if (!checkUserEDollar) {
                addError("edollar", "invalid e-dollar", rowNum);
                addErrorToMap("invalid e-dollar", rowNum);
            }
            if (!checkUserPWLength) {
                addError("password", "invalid password", rowNum);
                addErrorToMap("invalid password", rowNum);
            }
            if (!checkNameLength) {
                addError("name", "invalid name", rowNum);
                addErrorToMap("invalid name", rowNum);
            }
        }

    }

    /**
     * Get all errors in the file that was
     * checked.
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
                    columnName = "password";
                } else if (i == 2) {
                    columnName = "name";
                } else if (i == 3) {
                    columnName = "school";
                } else if (i == 4) {
                    columnName = "edollar";
                }
                String[] key = {"student.csv", String.valueOf(rowNum)};
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

        String[] key = {"student.csv", String.valueOf(rowNum)};
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
     * Get the errors of rows in the file.
     *
     * @return hashMap that contains all the errors during validation
     */
    public static HashMap<List<String>, ArrayList<String>> getErrorMap() {

        return errorMap;
    }
}
