package bios.dao;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Admin
 */
import bios.entity.*;
import java.sql.*;
import java.util.*;

/**
 *
 * @author qyk1994
 */
public class CourseDAO {

    private ArrayList<Course> allCourses;
    private static final String RETRIEVE_ALL = "select * from course";
    private static final String RETRIEVE_COURSE_COMPLETED = "select * from course_completed order by course_id, user_id";
    private static final String RETRIEVE_ALL_PREREQUISITES = "select * from prerequisite order by course_id, prerequisite_id";

    /**
     * Creates a CourseDAO object containing empty ArrayList used to store courses
     */
    public CourseDAO() {
        //initializes the collection of all courses
        allCourses = new ArrayList<Course>();
    }

    /**
     * retrieve Arraylist of all the Courses stored in CourseDAO object
     * @return list of courses from the database
     */
    public ArrayList<Course> retrieveAllCourses() {

        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(RETRIEVE_ALL);
                ResultSet rs = stmt.executeQuery();) {

            while (rs.next()) {
                String course_id = rs.getString(1);
                String school = rs.getString(2);
                String title = rs.getString(3);
                String desc = rs.getString(4);
                java.sql.Date dbExamDate = rs.getDate(5);
                String examStartTime = rs.getString(6);
                String examEndTime = rs.getString(7);

                java.util.Date examDate = new java.util.Date(dbExamDate.getTime());
                allCourses.add(new Course(course_id, school, title, desc, examDate, examStartTime, examEndTime));
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allCourses;
    }

    /**
     * retrieve a specificied Course object given the course ID
     * @param course_id Takes in a course_id
     * @return a course object based on the course_id
     */
    public Course retrieveCourse(String course_id) {
        Course toReturn = null;
        try {Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(RETRIEVE_ALL + " where course_id=?");
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
     * retrieve arraylist of prerequisiteCourses for a specific course
     * @param course_id Takes in a course_id string
     * @return a list of String based on the course_id
     */
    public ArrayList<String> retrievePrerequisiteCoursesID(String course_id) {
        ArrayList<String> toReturn = new ArrayList<>();
        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT prerequisite_id FROM prerequisite where course_id=?");
            stmt.setString(1, course_id);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String courseId = rs.getString(1);
                
                toReturn.add(courseId);
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
     * Retrieve ArrayList of String arrays containing the courses completed
     * @return a list of string for all the course Completed
     */
    public ArrayList<String[]> retrieveCourseCompleted() {
        ArrayList<String[]> toReturn = new ArrayList<>();
        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(RETRIEVE_COURSE_COMPLETED);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String userID = rs.getString(1);
                String courseID = rs.getString(2);
                toReturn.add(new String[]{userID, courseID});
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
     * Retrieve ArrayList of String Arrays containing the Prerequisite
     * @return a list of prerequisite from the database
     */
    public ArrayList<String[]> retrieveAllPrerequisites() {
        ArrayList<String[]> toReturn = new ArrayList<>();
        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(RETRIEVE_ALL_PREREQUISITES);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String courseID = rs.getString(1);
                String prerequisiteID = rs.getString(2);
                toReturn.add(new String[]{courseID, prerequisiteID});
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

}
