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
public class Bid implements Comparable<Bid> {

    private Student student;
    private double bidAmount;
    private String courseID;
    private String sectionID;
    private String status;
    private int round;

    /**
     * Specific Constructor
     *
     * @param student Takes in student of Student type
     * @param bidAmount Takes in bidAmount of double type
     * @param courseID Takes in courseID of String type
     * @param sectionID Takes in sectionID of String type
     * @param status Takes in status of String type
     * @param round Takes in round of int type
     */
    public Bid(Student student, double bidAmount, String courseID, String sectionID, String status, int round) {
        this.student = student;
        this.bidAmount = bidAmount;
        this.courseID = courseID;
        this.sectionID = sectionID;
        this.status = status;
        this.round = round;
    }

    /**
     * Specific Constructor
     *
     * @param courseID Takes in courseID of String type
     * @param sectionID Takes in sectionID of String type
     * @param status Takes in status of String type
     */
    public Bid(String courseID, String sectionID, String status) {
        this.courseID = courseID;
        this.sectionID = sectionID;
        this.status = status;
    }

    /**
     * Get the Student object
     *
     * @return the student object
     */
    public Student getStudent() {
        return student;
    }

    /**
     * Get the Bid Amount
     *
     * @return the bid amount
     */
    public double getBidAmount() {
        return bidAmount;
    }

    /**
     * Get the Bid CourseID
     *
     * @return the bid courseID
     */
    public String getCourseID() {
        return courseID;
    }

    /**
     * Get the Bid SectionID
     *
     * @return the bid sectionID
     */
    public String getSectionID() {
        return sectionID;
    }

    /**
     * Get the Bid Status
     *
     * @return the bid Status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set the Bid status
     *
     * @param status Set the status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Compare current bid with another bid
     *
     * @param anotherBid - the bid to compare
     */
    @Override
    public int compareTo(Bid anotherBid) {
        return Double.compare(anotherBid.bidAmount, bidAmount);
    }

    /**
     * Get the Bid Round
     *
     * @return the bid round (e.g. 1 or 2)
     */
    public int getBiddingRound() {
        return round;
    }
}
