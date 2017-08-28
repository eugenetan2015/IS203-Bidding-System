/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bios.bootstrap.validation;

import bios.dao.BidDAO;
import bios.dao.CourseDAO;
import bios.dao.SectionDAO;
import bios.dao.StudentDAO;
import bios.entity.Course;
import bios.entity.Section;
import bios.entity.Student;
import java.util.*;
import com.opencsv.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 *
 * @author Lau Yuit Theng, Cristabel
 */
public class BidValidation {

    private static double stud_eDollars = 0;
    private static boolean trap = false;

    private static ArrayList<String> errorList = new ArrayList<String>();
    private static ArrayList<String[]> okayList = new ArrayList<String[]>();
    private static HashMap<List<String>, ArrayList<String>> errorMap = new HashMap<List<String>, ArrayList<String>>();

    private static int numSuccessfulBids = 0;

    /**
     * Checks if the file has any blanks in the rows. If blanks are
     * present, it adds the error into an error log to be released containing
     * its row number and column blank.
     *
     * @param filePath the file name to be entered
     */
    public static void validate(String filePath) {

        numSuccessfulBids = 0;
        filePath = filePath + "bid.csv";
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
     * @return an arrayList of Integer that contains column numbers that are
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
     * @param errorColumn is an arrayList of Integer of the column numbers that has an
     * error
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
                    columnName = "amount";
                } else if (i == 2) {
                    columnName = "code";
                } else if (i == 3) {
                    columnName = "section";
                }

                errorList.add("bid.csv, row: " + rowNum + ", \"" + errorType + " [" + columnName + "]\"");
            }
        }
    }

    /**
     * Crafts the error message for each field to return.
     *
     * @param errorCol is a string of the column that has an error
     * @param errorType is a string describing the type of error
     * @param rowNum is the row number of the row which the error exists at
     */
    public static void addError(String errorCol, String errorType, int rowNum) {

        errorList.add("bid.csv, row: " + rowNum + ", \"" + errorType + " [" + errorCol + "]\"");
    }

    //add methods to perform the validations
    /**
     * Checks if the user exists.
     *
     * @param userid is a string that contains the user ID to be checked
     * @return boolean returns true if course exists, returns false if otherwise
     */
    public static boolean checkStudent(String userid) {

        boolean exists = true;
        StudentDAO studentDAO = new StudentDAO();
        if (studentDAO.retrieveStudent(userid) == null) {
            exists = false;
        }
        return exists;
    }

    /**
     * Checks if the bid amount is entered in the correct format.
     *
     * @param bidAmount is a String which contains the bid amount to be checked.
     * @return boolean returns true if the bid amount is in a valid format,
     * returns false if otherwise
     */
    public static boolean checkBidAmount(String bidAmount) {

        String value = String.valueOf(bidAmount);
        int index = value.indexOf(".");

        int length = 0;

        if (index != -1) {
            length = value.substring(index).length() - 1;
        }
        return Double.parseDouble(bidAmount) >= 10 && length <= 2;
    }

    /**
     * Checks if the course exists.
     *
     * @param courseid is a string that contains the course ID to be checked
     * @return boolean returns true if course exists, returns false if otherwise
     */
    public static boolean checkCourse(String courseid) {

        boolean exists = true;
        CourseDAO courseDAO = new CourseDAO();
        if (courseDAO.retrieveCourse(courseid) == null) {
            exists = false;
        }
        return exists;
    }

    /**
     * Checks if the section exists.
     *
     * @param courseid is a String containing the course ID of the course to be
     * checked
     * @param sectionid is a String containing the section ID of the section to
     * be checked
     * @return boolean returns true if the section exists, returns false if
     * otherwise
     */
    public static boolean checkSection(String courseid, String sectionid) {

        boolean exists = true;
        SectionDAO sectionDAO = new SectionDAO();
        if (sectionDAO.retrieve(courseid, sectionid) == null) {
            exists = false;
        }
        return exists;
    }

    // return true if it is own school
    // else return false
    /**
     * Checks if the course belongs to the school of the student.
     *
     * @param userid is a String containing the user's ID
     * @param courseid is a String containing the course ID of the course to be
     * checked
     * @return boolean returns true if it belongs to the user's school, returns
     * false if otherwise
     */
    public static boolean checkOwnSchool(String userid, String courseid) {

        StudentDAO studentDAO = new StudentDAO();
        Student student = studentDAO.retrieveStudent(userid);
        String school = student.getSchool();
        CourseDAO courseDAO = new CourseDAO();
        String school2 = courseDAO.retrieveCourse(courseid).getSchool();
        if (school.equals(school2)) {
            return true;
        }
        return false;
    }

    /**
     * Checks if a section's schedule clashes with the student's
     * existing schedule.
     *
     * @param userid is a String containing the user's ID
     * @param courseid is a String containing the course ID of the course
     * @param sectionid is a String containing the section ID of the section to
     * be checked
     * @return boolean returns true if it does not clash, returns false if it
     * does not clash
     */
    public static boolean checkTimetableClash(String userid, String courseid, String sectionid) {

        //get the day of the class
        SectionDAO sectionDAO = new SectionDAO();
        Section s = sectionDAO.retrieve(courseid, sectionid);
        String day = s.getDay();
        //get the timing
        String timeStart = s.getStartDate();
        String timeEnd = s.getEndDate();
        //check the day with other bids

        boolean pass = true;
        try {
            for (String[] bid : okayList) {
                String bidUsername = bid[0];
                String bidCourseID = bid[2];
                String bidSectionID = bid[3];
                if (bidUsername.equals(userid)) {
                    Section toCheckSection = sectionDAO.retrieve(bidCourseID, bidSectionID);
                    if (toCheckSection.getDay().equals(day)) {
                        String timeStart2 = sectionDAO.retrieve(bid[2], bid[3]).getStartDate();
                        String timeEnd2 = sectionDAO.retrieve(bid[2], bid[3]).getEndDate();
                        //check if it clashes
                        if (timeStart2.equals(timeStart) && timeEnd2.equals(timeEnd)) {
                            pass = false;
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        Date start = sdf.parse(timeStart);
                        Date end = sdf.parse(timeEnd);
                        Date start2 = sdf.parse(timeStart2);
                        Date end2 = sdf.parse(timeEnd2);

                        if (start2.before(end) && end2.after(end)) {
                            pass = false;
                        } else if (end2.after(start) && start2.before(start)) {
                            pass = false;
                        } else if (start2.after(start) && end2.before(end)) {
                            pass = false;
                        }
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //if the same, check the timing
        return pass;
    }

    /**
     * Checks if a courses's exam schedule clashes with the
     * student's existing exam schedule.
     *
     * @param userid is a String containing the user's ID
     * @param courseid is a String containing the course ID of the course exam
     * to check
     * @return boolean returns true if it does not clash, returns false if it
     * does not clash
     */
    public static boolean checkExamClash(String userid, String courseid) {

        //get the date of the exam
        CourseDAO courseDAO = new CourseDAO();
        Course c = courseDAO.retrieveCourse(courseid);
        Date date = c.getExamDate();
        //get the timing
        String timeStart = c.getExamStart();
        String timeEnd = c.getExamEnd();
        //check the day with other bids
        boolean pass = true;
        try {
            for (String[] bid : okayList) {
                if (bid[0].equals(userid)) {
                    if (courseDAO.retrieveCourse(bid[2]).getExamDate().compareTo(date) == 0) {
                        String timeStart2 = courseDAO.retrieveCourse(bid[2]).getExamStart();
                        String timeEnd2 = courseDAO.retrieveCourse(bid[2]).getExamEnd();
                        //check if it clashes
                        if (timeStart2.equals(timeStart) && timeEnd2.equals(timeEnd)) {
                            pass = false;
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        Date start = sdf.parse(timeStart);
                        Date end = sdf.parse(timeEnd);
                        Date start2 = sdf.parse(timeStart2);
                        Date end2 = sdf.parse(timeEnd2);

                        if (start.before(start2)) {
                            if (end.after(start2)) {
                                pass = false;
                            }
                        }
                        if (start.after(start2)) {
                            if (end.before(end2)) {
                                pass = false;
                            }
                            if (start.before(end2)) {
                                pass = false;
                            }
                        }
                        if (end.after(start2)) {
                            if (end.before(end2)) {
                                pass = false;
                            }
                        }
                        if (start2.before(start)) {
                            if (end2.after(start)) {
                                pass = false;
                            }
                        }
                        if (start2.after(start)) {
                            if (end2.before(end)) {
                                pass = false;
                            }
                            if (start2.before(end)) {
                                pass = false;
                            }
                        }
                        if (end2.after(start)) {
                            if (end2.before(end)) {
                                pass = false;
                            }
                        }
                        if (start.equals(start2) && end.equals(end2)) {
                            pass = false;
                        }
                        if (start.equals(end2)) {
                            pass = false;
                        }

                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //if the same, check the timing
        return pass;
    }

    /**
     * Checks if the user has completed the prerequisites for a
     * course.
     *
     * @param userid a String containing the user ID of the user to be checked
     * @param courseid a String containing the course ID of the course
     * prerequisites to be checked
     * @return boolean returns true if user has taken all prerequisite courses,
     * returns false if otherwise
     */
    public static boolean checkCompletedRequisites(String userid, String courseid) {

        StudentDAO studentDAO = new StudentDAO();
        return studentDAO.hasCompletedPrerequisite(userid, courseid);
    }

    /**
     * Checks if the student has completed the course.
     *
     * @param userid a String containing the user ID of the user to be checked
     * @param courseid a String containing the course ID of the course to be
     * checked
     * @return boolean returns true if the course has been completed, returns
     * false if otherwise
     */
    public static boolean checkCompletedCourse(String userid, String courseid) {

        StudentDAO studentDAO = new StudentDAO();
        ArrayList<String> completedCourseID = studentDAO.retrieveStudentCompletedCourses(userid);
        for (String completedId : completedCourseID) {
            if (courseid.equals(completedId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the student has already placed the maximum amount
     * of bids.
     *
     * @param userid a String containing the user ID of the user to be checked
     * @return boolean returns true if user has yet to bid the maximum amount,
     * returns false if otherwise
     */
    public static boolean checkSectionLimit(String userid) {

        //checkEDollars is called first, therefore will remove the previous bid of the course already bidded for
        //frees up one spot for user to bid again
        //loop through okay arraylist 
        int count = 0;
        for (String[] bids : okayList) {
            if (bids[0].equals(userid)) {
                count += 1;
            }
        }
        //handles the case where student has not bidded for this course but has already bidded for 5 other courses
        if (count >= 5) {
            return false;
        }
        return true;
    }

    /**
     * Checks if the user has enough eDollars to bid for the
     * section.
     *
     * @param userID Takes in userID of String type
     * @param bidAmount a String that contains the value of the bid amount
     * @param sectionID a String containing the section ID of the section to be
     * checked
     * @param courseID Takes in courseID of String type
     * @param stud_eDollars a String that contains the user's eDollars
     * @return boolean returns true if the user has enough eDollars to bid,
     * returns false if otherwise
     */
    public static boolean checkEDollars(String userID, String bidAmount, String sectionID, String courseID, double stud_eDollars) {

        boolean checkIsStudentUpdating = checkIsStudentUpdating(userID, sectionID, courseID);
        try {
            double amt = Double.parseDouble(bidAmount);
            checkIfStudentUpdatingAmount(userID, sectionID, courseID, amt, stud_eDollars);
            StudentDAO studentDAO = new StudentDAO();
            Student student = studentDAO.retrieveStudent(userID);
            double existingAmt = student.geteDollars();

            boolean validAmt = false;
            if (existingAmt >= amt) {
                validAmt = true;
            }

            if (!checkIsStudentUpdating && validAmt) {
                return true;
            } else if (checkIsStudentUpdating && validAmt) {
                return true;
            } else if (checkIsStudentUpdating && !validAmt) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
        return false;
    }

    // return true if student is updating
    // else return false
    /**
     * Checks if the bid is overriding the user's previous bid.
     *
     * @param userID a String containing the user ID of the user to be checked
     * @param sectionID Takes in sectionID of String type
     * @param courseID a String containing the course ID of the course to be
     * checked
     * @return boolean returns true if the bid is going to override the previous
     * bid, returns false if otherwise
     */
    public static boolean checkIsStudentUpdating(String userID, String sectionID, String courseID) {

        for (int i = 0; i < okayList.size(); i++) {
            String[] bid = okayList.get(i);
            if (bid[0].equals(userID)) {
                String currentSectionID = bid[3];
                String currentCourseID = bid[2];
                if (currentCourseID.equals(courseID)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the bid is overriding the user's previous bid.
     *
     * @param userID a String containing the user ID of the user to be checked
     * @param courseID a String containing the course ID of the course to be
     * checked
     * @param sectionID Takes in sectionID of String type
     * @param bidAmt is a double object containing the bid amount
     * @param stud_eDollars a String that contains the user's eDollars
     * @return boolean returns true if the bid is going to override the previous
     * bid, returns false if otherwise
     */
    public static boolean checkIfStudentUpdatingAmount(String userID, String sectionID, String courseID, double bidAmt, double stud_eDollars) {

        if (bidAmt > stud_eDollars) {
            return false;
        }

        StudentDAO studentDAO = new StudentDAO();
        for (int i = 0; i < okayList.size(); i++) {
            String[] bid = okayList.get(i);
            if (bid[0].equals(userID)) {
                String currentSectionID = bid[3];
                String currentCourseID = bid[2];
                if (currentCourseID.equals(courseID)) {
                    String currentAmount = bid[1];
                    boolean t = studentDAO.refundStudent2(userID, stud_eDollars);
                    okayList.remove(i);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the bid is overriding the user's previous bid.
     *
     * @param userID a String containing the user ID of the user to be checked
     * @param sectionID a String containing the section ID of the section to be
     * checked
     * @param courseID a String containing the course ID of the course to be
     * checked
     * @param bidAmount is a double object containing the bid amount
     * @return boolean returns true if the bid is going to override the previous
     * bid, returns false if otherwise
     */
    public static boolean checkIfStudentUpdatingAmount2(String userID, String sectionID, String courseID, String bidAmount) {

        StudentDAO studentDAO = new StudentDAO();
        for (int i = 0; i < okayList.size(); i++) {
            String[] bid = okayList.get(i);
            if (bid[0].equals(userID)) {
                String currentSectionID = bid[3];
                String currentCourseID = bid[2];
                if (currentSectionID.equals(sectionID) && currentCourseID.equals(courseID)) {
                    String currentAmount = bid[1];
                    try {
                        double currentBidAmount = Double.parseDouble(currentAmount);
                        double newBidAmount = Double.parseDouble(bidAmount);
                        if (currentBidAmount > newBidAmount) {
                            double difference = currentBidAmount - newBidAmount;
                            studentDAO.refundStudent(userID, difference);
                            okayList.remove(i);
                            return true;
                        } else if (newBidAmount > currentBidAmount) {
                            Student student = studentDAO.retrieveStudent(userID);
                            double currentBalance = student.geteDollars();
                            double originalBalance = currentBalance + currentBidAmount;
                            if (newBidAmount <= originalBalance) {
                                double difference = newBidAmount - currentBidAmount;
                                studentDAO.deduct(userID, difference);
                                okayList.remove(i);
                                return true;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }
    // return true if current round is 1 
    // else false

    /**
     * Checks if the round is currently round 1.
     *
     * @return boolean returns true if the round is currently round 1, returns
     * false if otherwise
     */
    public static boolean checkIfRound1() {

        BidDAO biddao = new BidDAO();
        String[] currentRound = biddao.getCurrentBidRound();
        if (currentRound[1].equals("1")) {
            return true;
        }
        return false;
    }

    /**
     * Check the secondary validation on entries that are not blank.
     * It checks that the entries are in the correct format.
     *
     * @param entry is a string array containing entries to be checked
     * @param rowNum is a number to identify the row which the entries are from
     */
    public static void secondaryValidation(String[] entry, int rowNum) {

        StudentDAO studentDAO = new StudentDAO();
        String userid = entry[0].trim();
        String bidAmount = entry[1].trim();
        String courseid = entry[2].trim();
        String sectionid = entry[3].trim();

        boolean checkUserID = checkStudent(userid);
        boolean checkBidAmount = checkBidAmount(bidAmount);
        boolean checkCourseID = checkCourse(courseid);
        boolean checkSectionID = false;
        if (checkCourseID) {
            checkSectionID = checkSection(courseid, sectionid);
        }

        boolean pass = false;

        if (checkUserID && checkBidAmount && checkCourseID && checkSectionID) {
            pass = true;
        } else {
            if (!checkUserID) {
                addError("user", "invalid userid", rowNum);
                addErrorToMap("invalid userid", rowNum);
            }
            if (!checkBidAmount) {
                addError("bid amount", "invalid amount", rowNum);
                addErrorToMap("invalid amount", rowNum);
            }
            if (!checkCourseID) {
                addError("course", "invalid course", rowNum);
                addErrorToMap("invalid course", rowNum);
            }
            if (!checkSectionID && checkCourseID) {
                addError("section", "invalid section", rowNum);
                addErrorToMap("invalid section", rowNum);
            }
        }

        //also checks if student has bidded for the previous section
        if (pass) {
            ArrayList<String[]> okayStudentList = StudentValidation.getOkayList();

            double stud_eDollars = 0;

            for (String[] eachEntry : okayStudentList) {
                String userID = eachEntry[0];

                if (userID.equals(userID)) {
                    stud_eDollars = Double.parseDouble(eachEntry[4]);
                }
            }
            boolean checkCompletedRequisites = checkCompletedRequisites(userid, courseid);
            boolean checkCompletedCourse = checkCompletedCourse(userid, courseid);
            boolean checkSectionLimit = checkSectionLimit(userid);
            boolean checkRound1 = checkIfRound1();
            boolean checkOwnSchool = checkOwnSchool(userid, courseid);
            boolean checkTimetableClash = checkTimetableClash(userid, courseid, sectionid);
            boolean checkExamClash = checkExamClash(userid, courseid);
            boolean checkIsStudentUpdating = checkIsStudentUpdating(userid, sectionid, courseid);
            boolean checkEDollars = checkEDollars(userid, bidAmount, sectionid, courseid, stud_eDollars);

            // start validating errors
            // if current round is 1 and is not checkOwnSchool
            if (checkRound1 && !checkOwnSchool) {
                addError("course", "not own school course", rowNum);
                addErrorToMap("not own school course", rowNum);
            }

            if (!checkIsStudentUpdating && !checkTimetableClash) {
                addError("section", "class timetable clash", rowNum);
                addErrorToMap("class timetable clash", rowNum);
            }
            if (!checkIsStudentUpdating && !checkExamClash) {
                addError("course", "exam timetable clash", rowNum);
                addErrorToMap("exam timetable clash", rowNum);
            }

            if (!checkCompletedRequisites) {
                addError("user", "incomplete prerequisites", rowNum);
                addErrorToMap("incomplete prerequisites", rowNum);
            }
            if (checkCompletedCourse) {
                addError("user", "course completed", rowNum);
                addErrorToMap("course completed", rowNum);
            }
            if (!checkSectionLimit) {
                addError("user", "section limit reached", rowNum);
                addErrorToMap("section limit reached", rowNum);
            }
            if (!checkEDollars) {
                addError("user", "not enough e-dollar", rowNum);
                addErrorToMap("not enough e-dollar", rowNum);
            }

            boolean checkUpdatingIgnoreClassClash = checkIsStudentUpdating || checkTimetableClash;
            boolean checkUpdatingIgnoreExamClash = checkIsStudentUpdating || checkExamClash;
            // valid eDollar && checkOwnSchool

            // round 1 new bid, 
            boolean newBids = checkEDollars && checkOwnSchool && !checkCompletedCourse
                    && checkCompletedRequisites
                    && checkSectionLimit
                    && !checkIsStudentUpdating
                    && checkTimetableClash
                    && checkExamClash;

            boolean studentUpdatingBid
                    = checkEDollars
                    && checkOwnSchool
                    && checkCompletedRequisites
                    && checkSectionLimit
                    && checkUpdatingIgnoreClassClash
                    && checkUpdatingIgnoreExamClash
                    && checkIsStudentUpdating;

            if (newBids) {
                okayList.add(entry);
                double amount = Double.parseDouble(bidAmount);
                studentDAO.deduct(userid, amount);
                numSuccessfulBids++;
            } else if (studentUpdatingBid) {
                double amount = Double.parseDouble(bidAmount);
                studentDAO.deduct(userid, amount);
                okayList.add(entry);
                numSuccessfulBids++;

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
     * Check all entries that have passed the validation checks.
     *
     * @return arrayList of String Array containing all valid entries
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
                    columnName = "amount";
                } else if (i == 2) {
                    columnName = "code";
                } else if (i == 3) {
                    columnName = "section";
                }

                String[] key = {"bid.csv", String.valueOf(rowNum)};
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

        String[] key = {"bid.csv", String.valueOf(rowNum)};
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
     * @return a HashMap of errors
     */
    public static HashMap<List<String>, ArrayList<String>> getErrorMap() {

        return errorMap;
    }

    /**
     * Get the number of successful bids.
     *
     * @return int returns the number of successful bids
     */
    public static int getNumSuccessfulBids() {

        return numSuccessfulBids;
    }
}
