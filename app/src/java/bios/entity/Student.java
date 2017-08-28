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
public class Student {
    private String studentID;
    private String password;
    private String name;
    private String school;
    private Double eDollars;
    private ArrayList<Course> coursesTaken;
    private ShoppingCart shoppingCart;
    
    //new student (freshman), no courses taken prior

    /**
     * Specific Constructor
     * @param studentID Takes in studentID of String type
     * @param password Takes in password of String type
     * @param name Takes in name of String type
     * @param school Takes in school of String type
     * @param eDollars Takes in eDollars of String type
     */
    public Student(String studentID, String password, String name, String school, Double eDollars) {
        this.studentID = studentID;
        this.password = password;
        this.name = name;
        this.school = school;
        this.eDollars = eDollars;
        //when student is created (new), he/she has yet to take a course
        this.coursesTaken = new ArrayList<Course>();
        this.shoppingCart = new ShoppingCart();
    }
    
    /**
     * Get the Student StudentID
     * @return the studentID
     */
    public String getStudentID() {
        return studentID;
    }

    /**
     * Get the Student Password
     * @return the student password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Get the Student Name
     * @return the student Name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the Student School
     * @return the student School
     */
    public String getSchool() {
        return school;
    }

    /**
     * Get the Student EDollars
     * @return the student eDollars
     */
    public Double geteDollars() {
        return eDollars;
    }
    
    /**
     * Get the list of courses taken by the student
     * @return an arrayList of Course type which contained all the coursesTaken by the student
     */
    public ArrayList<Course> getCoursesTaken() {
        return coursesTaken;
    }
    
    /**
     * Drop course for Student
     * @param course - the course to drop
     * @return true if courseTaken is equal to the current course else return false
     */
    public boolean dropCourse(Course course){
        for(int i = 0; i < coursesTaken.size(); i++) {
            if(coursesTaken.get(i).equals(course)){
                coursesTaken.remove(i);
                return true;
            }
        }
        return false;
    }
    
    //add courses to the student's list of courses done

    /**
     * Add a course for Student
     * @param course - the course to add
     */
    public void addCourse(Course course) {
        coursesTaken.add(course);
    }
    
    /**
     * Add eDollar to Student
     * @param addAmount - the addAmount to add
     */
    public void addDollars(double addAmount) {
        eDollars += addAmount;
    }
    
    /**
     * Deduct amount from Student
     * @param deductAmount - the deductAmount to deduct
     */
    public void deductDollars(double deductAmount) {
        eDollars -= deductAmount;
    }
    
    
}
