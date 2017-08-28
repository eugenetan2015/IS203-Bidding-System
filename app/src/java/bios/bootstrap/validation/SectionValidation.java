/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bios.bootstrap.validation;

import bios.dao.*;
import bios.entity.*;
import java.util.*;
import java.util.Date;
import com.opencsv.*;
import java.io.*;
import java.text.SimpleDateFormat;

/**
 *
 * @author Tan Ming Kwang
 */
public class SectionValidation {

    private static ArrayList<String> errorList = new ArrayList<>();
    private static ArrayList<String[]> okayList = new ArrayList<>();
    private static HashMap<List<String>, ArrayList<String>> errorMap = new HashMap<>();

    /**
     * Checks if the file has any blanks in the rows. If blanks are
     * present, it adds the error into an error log to be released containing
     * its row number and column blank.
     *
     * @param filePath the file name to be entered
     */
    public static void validate(String filePath) {

        filePath = filePath + "section.csv";
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
     * @return an ArrayList of integer that contains the column numbers that are
     * blank for the field
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
                    columnName = "course";
                } else if (i == 1) {
                    columnName = "section";
                } else if (i == 2) {
                    columnName = "day";
                } else if (i == 3) {
                    columnName = "start";
                } else if (i == 4) {
                    columnName = "end";
                } else if (i == 5) {
                    columnName = "instructor";
                } else if (i == 6) {
                    columnName = "venue";
                } else if (i == 7) {
                    columnName = "size";
                }

                errorList.add("section.csv, row: " + rowNum + ", \"" + errorType + " [" + columnName + "]\"");
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

        errorList.add("section.csv, row: " + rowNum + ", \"" + errorType + " [" + errorCol + "]\"");
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
     * Checks if the entry is a valid time format.
     *
     * @param time is a String containing the time value
     * @return boolean returns true if the String entered is a valid time
     * format, returns false if otherwise
     */
    public static boolean checkValidTime(String time) {

        Date toCheck = null;
        if (time != null || !time.equals("")) {
            if (time.length() > 5) {
                return false;
            }
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                sdf.setLenient(false);
                toCheck = sdf.parse(time);
                if (toCheck != null) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
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
     * Checks if the section value in valid format.
     *
     * @param sectionID is the String containing the value of the section ID to
     * be checked
     * @return boolean returns true if the sectionID is in valid format, returns
     * false otherwise
     */
    public static boolean checkIfValidSection(String sectionID) {

        if (sectionID.length() > 3) {
            return false;
        }

        char firstCharacter = Character.toUpperCase(sectionID.charAt(0));
        if (firstCharacter != 'S') {
            return false;
        }

        String sectionNum = sectionID.substring(1);

        try {
            int sectNum = Integer.parseInt(sectionNum);

            if (sectNum < 1 || sectNum > 100) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Checks if the entry is a valid Integer to represent a day.
     *
     * @param intDay is a String containing the Integer to represent a day
     * @return boolean returns true if it a valid Integer to represent a day,
     * returns false if otherwise
     */
    public static boolean checkValidDay(String intDay) {

        try {
            int dayNum = Integer.parseInt(intDay);
            if (dayNum >= 1 && dayNum <= 7) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Converts a String value to a Date object.
     *
     * @param time is a String containing the time value to be converted
     * @return Date returns converted Date object
     */
    public static Date convertStringToTime(String time) {

        Date toReturn = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            sdf.setLenient(false);
            toReturn = sdf.parse(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    /**
     * Checks if the String value is a positive Integer.
     *
     * @param num is a String containing the value to be checked
     * @return boolean returns true if String value is a positive Integer,
     * returns false if otherwise
     */
    public static boolean checkIfPositive(String num) {

        try {
            int intNum = Integer.parseInt(num);
            if (intNum > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * Checks if the timing of one Date is after the timing of
     * another.
     *
     * @param firstTime is a Date object to be compared
     * @param secondTime is a Date object to be compared against the firstTime
     * @return boolean returns true if the firstTime is after the secondTime,
     * returns false if otherwise
     */
    public static boolean compareFirstTimeToSecond(Date firstTime, Date secondTime) {

        try {
            if (firstTime.after(secondTime)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
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

        int i = 0;

        // trim all the whitespaces
        for (String s : entry) {
            entry[i] = s.trim();
            i++;
        }

        String course = entry[0].trim();
        String section = entry[1].trim();
        String day = entry[2].trim();
        String start = entry[3].trim();
        String end = entry[4].trim();
        String instructor = entry[5].trim();
        String venue = entry[6].trim();
        String size = entry[7].trim();

        boolean checkCourseExist = checkIfCourseExist(course);
        boolean checkValidDay = checkValidDay(day);
        boolean checkValidEndTimeAfterStart = false;
        boolean checkValidInstructor = checkStringLength(instructor, 100);
        boolean checkValidVenue = checkStringLength(venue, 100);
        boolean checkValidSize = checkIfPositive(size);

        boolean checkValidSection = true;
        if (checkCourseExist) {
            checkValidSection = checkIfValidSection(section);
        }

        boolean checkValidStartTime = checkValidTime(start);
        boolean checkValidEndTime = checkValidTime(end);

        if (!checkValidStartTime && checkValidEndTime) {
            checkValidEndTimeAfterStart = true;
        } else if (checkValidStartTime && !checkValidEndTime) {
            checkValidEndTimeAfterStart = true;
        } else {
            Date startTime = convertStringToTime(start);
            Date endTime = convertStringToTime(end);
            checkValidEndTimeAfterStart = compareFirstTimeToSecond(endTime, startTime);
        }

        if ((checkCourseExist) && (checkValidSection) && (checkValidDay) && (checkValidStartTime) && (checkValidEndTimeAfterStart) && (checkValidInstructor) && (checkValidVenue) && (checkValidSize)) {
            okayList.add(entry);
        } else {
            if (!checkCourseExist) {
                addError("course", "invalid course", rowNum);
                addErrorToMap("invalid course", rowNum);
            }
            if ((checkCourseExist) && (!checkValidSection)) {
                addError("section", "invalid section", rowNum);
                addErrorToMap("invalid section", rowNum);
            }
            if (!checkValidDay) {
                addError("day", "invalid day", rowNum);
                addErrorToMap("invalid day", rowNum);
            }
            if (!checkValidStartTime) {
                addError("exam start", "invalid start", rowNum);
                addErrorToMap("invalid start", rowNum);
            }
            if (!checkValidEndTimeAfterStart) {
                addError("exam end", "invalid end", rowNum);
                addErrorToMap("invalid end", rowNum);
            }

            if (!checkValidInstructor) {
                addError("instructor", "invalid instructor", rowNum);
                addErrorToMap("invalid instructor", rowNum);
            }
            if (!checkValidVenue) {
                addError("venue", "invalid venue", rowNum);
                addErrorToMap("invalid venue", rowNum);
            }
            if (!checkValidSize) {
                addError("size", "invalid size", rowNum);
                addErrorToMap("invalid size", rowNum);
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
     * Get all entries that have passed the validation checks.
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
                    columnName = "course";
                } else if (i == 1) {
                    columnName = "section";
                } else if (i == 2) {
                    columnName = "day";
                } else if (i == 3) {
                    columnName = "start";
                } else if (i == 4) {
                    columnName = "end";
                } else if (i == 5) {
                    columnName = "instructor";
                } else if (i == 6) {
                    columnName = "venue";
                } else if (i == 7) {
                    columnName = "size";
                }

                String[] key = {"section.csv", String.valueOf(rowNum)};
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

        String[] key = {"section.csv", String.valueOf(rowNum)};
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
     * @return hashMap that contains all the errors
     */
    public static HashMap<List<String>, ArrayList<String>> getErrorMap() {

        return errorMap;
    }
}
