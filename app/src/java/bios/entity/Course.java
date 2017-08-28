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
public class Course {
    
    private String courseID;
    private ArrayList<Course> prerequisites;
    private String school;
    private String title;
    private String description;
    private Date examDate;
    private String examStart;
    private String examEnd;
    private ArrayList<Section> sections;

    /**
     * Specific Constructor
     * @param courseID Takes in courseID of String type
     * @param school Takes in school of String type
     * @param title Takes in title of String type
     * @param description Takes in description of String type
     * @param examDate Takes in examDate of Date type
     * @param examStart Takes in examStart of Date type
     * @param examEnd Takes in examEnd of Date type
     */
    public Course(String courseID, String school, String title, String description, Date examDate, String examStart, String examEnd) {
        this.courseID = courseID;
        //creates new arraylist to add prerequisites to
        this.prerequisites = new ArrayList<>();
        //creates to add sections to
        this.sections = new ArrayList<>();
        this.school = school;
        this.title = title;
        this.description = description;
        this.examDate = examDate;
        this.examStart = examStart;
        this.examEnd = examEnd;
    }

    /**
     * Get the Course ID
     * @return the courseID
     */
    public String getCourseID() {
        return courseID;
    }

    /**
     * Get all the prerequisite for this course
     * @return the prerequisite
     */
    public ArrayList<Course> getPrerequisites() {
        return prerequisites;
    }

    /**
     * Get the Course School
     * @return the school
     */
    public String getSchool() {
        return school;
    }

    /**
     * Get the Course Title
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the Course Description
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the Course Exam Date
     * @return the ExamDate
     */
    public Date getExamDate() {
        return examDate;
    }

    /**
     * Get the Course Exam Start Time
     * @return the ExamStartTime
     */
    public String getExamStart() {
        return examStart;
    }

    /**
     * Get the Course Exam End Time
     * @return the ExamEnd
     */
    public String getExamEnd() {
        return examEnd;
    }

    /**
     * Add the course to the prerequisite list
     * @param course - the course to be added to the prerequisite list
     */
    public void addPrerequisite(Course course) {
        prerequisites.add(course);
    }
}
