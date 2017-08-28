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
import bios.entity.Course;
import bios.entity.Student;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;

/**
 *
 * @author qyk1994
 */
public class StudentDAO {

    /**
     *
     */
    public static ArrayList<Student> allStudents;
    private static final String RETRIEVE_ALL = "select * from student where user_id != 'admin'";
    private static final String RETRIEVE_USERNAME = "select * from student where user_id = ?";
    private static final String USER_EDOLLARS = "SELECT EDOLLAR FROM STUDENT WHERE USER_ID = ?";
    private static final String UPDATE_EDOLLARS = "UPDATE STUDENT SET EDOLLAR = ? WHERE USER_ID = ?";

    static {
        if (allStudents == null || allStudents.size() < 1) {
            allStudents = retrieveAll();
        }
    }

    /*public StudentDAO() {
        this.allStudents = retrieveAll();
        //adds in all the students after

    }*/

    /**
     * retrieve ArrayList of all students
     * @return the list of students from the database
     */

    public static ArrayList<Student> retrieveAll() {
        //return studentList;
        ArrayList<Student> result = new ArrayList<>();

        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(RETRIEVE_ALL);
                ResultSet rs = stmt.executeQuery();) {

            while (rs.next()) {
                String id = rs.getString(1);
                String pw = rs.getString(2);
                String name = rs.getString(3);
                String school = rs.getString(4);
                double eDollars = rs.getDouble(5);
                result.add(new Student(id, pw, name, school, eDollars));
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * retrieve the Student object given the specific user_id
     * @param user_id Takes in a user_id of String type
     * @return Student object
     */
    public Student retrieveStudent(String user_id) {
        Student toReturn = null;
        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(RETRIEVE_USERNAME);
            stmt.setString(1, user_id);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String id = rs.getString(1);
                String pw = rs.getString(2);
                String name = rs.getString(3);
                String school = rs.getString(4);
                double eDollars = rs.getDouble(5);
                toReturn = new Student(id, pw, name, school, eDollars);
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
     * deduct specified edollars amount from specified student object
     * @param user_id Takes in a user_id of String type
     * @param amount Takes in an amount of double type
     * @return true if successful, false otherwise
     */
    public boolean deduct(String user_id, double amount) {

        double balance = retrieveEdollars(user_id);
        if (balance == 0.0) {
            return false;
        } else {
            try {
                Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(UPDATE_EDOLLARS);
                DecimalFormat df = new DecimalFormat("0.00");
                double new_balance = balance - amount;
                String amountStr = "";
                double amt = 0.0;
                if (new_balance != 0.0) {
                    amountStr = df.format(new_balance);
                    amt = Double.parseDouble(amountStr);
                }
                if (new_balance < 0) {
                    return false;
                }
                stmt.setDouble(1, amt);
                stmt.setString(2, user_id);
                stmt.execute();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return true;
        }
    }

    /**
     * retrieve Edollars amount from specified student from student id
     * @param user_id Takes in an user_id of String type
     * @return double amount of edollars of student object
     */
    public double retrieveEdollars(String user_id) {
        double balance = 0;
        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(USER_EDOLLARS);
            stmt.setString(1, user_id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                balance = rs.getDouble(1);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balance;
    }

    /**
     * Refund specified amount to the specified student object
     * @param user_id Takes in a user_id of String type
     * @param amount Takes in a amount of amount type
     * @return true if successful, false if otherwise
     */
    public boolean refundStudent(String user_id, double amount) {
        //retrieve student
        Student student = retrieveStudent(user_id);
        //add amount back to his/her balance
        student.addDollars(amount);
        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(USER_EDOLLARS);
            stmt.setString(1, user_id);
            ResultSet rs = stmt.executeQuery();
            double balance = 0;
            while (rs.next()) {
                balance = rs.getDouble(1);
            }
            try {
                Connection conn1 = ConnectionManager.getConnection();
                PreparedStatement stmt1 = conn.prepareStatement(UPDATE_EDOLLARS);
                double new_balance = balance + amount;
                stmt1.setDouble(1, new_balance);
                stmt1.setString(2, user_id);
                stmt1.execute();
                stmt1.close();
                conn1.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;

    }

    /**
     * Refund specified amount to the specified student object
     * @param user_id Takes in a user_id of String type
     * @param amount Takes in an amount of double type
     * @return true if successful, false if otherwise
     */
    public boolean refundStudent2(String user_id, double amount) {
        //retrieve student
        try {
            Connection conn1 = ConnectionManager.getConnection();
            PreparedStatement stmt1 = conn1.prepareStatement(UPDATE_EDOLLARS);
            stmt1.setDouble(1, amount);
            stmt1.setString(2, user_id);
            stmt1.execute();
            stmt1.close();
            conn1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;

    }

    /**
     * return Arraylist of all students
     * @return arraylist of all students
     */
    public ArrayList<Student> retrieveAllStudents() {
        return allStudents;
    }

    /**
     * return completed courses of specified student object from the given Student object
     * @param studentID Takes in a studentID of String type
     * @return an arraylist of student completed courses
     */
    public ArrayList<String> retrieveStudentCompletedCourses(String studentID) {
        ArrayList<String> toReturn = new ArrayList<>();
        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT course_id FROM course_completed where user_id=?");
            stmt.setString(1, studentID);
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
     * Checks if student has completed the specified prerequisite
     * @param studentID Takes in a studentID of String type
     * @param courseID Takes in a courseID of String type
     * @return true if student has completed, false otherwise
     */
    public boolean hasCompletedPrerequisite(String studentID, String courseID) {
        CourseDAO courseDAO = new CourseDAO();
        ArrayList<String> prerequisiteIDs = courseDAO.retrievePrerequisiteCoursesID(courseID);
        if (prerequisiteIDs == null || prerequisiteIDs.size() < 1) {
            return true;
        }
        ArrayList<String> studCompletedCourses = retrieveStudentCompletedCourses(studentID);
        for (int i = 0; i < prerequisiteIDs.size(); i++) {
            String currentPrerequisiteCourseID = prerequisiteIDs.get(i);
            for (String currentStudentCompletedCourseID : studCompletedCourses) {
                if (currentPrerequisiteCourseID.equals(currentStudentCompletedCourseID)) {
                    prerequisiteIDs.remove(i);
                }
            }
        }

        if (prerequisiteIDs.size() == 0) {
            return true;
        } else {
            return false;
        }
    }
}
