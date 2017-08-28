/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bios.bootstrap.validation;

import java.util.*;
import com.opencsv.*;
import java.io.*;
import java.text.SimpleDateFormat;

/**
 *
 * @author Tan Ming Kwang
 */
public class CourseValidation {

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

        filePath = filePath + "course.csv";
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
     * @return arrayList of Integer returns the column numbers that are blank for the
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
     * @param errorColumn is a list of Integer that contains the errorColumn
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
                    columnName = "school";
                } else if (i == 2) {
                    columnName = "title";
                } else if (i == 3) {
                    columnName = "description";
                } else if (i == 4) {
                    columnName = "exam date";
                } else if (i == 5) {
                    columnName = "exam start";
                } else if (i == 6) {
                    columnName = "exam end";
                }

                errorList.add("course.csv, row: " + rowNum + ", \"" + errorType + " [" + columnName + "]\"");
            }
        }
    }

    /**
     * Crafts the error message for each field to return.
     *
     * @param errorCol is a string that contains the errorColumn
     * @param errorType is a string describing the type of error
     * @param rowNum is the row number of the row which the error exists at
     */
    public static void addError(String errorCol, String errorType, int rowNum) {

        errorList.add("course.csv, row: " + rowNum + ", \"" + errorType + " [" + errorCol + "]\"");
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
     * Checks if the entry is a valid date format.
     *
     * @param date is a String containing the date value
     * @return boolean returns true if the String entered is a valid date
     * format, returns false if otherwise
     */
    public static boolean checkValidDate(String date) {

        Date toCheck = null;
        if (date != null || !date.equals("")) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                sdf.setLenient(false);
                toCheck = sdf.parse(date);
                if (toCheck != null) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Checks if the entry is a valid time format.
     *
     * @param time is a String containing the time value
     * @return boolean returns true if the String entered is a valid time
     * format, returns false if otherwise
     */
    public static boolean checkValidTime(String time) {

        Date toCheck = null;
        if (time != null || !time.equals("")) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                sdf.setLenient(false);
                toCheck = sdf.parse(time);
                if (toCheck != null) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Checks if the end timing of one session is before the start
     * timing of another.
     *
     * @param startTime is a String containing the value of the start time
     * @param endTime is a String containing the value of the end time
     * @return boolean returns true if the end timing occurs before the start
     * timing, returns false if otherwise
     */
    public static boolean checkIfEndIsBeforeStart(String startTime, String endTime) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date start = sdf.parse(startTime);
            Date end = sdf.parse(endTime);
            if (end.before(start)) {
                return true;
            }
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    /**
     * Checks if both the start time and end time are the same
     * timings.
     *
     * @param startTime is a String containing the value of the start time
     * @param endTime is a String containing the value of the end time
     * @return boolean returns true if the start and end timings are the same,
     * returns false if otherwise
     */
    public static boolean checkIfEndIsSameAsStart(String startTime, String endTime) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date start = sdf.parse(startTime);
            Date end = sdf.parse(endTime);
            if (end.compareTo(start) == 0) {
                return true;
            }
        } catch (Exception e) {
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
        String school = entry[1].trim();
        String title = entry[2].trim();
        String description = entry[3].trim();
        String examDate = entry[4].trim();
        String examStart = entry[5].trim();
        String examEnd = entry[6].trim();

        boolean checkTitleLength = checkStringLength(title, 100);
        boolean checkDescLength = checkStringLength(description, 1000);
        boolean checkValidDate = checkValidDate(examDate);
        boolean checkValidStartTime = checkValidTime(examStart);
        boolean checkValidEndTime = checkValidTime(examEnd);
        boolean checkIfEndIsBeforeStart = false;
        boolean checkIfEndIsSameAsStart = false;
        if (checkValidStartTime && checkValidEndTime) {
            checkIfEndIsBeforeStart = checkIfEndIsBeforeStart(examStart, examEnd);
            checkIfEndIsSameAsStart = checkIfEndIsSameAsStart(examStart, examEnd);
        }

        if ((checkTitleLength) && (checkDescLength) && (checkValidDate) && (checkValidStartTime) && (checkValidEndTime) && (!checkIfEndIsBeforeStart) && (!checkIfEndIsSameAsStart)) {
            okayList.add(entry);
        } else {
            if (!checkTitleLength) {
                addError("title", "invalid title", rowNum);
                addErrorToMap("invalid title", rowNum);
            }
            if (!checkDescLength) {
                addError("description", "invalid description", rowNum);
                addErrorToMap("invalid description", rowNum);
            }
            if (!checkValidDate) {
                addError("exam date", "invalid exam date", rowNum);
                addErrorToMap("invalid exam date", rowNum);
            }
            if (!checkValidStartTime) {
                addError("exam start", "invalid exam start", rowNum);
                addErrorToMap("invalid exam start", rowNum);
            }
            if (!checkValidEndTime) {
                addError("exam end", "invalid exam end", rowNum);
                addErrorToMap("invalid exam end", rowNum);
            }
            if (checkValidStartTime && checkValidEndTime && checkIfEndIsBeforeStart) {
                addError("exam end", "invalid exam end", rowNum);
                addErrorToMap("invalid exam end", rowNum);
            }
            if (checkValidStartTime && checkValidEndTime && checkIfEndIsSameAsStart) {
                addError("exam end", "invalid exam end", rowNum);
                addErrorToMap("invalid exam end", rowNum);
            }
        }

    }

    /**
     * Get all errors in the file that was checked.
     *
     * @return arrayList of String containing the errors
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
                    columnName = "course";
                } else if (i == 1) {
                    columnName = "school";
                } else if (i == 2) {
                    columnName = "title";
                } else if (i == 3) {
                    columnName = "description";
                } else if (i == 4) {
                    columnName = "exam date";
                } else if (i == 5) {
                    columnName = "exam start";
                } else if (i == 6) {
                    columnName = "exam end";
                }

                String[] key = {"course.csv", String.valueOf(rowNum)};
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

        String[] key = {"course.csv", String.valueOf(rowNum)};
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
     * @return the error map which contains all the errors
     */
    public static HashMap<List<String>, ArrayList<String>> getErrorMap() {

        return errorMap;
    }
}
