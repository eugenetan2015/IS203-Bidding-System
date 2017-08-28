package bios.entity;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Wilson
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Wilson
 */
public class StudentTimeTable {

    private int id;
    private String start;
    private String end;
    private String title;
    private String venue;
    private String courseCode;
    private String sectionID;
    private String color;

    /**
     * Specific Constructor
     * @param id Takes in id of String type
     * @param start Takes in start of String type
     * @param end Takes in end of String type
     * @param sectionID Takes in sectionID of String type
     * @param courseCode Takes in courseCode of String type
     * @param courseTitle Takes in courseTitle of String type
     * @param venue Takes in venue of String type
     * @param color Takes in color of String type
     */
    public StudentTimeTable(int id, String start, String end, String sectionID, String courseCode, String courseTitle, String venue, String color) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.courseCode = courseCode;
        this.venue = venue;
        this.sectionID = sectionID;
        String allStr = String.join(System.getProperty("line.separator"),
                "[" + courseCode + "]" + "\n\n" + courseTitle,
                "(" + sectionID + ")" + "\n\n" + venue);

        title = allStr;
        this.color = color;
    }

    /**
     * Get the StudentTimeTable ID
     * @return the studentTimeTable id
     */
    public int getId() {
        return id;
    }

    /**
     * Get the StudentTimeTable Start Time
     * @return the start time of StudentTimeTable
     */
    public String getStart() {
        return start;
    }

    /**
     * Get the StudentTimeTable End Time
     * @return the end time of StudentTimeTable
     */
    public String getEnd() {
        return end;
    }

    /**
     * Get the StudentTimeTable Title
     * @return the title of the course
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the StudentTimeTable Venue
     * @return the venue of the section
     */
    public String getVenue() {
        return venue;
    }

    /**
     * Get the StudentTimeTable CourseCode
     * @return the courseCode of the course
     */
    public String getCourseCode() {
        return courseCode;
    }

    /**
     * Get the StudentTimeTable SectionID
     * @return the sectionID of the section
     */
    public String getSectionID() {
        return sectionID;
    }

    /**
     * Get the StudentTimeTable Color
     * @return the color used in StudentTimetable
     */
    public String getColor() {
        return color;
    }
}