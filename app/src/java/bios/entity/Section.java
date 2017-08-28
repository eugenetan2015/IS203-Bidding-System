package bios.entity;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Admin
 */
import java.util.*;

/**
 *
 * @author Admin
 */
public class Section {
    private String courseID;
    private String sectionID;
    private String day;
    private String startDate;
    private String endDate;
    private String instructor;
    private String venue;
    private int classSize;
    private ArrayList<Student> studentList;
    private double eDollars;
    private String userID;
    
    /**
     * Specific Constructor
     * @param sectionID Takes in sectionID of String type
     * @param courseID Takes in courseID of String type
     * @param day Takes in day of String type
     * @param startDate Takes in startDate of String type
     * @param endDate Takes in endDate of String type
     * @param instructor Takes in instructor of String type
     * @param venue Takes in venue of String type
     * @param classSize Takes in classSize of int type
     */
    public Section(String sectionID, String courseID, String day, String startDate, String endDate, String instructor, String venue, int classSize) {
        this.sectionID = sectionID;
        this.courseID = courseID;
        this.day = day;
        this.startDate = startDate;
        this.endDate = endDate;
        this.instructor = instructor;
        this.venue = venue;
        this.classSize = classSize;
        this.studentList = new ArrayList<Student>();
    }
    
    /**
     *
     * @param userID Takes in an userID of String type
     * @param eDollars Takes in eDollar of double type
     */
    public Section(String userID, double eDollars) {
        this.userID = userID;
        this.eDollars = eDollars;
    }
    
    /**
     * Get the Section SectionID
     * @return the current sectionID
     */
    public String getSectionID() {
        return sectionID;
    }
   
    /**
     * Get the Section CourseID
     * @return the section courseID
     */
    public String getCourseID() {
        return courseID;
    }

    /**
     * Get the Section Day
     * @return the section Day
     */
    public String getDay() {
        return day;
    }

    /**
     * Get the Section StartDate
     * @return the section startDate
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Get the Section EndDate
     * @return the section endDate
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * Get the Section Instructor
     * @return the section Instructor
     */
    public String getInstructor() {
        return instructor;
    }

    /**
     * Get the Section Venue
     * @return the section venue
     */
    public String getVenue() {
        return venue;
    }

    /**
     * Get the Section Class Size
     * @return the section class size
     */
    public int getClassSize() {
        return classSize;
    }

    /**
     * Get the list of students in the section
     * @return the students
     */
    public ArrayList<Student> getStudentList() {
        return studentList;
    }

    /**
     * Get the Section Vacancy
     * @return the number of vacancy available
     */
    public int checkVacancy(){
        return classSize - studentList.size();
    }

    /**
     * Get the Section eDollars
     * @return the eDollars amount
     */
    public double geteDollars() {
        return eDollars;
    }

    /**
     * Get the Section UserID
     * @return the userID
     */
    public String getUserID() {
        return userID;
    }
}
