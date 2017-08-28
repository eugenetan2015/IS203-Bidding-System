/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bios.bootstrap.util;

import bios.dao.BidDAO;
import bios.dao.CourseDAO;
import bios.dao.SectionDAO;
import bios.dao.StudentDAO;
import bios.entity.Bid;
import bios.entity.Course;
import bios.entity.Section;
import bios.entity.Student;
import com.google.gson.JsonElement;
import is203.JWTException;
import is203.JWTUtility;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 *
 * @author Wilson
 */
public class ValidationUtility {

    // use by all JSON

    /**
     * Check for URL parameters that is missing/blank/invalid
     * @param jsonReq the jsonReq r parameter
     * @param token the token parameter
     * @return a TreeMap that contained all the errors during common validation
     */
    public TreeMap<String, String> checkForParameterNull(String jsonReq, String token) {
        TreeMap<String, String> map = new TreeMap<>();

        if (jsonReq == null) {
            map.put("jsonReq", "missing r");
        } else if (jsonReq.equals("")) {
            map.put("jsonReq", "blank r");
        }

        if (token == null) {
            map.put("token", "missing token");
        } else if (token.equals("")) {
            map.put("token", "blank token");
        } else {
            try {
                JWTUtility.verify(token, AdminUtility.getSecretKey());

            } catch (JWTException e) {
                map.put("token", "invalid token");
            }
        }

        return map;
    }

    /**
     * Check for URL parameters that is missing/blank/invalid
     * @param token the token parameter
     * @return a TreeMap that contained all the errors during common validation
     */
    public TreeMap<String, String> checkForParameterNull(String token) {
        TreeMap<String, String> map = new TreeMap<>();

        if (token == null) {
            map.put("token", "missing token");
        } else if (token.equals("")) {
            map.put("token", "blank token");
        } else {
            try {
                JWTUtility.verify(token, AdminUtility.getSecretKey());

            } catch (JWTException e) {
                map.put("token", "invalid token");
            }
        }

        return map;
    }

    /**
     * Check for missing/blank fields for username and password
     * @param username the username
     * @param password the password
     * @return a TreeMap that contained all the errors during common validation
     */
    public TreeMap<String, String> checkLoginParameterBlank(String username, String password) {
        TreeMap<String, String> map = new TreeMap<>();

        if (username == null) {
            map.put("username", "missing username");
        } else if (username.equals("")) {
            map.put("username", "blank username");
        }

        if (password == null) {
            map.put("password", "missing password");
        } else if (password.equals("")) {
            map.put("password", "blank password");
        }

        return map;
    }

    /**
     * Validate JSON Individual Elements Parameters to check for missing/blank fields
     * @param userIDElement the userIDElements attribute in r parameter
     * @param courseElement the courseElement attribute in r parameter
     * @param sectionElement the sectionIDElement attribute in r parameter
     * @return a TreeMap that contained all the errors during common validation
     */
    public TreeMap<String, String> validJSONParameters(JsonElement userIDElement, JsonElement courseElement, JsonElement sectionElement) {
        TreeMap<String, String> map = new TreeMap<>();

        if (userIDElement == null) {
            map.put("userid", "missing userid");
        } else {
            String userID = userIDElement.getAsString();
            if (userID.equals("")) {
                map.put("userid", "blank userid");
            }
        }

        if (courseElement == null) {
            map.put("course", "missing course");
        } else {
            String courseID = courseElement.getAsString();
            if (courseID.equals("")) {
                map.put("course", "blank course");
            }
        }

        if (sectionElement == null) {
            map.put("section", "missing section");
        } else {
            String sectionIDStr = sectionElement.getAsString();
            if (sectionIDStr.equals("")) {
                map.put("section", "blank section");
            }
        }

        return map;
    }

    /**
     * Check for invalid student, course, section and round ended
     * @param student the student object
     * @param course the course object
     * @param section the section object
     * @param currentRound the currentRound array
     * @return a TreeMap that contained all the errors during common validation
     */
    public TreeMap<String, String> checkInvalid(Student student, Course course, Section section, String[] currentRound) {
        TreeMap<String, String> map = new TreeMap<>();

        if (student == null) {
            map.put("student", "invalid student");
        }

        if (course == null) {
            map.put("course", "invalid course");
        }

        if (course != null && section == null) {
            map.put("section", "invalid section");
        }

        if (currentRound[0].equals("ended")) {
            map.put("troundended", "round ended");
        }

        return map;
    }

    /**
     * Check for missing/blank for username
     * @param userIDElement the userIDElement attribute in r parameter
     * @return a TreeMap that contained all the errors during common validation
     */
    public TreeMap<String, String> validJSONParameters(JsonElement userIDElement) {
        TreeMap<String, String> map = new TreeMap<>();

        if (userIDElement == null) {
            map.put("userid", "missing userid");
        } else {
            String userID = userIDElement.getAsString();
            if (userID.equals("")) {
                map.put("userid", "blank userid");
            }
        }

        return map;
    }

    /**
     * Check for invalid Student
     * @param student the student object
     * @return a TreeMap that contained all the errors during common validation
     */
    public TreeMap<String, String> checkInvalid(Student student) {
        TreeMap<String, String> map = new TreeMap<>();

        if (student == null) {
            map.put("student", "invalid userid");
        }

        return map;
    }

    /**
     * Check for missing/blank field for Course and Section
     * @param courseElement the courseElement attribute in r parameter
     * @param sectionIDElement the sectionIDElement attribute in r parameter
     * @return a TreeMap that contained all the errors during common validation
     */
    public TreeMap<String, String> validJSONParameters(JsonElement courseElement, JsonElement sectionIDElement) {
        TreeMap<String, String> map = new TreeMap<>();
        if (courseElement == null) {
            map.put("course", "missing course");
        } else {
            String courseCode = courseElement.getAsString();
            if (courseCode.equals("")) {
                map.put("course", "blank course");
            }
        }

        if (sectionIDElement == null) {
            map.put("section", "missing section");
        } else {
            String sectionIDStr = sectionIDElement.getAsString();
            if (sectionIDStr.equals("")) {
                map.put("section", "blank section");
            }
        }

        return map;
    }

    /**
     * Check for invalid field for course and section
     * @param course the course object
     * @param section the section object
     * @return a TreeMap that contained all the errors during common validation
     */
    public TreeMap<String, String> checkInvalid(Course course, Section section) {
        TreeMap<String, String> map = new TreeMap<>();

        if (course == null) {
            map.put("course", "invalid course");
        }

        if (course != null && section == null) {
            map.put("section", "invalid section");
        }

        return map;
    }

    /**
     * Check for missing/blank fields for userid, amount, course and section
     * @param userIDElement the userIDElement attribute in r parameter
     * @param amountElement the amountElement attribute in r parameter
     * @param sectionElement the sectionElement attribute in r parameter
     * @param courseElement the courseElement attribute in r parameter
     * @return a TreeMap that contained all the errors during common validation
     */
    public TreeMap<String, String> validJSONParameters(JsonElement userIDElement, JsonElement amountElement, JsonElement courseElement, JsonElement sectionElement) {
        TreeMap<String, String> map = new TreeMap<>();

        if (userIDElement == null) {
            map.put("userid", "missing userid");
        } else {
            String userID = userIDElement.getAsString();
            if (userID.equals("")) {
                map.put("userid", "blank userid");
            }
        }

        if (amountElement == null) {
            map.put("amount", "missing amount");
        } else {
            String amountStr = amountElement.getAsString();

            if (amountStr.isEmpty()) {
                map.put("amount", "blank amount");
            }
        }

        if (courseElement == null) {
            map.put("course", "missing course");
        } else {
            String courseID = courseElement.getAsString();
            if (courseID.equals("")) {
                map.put("course", "blank course");
            }
        }

        if (sectionElement == null) {
            map.put("section", "missing section");
        } else {
            String sectionIDStr = sectionElement.getAsString();
            if (sectionIDStr.equals("")) {
                map.put("section", "blank section");
            }
        }

        return map;
    }

    /**
     * Check for invalid field for userID, amount, course and sectionID
     * @param userID the userID attribute in r parameter
     * @param amount the amount attribute in r parameter
     * @param course the course attribute in r parameter
     * @param sectionID the sectionID attribute in r parameter
     * @return a TreeMap that contained all the errors during common validation
     */
    public TreeMap<String, String> checkInvalid(String userID, String amount,
            String course, String sectionID) {
        TreeMap<String, String> map = new TreeMap<>();

        if (!checkValidStudent(userID)) {
            map.put("userid", "invalid userid");
        }

        if (!checkValidAmount(amount)) {
            map.put("amount", "invalid amount");
        }

        if (!checkValidCourse(course)) {
            map.put("course", "invalid course");
        }

        if (checkValidCourse(course) && !checkValidSection(course, sectionID)) {
            map.put("section", "invalid section");
        }

        return map;
    }

    /**
     * Check for valid student
     * @param userID the userID
     * @return true if the student is valid else return false
     */
    public boolean checkValidStudent(String userID) {
        StudentDAO studentDAO = new StudentDAO();
        Student student = studentDAO.retrieveStudent(userID);

        return student != null;
    }

    /**
     * Check for valid amount
     * @param inputAmount the eDollar amount
     * @return true when the the amount is more than or equal to 10 and it is 2 decimal places else return false
     */
    public boolean checkValidAmount(String inputAmount) {
        String value = String.valueOf(inputAmount);
        int index = value.indexOf(".");

        int length = 0;

        if (index != -1) {
            length = value.substring(index).length() - 1;
        }

        return Double.parseDouble(inputAmount) >= 10 && length <= 2;
    }

    /**
     * Check for valid course
     * @param course the course name
     * @return true if the course is valid else return false
     */
    public boolean checkValidCourse(String course) {
        CourseDAO courseDAO = new CourseDAO();
        Course validCourse = courseDAO.retrieveCourse(course);
        return validCourse != null;
    }

    /**
     * Check for valid section
     * @param courseID the courseID
     * @param sectionID the sectionID
     * @return true if the section is valid else return false
     */
    public boolean checkValidSection(String courseID, String sectionID) {
        SectionDAO sectionDAO = new SectionDAO();
        Section section = sectionDAO.retrieve(courseID, sectionID);
        return section != null;
    }

    /**
     * Check for logical validation
     * @param userID the userID
     * @param amount the amount
     * @param courseID the courseID
     * @param sectionID the sectionID
     * @return a TreeMap that contained all the logical validation errors
     */
    public TreeMap<String, String> checkLogicalValidation(String userID, double amount,
            String courseID, String sectionID) {
        TreeMap<String, String> map = new TreeMap<>();

        if (checkIfRound2() && checkBidTooLow(courseID, sectionID, amount)) {
            map.put("bid too low", "bid too low");
        }

        if (checkInsufficientEDollars(userID, courseID, sectionID, amount)) {
            map.put("insufficient e$", "insufficient e$");
        }
        if (checkClassTimeTableClash(userID, courseID, sectionID)) {
            map.put("class timetable clash", "class timetable clash");
        }
        
        if (checkExamTimeTableClash(userID, courseID, sectionID)) {
            map.put("exam timetable clash", "exam timetable clash");
        }

        if (!checkCompletedRequisites(userID, courseID)) {
            map.put("incomplete prerequisites", "incomplete prerequisites");
        }

        if (!checkRoundStatus()) {
            map.put("round ended", "round ended");
        }

        if (checkCompletedCourse(userID, courseID)) {
            map.put("course completed", "course completed");
        }

        if (checkIfCourseEnrolled(userID, courseID, sectionID)) {
            map.put("course enrolled", "course enrolled");
        }

        if (checkSectionLimit(userID, courseID)) {
            map.put("section limit reached", "section limit reached");
        }

        if (checkIfRound1() && !checkOwnSchool(userID, courseID)) {
            map.put("not own school course", "not own school course");
        }

        if (checkVacancy(courseID, sectionID)) {
            map.put("no vacancy", "no vacancy");
        }
        return map;
    }

    /**
     * Check if the bid is too low
     * @param courseID the courseID
     * @param sectionID the sectionID
     * @param amount the amount
     * @return true if the minimum bid amount is greater than current amount else return false
     */
    public boolean checkBidTooLow(String courseID, String sectionID, double amount) {
        SectionDAO sectDAO = new SectionDAO();
        Section sect = sectDAO.retrieve(courseID, sectionID);
        BidDAO bidDAO = new BidDAO();
        double minBidAmt = 0;
        String[] currentRound = bidDAO.getCurrentBidRound();
        if (currentRound != null) {
            int round = Integer.parseInt(currentRound[1]);
            if (round == 2) {
                minBidAmt = bidDAO.calculateMinBid(sect);
            }
        }
        return minBidAmt > amount;
    }

    /**
     * Check for insufficient eDollars
     * @param userID the userID
     * @param courseID the courseID
     * @param sectionID the sectionID
     * @param bidAmount the bidAmount
     * @return true if the existing amount is more than the bid amount else return false
     */
    public boolean checkInsufficientEDollars(String userID, String courseID, String sectionID, double bidAmount) {
        double bidAmountForBiddedCourse = checkCourseBidAmount(userID, courseID);
        boolean courseExists = checkIfCourseExists(userID, courseID);
        StudentDAO studentDAO = new StudentDAO();
        Student student = studentDAO.retrieveStudent(userID);
        double existingAmt = student.geteDollars();

        if (courseExists) {
            existingAmt = existingAmt + bidAmountForBiddedCourse;
        }

        return existingAmt < bidAmount;
    }

    /**
     * Check for Class Timetable Clash 
     * @param userID the userID
     * @param courseID the courseID
     * @param sectionID the sectionID
     * @return true if there is a timetable clash in the student enrolled course or when there is a timetable lash in the bid table
     */
    public boolean checkClassTimeTableClash(String userID, String courseID, String sectionID) {
        // check both student enrolled courses and the existing bids
        BidDAO bidDAO = new BidDAO();
        return clashWithEnrolledSection(userID, courseID, sectionID)
                || bidDAO.checkIfTimeTableClash2(userID, sectionID, courseID);
    }

    /**
     * Check if section clash with student enrolled section
     * @param userID the userID
     * @param courseID the courseID
     * @param sectionID the sectionID
     * @return true Condition 1: when the current courseID equal to the student enrolled section courseID and current sectionID equal to the student enrolled sectionID.
     * Condition 2: when the current courseID is not equal to the student enrolled section courseID and the current day of the section is equal to the student enrolled Day
     * and if the startTime and endTime matches
     */
    public boolean clashWithEnrolledSection(String userID, String courseID, String sectionID) {
        SectionDAO sectDAO = new SectionDAO();

        Section currentSect = sectDAO.retrieve(courseID, sectionID);
        ArrayList<Section> sectList = sectDAO.retrieveSectionsOfStudent(userID);
        for (Section s : sectList) {
            if (s.getCourseID().equals(courseID) && s.getSectionID().equals(sectionID)) {
                return true;
            }

            if (!s.getCourseID().equals(courseID) && s.getDay().equals(currentSect.getDay())) {
                String toCheckStartTime = s.getStartDate();
                String toCheckEndTime = s.getEndDate();
                String currentSectStartTime = currentSect.getStartDate();
                String currentSectEndTime = currentSect.getEndDate();
                if (toCheckStartTime.equals(currentSectStartTime) && toCheckEndTime.equals(currentSectEndTime)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check for Exam Timetable clash
     * @param userID the userID
     * @param courseID the courseID
     * @param sectionID the sectionID
     * @return true if there is a exam timetable clash in the student enrolled course or when there is a exam timetable lash in the bid table
     */
    public boolean checkExamTimeTableClash(String userID, String courseID, String sectionID) {
        // check both student enrolled courses and the existing bids
        BidDAO bidDAO = new BidDAO();
        return clashWithEnrolledCourse(userID, courseID, sectionID) || bidDAO.checkIfExamClash2(userID, courseID);
    }

    /**
     * Check if the course and section clash with student enrolled section and course
     * @param userID the userID
     * @param courseID the courseID
     * @param sectionID the sectionID
     * @return true Condition 1: when the current courseID equal to the student enrolled section courseID and current sectionID equal to the student enrolled sectionID.
     * Condition 2: when the current courseID is not equal to the student enrolled section courseID and the current day of the section is equal to the student enrolled Day
     * and if the startTime and endTime matches
     */
    public boolean clashWithEnrolledCourse(String userID, String courseID, String sectionID) {
        SectionDAO sectDAO = new SectionDAO();
        CourseDAO courseDAO = new CourseDAO();
        Course currentCourse = courseDAO.retrieveCourse(courseID);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");

        ArrayList<Section> sectList = sectDAO.retrieveSectionsOfStudent(userID);
        for (Section s : sectList) {
            Course c = courseDAO.retrieveCourse(s.getCourseID());
            String toCheckExamStartDate = sdf.format(c.getExamDate());
            String currentExamStartDate = sdf.format(currentCourse.getExamDate());

            if (s.getCourseID().equals(courseID) && s.getSectionID().equals(sectionID)) {
                return true;
            }
            
            if (!c.getCourseID().equals(courseID) && toCheckExamStartDate.equals(currentExamStartDate)) {
                String toCheckExamStartTime = c.getExamStart();
                String toCheckExamEndTime = c.getExamEnd();
                String currentExamStartTime = currentCourse.getExamStart();
                String currentExamEndTime = currentCourse.getExamEnd();
                if (toCheckExamStartTime.equals(currentExamStartTime) && toCheckExamEndTime.equals(currentExamEndTime)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if student have completed the prerequisite
     * @param userid the userID
     * @param courseID the courseID
     * @return true if user have completed all the prerequisite for the course else return false
     */
    public static boolean checkCompletedRequisites(String userid, String courseID) {
        StudentDAO studentDAO = new StudentDAO();
        return studentDAO.hasCompletedPrerequisite(userid, courseID);
    }

    /**
     * Check the current round status
     * @return true if the current round is started else return false
     */
    public static boolean checkRoundStatus() {
        BidDAO bidDAO = new BidDAO();
        String[] status = bidDAO.getCurrentBidRound();
        return status[0].equals("started");
    }

    /**
     * Check if the student have completed the course
     * @param userID the userID
     * @param courseID the courseID
     * @return true if the student have completed the course else return false
     */
    public static boolean checkCompletedCourse(String userID, String courseID) {
        StudentDAO studentDAO = new StudentDAO();
        ArrayList<String> completedCourseID = studentDAO.retrieveStudentCompletedCourses(userID);
        for (String completedId : completedCourseID) {
            if (courseID.equals(completedId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the student is enrolled in the course
     * @param userID the userID
     * @param courseID the courseID
     * @param sectionID the sectionID
     * @return true if student is enrolled in the course else return false
     */
    public static boolean checkIfCourseEnrolled(String userID, String courseID, String sectionID) {
        SectionDAO sectDAO = new SectionDAO();
        return sectDAO.checkCourseEnrolled(userID, courseID, sectionID);
    }

    /**
     * Check if the section limit of 5 have been reached.
     * @param userID the userID
     * @param courseID the courseID
     * @return true if there the number of section is less than 5 else if
     * the course exists is in exist in the bids return false
     */
    public static boolean checkSectionLimit(String userID, String courseID) {
        BidDAO bidDAO = new BidDAO();
        int numOfSection = bidDAO.countNumOfBids(userID);

        if (checkIfCourseExists(userID, courseID)) {
            return false;
        }

        return numOfSection >= 5;
    }

    /**
     * Check if the round is 1
     * @return true if the round is 1 else return false
     */
    public static boolean checkIfRound1() {
        BidDAO bidDAO = new BidDAO();
        String[] currentRound = bidDAO.getCurrentBidRound();
        return currentRound[1].equals("1");
    }

    /**
     * Check if the round is 2
     * @return true if the round is 2 else return false
     */
    public static boolean checkIfRound2() {
        BidDAO bidDAO = new BidDAO();
        String[] currentRound = bidDAO.getCurrentBidRound();
        return currentRound[1].equals("2");
    }

    /**
     * Check if student is currently in the same school
     * @param userid the userID
     * @param courseid the courseID
     * @return true if the student is in the same school else return false
     */
    public static boolean checkOwnSchool(String userid, String courseid) {
        StudentDAO studentDAO = new StudentDAO();
        Student student = studentDAO.retrieveStudent(userid);
        String school = student.getSchool();
        CourseDAO courseDAO = new CourseDAO();
        String school2 = courseDAO.retrieveCourse(courseid).getSchool();
        return school.equals(school2);
    }

    /**
     * Check the section vacancy
     * @param courseID the courseID
     * @param sectionID the sectionID
     * @return true if the vacancy is not equal to 0 else return false
     */
    public static boolean checkVacancy(String courseID, String sectionID) {
        SectionDAO sectionDAO = new SectionDAO();
        Section sect = sectionDAO.retrieve(courseID, sectionID);
        int vacancy = sect.checkVacancy() - sectionDAO.retrieveNumOfFilledSections(sect.getCourseID(), sect.getSectionID());
        return vacancy == 0;
    }

    /**
     * Check the course bid amount
     * @param userID the userID
     * @param courseIDToCheck the courseIDToCheck
     * @return the bid amount if the bid courseID matches the currentCourseID else return 0
     */
    public static double checkCourseBidAmount(String userID, String courseIDToCheck) {
        // check course bid amt
        BidDAO bidDAO = new BidDAO();
        ArrayList<Bid> allStudentBids = bidDAO.viewStudentBids(userID);;
        for (Bid bid : allStudentBids) {
            String courseID = bid.getCourseID();
            if (courseID.equals(courseIDToCheck)) {
                return bid.getBidAmount();
            }
        }
        return 0;
    }

    /**
     * Check if the course exists
     * @param userID the userID
     * @param courseIDToCheck the courseIDToCheck
     * @return true if the bid courseID matches the current courseID else return false
     */
    public static boolean checkIfCourseExists(String userID, String courseIDToCheck) {
        // check course bid amt
        BidDAO bidDAO = new BidDAO();
        ArrayList<Bid> allStudentBids = bidDAO.viewStudentBids(userID);;
        for (Bid bid : allStudentBids) {
            String courseID = bid.getCourseID();
            if (courseID.equals(courseIDToCheck)) {
                return true;
            }
        }
        return false;
    }

    /* DROP SECTION */

    /**
     * Check for invalid field for Student, Course, Section and when currentRound is not active
     * @param student the student object
     * @param course the course object
     * @param section the section object
     * @param currentRound the currentRound array
     * @return a TreeMap that contained all the errors during common validation
     */

    public TreeMap<String, String> checkInvalid(Student student, Course course, Section section, String currentRound) {
        TreeMap<String, String> map = new TreeMap<>();

        if (student == null) {
            map.put("student", "invalid student");
        }

        if (course == null) {
            map.put("course", "invalid course");
        }

        if (course != null && section == null) {
            map.put("section", "invalid section");
        }

        if (currentRound.equals("ended")) {
            map.put("troundended", "round not active");
        }

        return map;
    }
}
