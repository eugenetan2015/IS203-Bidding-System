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
import bios.entity.Bid;
import bios.entity.Course;
import bios.entity.Section;
import bios.entity.Student;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author Quek Yew Kit
 */
public class BidDAO {

    private static final String ADD_BIDS = "INSERT INTO `is203`.`bid` (`User_id`, `Course_id`, `Section_id`, `Round`, `Status`, `BidAmount`) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String CHECK_BID_EXISTS = "SELECT * from bid where user_id = ? and course_id = ? and round = ?";
    private static final String RETRIEVE_ALL_BIDS_OF_USER = "select bidAmount, course_id, section_id, status, round from bid where user_id = ?";
    private static final String RETRIEVE_ALL_BIDS_OF_USER2 = "select bidAmount, course_id, section_id, status, round from bid where user_id = ? and round = ?";
    private static final String RETRIEVE_ALL_BIDS_FOR_SECTION = "select * from bid where course_id = ? and section_id = ? and round = ? order by bidamount desc, user_id";
    private static final String DELETE_FROM_BID_TABLE = "DELETE FROM `is203`.`bid` WHERE `bid`.`User_id` = ? AND `bid`.`Course_id` = ? AND `bid`.`Section_id` = ? AND CONCAT(`bid`.`Round`) = ?";
    private static final String RETRIEVE_BIDDING_CART = "select * from bidding_cart where user_id = ?";
    private static final String RETRIEVE_ALL = "select * from bid order by course_id, section_id, bidamount desc, user_id";
    private static final String UPDATE_MIN_BID = "UPDATE minimum_bid SET amount = ? WHERE course_id = ? AND section_id = ?";
    private static final String ADD_MIN_BIDS = "INSERT INTO `is203`.`minimum_bid` (`course_id`, `section_id`, `amount`) VALUES (?, ?, ?)";
    private static final String DELETE_ALL_MIN_BIDS = "delete from minimum_bid";
    private static final String RETRIEVE_MIN_BID = "select amount from minimum_bid where course_id = ? and section_id = ?";
    private static final String RETRIEVE_SUCCESSFUL_BIDS = "SELECT * FROM section_student WHERE course_id = ? and section_id = ? order by bidamount desc, user_id";
    private static final String RETRIEVE_SUCCESSFUL_BIDS_FROM_BID_IN_ROUND2 = "SELECT * from bid where round = 2 and status = 'success' order by bidamount";

    /* ADDED FOR JSON - UPDATE_BID */
    private static final String UPDATE_STUDENT_BID = "update bid set bidAmount = ? where course_id = ? and section_id = ? and user_id = ?";
    private static final String RETRIEVE_BID_EDOLLAR = "select bidAmount from bid where user_id = ? and course_id = ? and section_id = ?";

    private static String SELECTED_BID_ROUND1 = "select * from bid where round = ?";
    private static String RETRIEVE_SECTION_FROM_BID = "select distinct course_id, section_id from bid where round = ?";
    private static String RETRIEVE_A_BID = "select * from bid where user_id = ? and course_id = ? and section_id = ? and round = ?";

    private ArrayList<Bid> allBids;
    String[] roundStatus = getCurrentBidRound();
    int currentRound = Integer.parseInt(roundStatus[1]);

    /**
     * Creates a BidDAO object with an empty arraylist - stores all the
     * different bids
     */
    public BidDAO() {
        this.allBids = new ArrayList<>();
    }

    /**
     * Adds a bid to BidDAO
     *
     * @param bid pass in a bid object
     * @return true if successful, false otherwise
     */
    public boolean addBid(Bid bid) {
        try {
            allBids.add(bid);
            //if(sc.nextDouble()==0){
            //return false;
            //}
            String user_id = bid.getStudent().getStudentID();
            String course_id = bid.getCourseID();
            String section_id = bid.getSectionID();
            double round = Double.parseDouble(roundStatus[1]);
            String status = "Pending";
            double bidAmt = bid.getBidAmount();
            try {
                Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(CHECK_BID_EXISTS);
                stmt.setString(1, user_id);
                stmt.setString(2, course_id);
                stmt.setInt(3, currentRound);
                ResultSet rs = stmt.executeQuery();
                if (rs.first() == true) {
                    return false;
                }
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Connection conn1 = null;
            PreparedStatement stmt1 = null;
            try {
                conn1 = ConnectionManager.getConnection();
                stmt1 = conn1.prepareStatement(ADD_BIDS);
                stmt1.setString(1, user_id);
                stmt1.setString(2, course_id);
                stmt1.setString(3, section_id);
                stmt1.setDouble(4, round);
                stmt1.setString(5, status);
                stmt1.setDouble(6, bidAmt);
                stmt1.execute();
                stmt1.close();
                conn1.close();
                return true;
            } catch (SQLException e) {
                return true; // why does it return false here
            }
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * returns ArrayList of Bids stored in BidDAO() object
     *
     * @return all the bids
     */
    public ArrayList<Bid> retrieveAll() {
        return allBids;
    }

    /**
     * returns ArrayList of Bids stored in BidDAO() object
     *
     * @return all the bids
     */
    public ArrayList<Bid> retrieveAllBids() {
        ArrayList<Bid> bids = new ArrayList<Bid>();

        StudentDAO stuDao = new StudentDAO();
        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(RETRIEVE_ALL);
            ResultSet rs = null;
            rs = stmt.executeQuery();
            while (rs.next()) {
                String user_id = rs.getString(1);
                String course_id = rs.getString(2);
                String section_id = rs.getString(3);
                int currentRound = rs.getInt(4);
                String status = rs.getString(5);
                double bidAmt = rs.getDouble(6);
                Bid addThis = new Bid(stuDao.retrieveStudent(user_id), bidAmt, course_id, section_id, status, currentRound);
                bids.add(addThis);
            }
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            return null;
        }
        return bids;
    }

    /**
     *
     * @param userID Current user - userID. E.g. amy.tan.2009
     * @param courseID Current course - courseID. E.g. IS101
     * @param sectionID Current section - sectionID. E.g. S1
     * @param round Current Round. E.g. 1
     * @return current Bid object with the specified details, if Bid does not
     * exist, return null exist, return null
     */
    public Bid retrieveABids(String userID, String courseID, String sectionID, int round) {
        StudentDAO stuDao = new StudentDAO();
        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(RETRIEVE_A_BID);
            stmt.setString(1, userID);
            stmt.setString(2, courseID);
            stmt.setString(3, sectionID);
            stmt.setInt(4, round);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String user_id = rs.getString(1);
                String course_id = rs.getString(2);
                String section_id = rs.getString(3);
                int currentRound = rs.getInt(4);
                String status = rs.getString(5);
                double bidAmt = rs.getDouble(6);
                Bid addThis = new Bid(stuDao.retrieveStudent(user_id), bidAmt, course_id, section_id, status, currentRound);
                return addThis;
            }
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    /**
     * Generate the Bid Round If vacancy of the section is more than the number
     * of bids, automatically adds all the bids to the section minimum clearing
     * price is set based on the number of vacancies, i.e. if the class has 35
     * vacancies, the 35th highest bid is the clearing price. If there is only
     * one bid at the clearing price, it will be successful. Otherwise, all bids
     * at the clearing price will be dropped regardless of whether they can
     * technically all be accommodated
     *
     * @param allBidsForASection List of Bids For A Section
     * @param section Current section object
     * @param round Current round
     */
    public void generateBidsRound(ArrayList<Bid> allBidsForASection, Section section, int round) {
        ArrayList<Bid> successfulBids = new ArrayList<>();
        ArrayList<Bid> unsuccessfulBids = new ArrayList<>();

        //retrieve vacancy
        SectionDAO sectionDAO = new SectionDAO();
        StudentDAO studentDAO = new StudentDAO();
        int maxSpace = section.checkVacancy();
        int filledSpace = sectionDAO.retrieveNumOfFilledSections(section.getCourseID(), section.getSectionID());
        int vacancy = maxSpace - filledSpace;
        int index = vacancy - 1;
        boolean checkIfBeforeIndexSameAmount = false;
        boolean checkIfAfterIndexSameAmount = false;
        Collections.sort(allBidsForASection);

        if (vacancy > allBidsForASection.size()) {

            for (Bid b : allBidsForASection) {
                successfulBids.add(b);
            }

        } else if (vacancy <= allBidsForASection.size() && vacancy > 0) {
            double toCheckAmount = allBidsForASection.get(index).getBidAmount();
            for (int i = 0; i < index; i++) {
                Bid currentBid = allBidsForASection.get(i);
                double currentBidAmount = currentBid.getBidAmount();
                if (toCheckAmount == currentBidAmount) {
                    checkIfBeforeIndexSameAmount = true;
                }
            }

            for (int i = vacancy; i < allBidsForASection.size(); i++) {
                Bid currentBid = allBidsForASection.get(i);
                double currentBidAmount = currentBid.getBidAmount();
                if (toCheckAmount == currentBidAmount) {
                    checkIfAfterIndexSameAmount = true;
                }
            }

            if (round == 2) {

                if (checkIfBeforeIndexSameAmount && !checkIfAfterIndexSameAmount) {
                    successfulBids = seperateList(0, index, allBidsForASection);
                    unsuccessfulBids = seperateList(index + 1, allBidsForASection.size() - 1, allBidsForASection);

                } else if (checkIfBeforeIndexSameAmount && checkIfAfterIndexSameAmount) {
                    int indexOfFirstOccurance = getFirstIndexOfBidAmount(toCheckAmount, allBidsForASection);
                    if (indexOfFirstOccurance != 0) {
                        successfulBids = seperateList(0, indexOfFirstOccurance - 1, allBidsForASection);
                        unsuccessfulBids = seperateList(indexOfFirstOccurance, allBidsForASection.size() - 1, allBidsForASection);
                    } else {
                        unsuccessfulBids = seperateList(0, allBidsForASection.size() - 1, allBidsForASection);
                    }
                } else if (!checkIfBeforeIndexSameAmount && checkIfAfterIndexSameAmount) {
                    successfulBids = seperateList(0, index - 1, allBidsForASection);
                    unsuccessfulBids = seperateList(index, allBidsForASection.size() - 1, allBidsForASection);

                } else if (!checkIfBeforeIndexSameAmount && !checkIfAfterIndexSameAmount) {
                    successfulBids = seperateList(0, index, allBidsForASection);
                    unsuccessfulBids = seperateList(index + 1, allBidsForASection.size() - 1, allBidsForASection);
                } else {
                    //all unsuccessful
                    for (Bid b : allBidsForASection) {
                        unsuccessfulBids.add(b);
                    }
                }

            } else if (round == 1) {

                if (checkIfBeforeIndexSameAmount) {
                    int indexOfFirstOccurance = getFirstIndexOfBidAmount(toCheckAmount, allBidsForASection);
                    if (indexOfFirstOccurance != 0) {
                        successfulBids = seperateList(0, indexOfFirstOccurance - 1, allBidsForASection);
                        unsuccessfulBids = seperateList(indexOfFirstOccurance, allBidsForASection.size() - 1, allBidsForASection);
                    } else {
                        unsuccessfulBids = seperateList(0, allBidsForASection.size() - 1, allBidsForASection);
                    }
                } else if (checkIfAfterIndexSameAmount) {
                    successfulBids = seperateList(0, index - 1, allBidsForASection);
                    unsuccessfulBids = seperateList(index, allBidsForASection.size() - 1, allBidsForASection);
                } else {
                    successfulBids = seperateList(0, index, allBidsForASection);
                    unsuccessfulBids = seperateList(index + 1, allBidsForASection.size() - 1, allBidsForASection);
                }

            }
        }
        for (Bid unsuccessfulBid : unsuccessfulBids) {
            Student currentStudent = unsuccessfulBid.getStudent();
            String studentID = currentStudent.getStudentID();
            String courseID = unsuccessfulBid.getCourseID();
            String sectionID = unsuccessfulBid.getSectionID();
            double bidAmount = unsuccessfulBid.getBidAmount();
            updateBidStatus(studentID, courseID, sectionID, round, "Fail");
            studentDAO.refundStudent(studentID, bidAmount);
        }

        for (Bid successBid : successfulBids) {
            Student currentStudent = successBid.getStudent();
            String studentID = currentStudent.getStudentID();
            String courseID = successBid.getCourseID();
            String sectionID = successBid.getSectionID();
            double bidAmount = successBid.getBidAmount();
            updateBidStatus(studentID, courseID, sectionID, round, "Success");
            sectionDAO.insertSectionStudent(studentID, courseID, sectionID, bidAmount);
        }

    }

    /**
     * calculates the minimum bid based on the section code
     *
     * @param section Current section object
     * @return the minimum bid amount
     */
    public double calculateMinBid(Section section) {

        String courseID = section.getCourseID();
        String sectionID = section.getSectionID();
        double minBid = 0;
        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(RETRIEVE_MIN_BID);
            ResultSet rs = null;
            stmt.setString(1, courseID);
            stmt.setString(2, sectionID);
            rs = stmt.executeQuery();
            while (rs.next()) {
                minBid = rs.getDouble(1);
            }
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            return 0;
        }
        return minBid;

    }

    /**
     * Takes in a student Bid object and Section code, returns true if Student's
     * Bid amount is above the clearing price, returns false otherwise
     * generateBids
     *
     * @param studentBid pass in studentBid
     * @param toCheck Current Section to check
     * @return true if successful, false otherwise
     */
    public boolean stillInRange(Bid studentBid, Section toCheck) {

        ArrayList<Bid> successfulBids = new ArrayList<Bid>();
        ArrayList<Bid> unsuccessfulBids = new ArrayList<Bid>();
        SectionDAO sectionDAO = new SectionDAO();
        ArrayList<Bid> allBidsForASection = viewAllBidsForSection(toCheck.getCourseID(), toCheck.getSectionID(), 2); //method only called in round 2
        int maxSpace = toCheck.checkVacancy();
        int filledSpace = sectionDAO.retrieveNumOfFilledSections(toCheck.getCourseID(), toCheck.getSectionID());
        int vacancy = maxSpace - filledSpace;
        int index = vacancy - 1;
        boolean checkIfBeforeIndexSameAmount = false;
        boolean checkIfAfterIndexSameAmount = false;
        Collections.sort(allBidsForASection);

        if (vacancy >= allBidsForASection.size()) {
            //all successful
            for (Bid b : allBidsForASection) {
                successfulBids.add(b);
            }
        } else if (vacancy < allBidsForASection.size() && vacancy > 0) {
            double toCheckAmount = allBidsForASection.get(index).getBidAmount();
            for (int i = 0; i < index; i++) {
                Bid currentBid = allBidsForASection.get(i);
                double currentBidAmount = currentBid.getBidAmount();
                if (toCheckAmount == currentBidAmount) {
                    checkIfBeforeIndexSameAmount = true;
                }
            }

            for (int i = vacancy; i < allBidsForASection.size(); i++) {
                Bid currentBid = allBidsForASection.get(i);
                double currentBidAmount = currentBid.getBidAmount();
                if (toCheckAmount == currentBidAmount) {
                    checkIfAfterIndexSameAmount = true;
                }
            }

            if (checkIfBeforeIndexSameAmount && !checkIfAfterIndexSameAmount) {
                successfulBids = seperateList(0, index, allBidsForASection);
                unsuccessfulBids = seperateList(index + 1, allBidsForASection.size() - 1, allBidsForASection);

            } else if (checkIfBeforeIndexSameAmount && checkIfAfterIndexSameAmount) {
                int indexOfFirstOccurance = getFirstIndexOfBidAmount(toCheckAmount, allBidsForASection);
                if (indexOfFirstOccurance != 0) {
                    successfulBids = seperateList(0, indexOfFirstOccurance - 1, allBidsForASection);
                    unsuccessfulBids = seperateList(indexOfFirstOccurance, allBidsForASection.size() - 1, allBidsForASection);
                } else {
                    unsuccessfulBids = seperateList(0, allBidsForASection.size() - 1, allBidsForASection);
                }
            } else if (!checkIfBeforeIndexSameAmount && checkIfAfterIndexSameAmount) {
                successfulBids = seperateList(0, index - 1, allBidsForASection);
                unsuccessfulBids = seperateList(index, allBidsForASection.size() - 1, allBidsForASection);

            } else if (!checkIfBeforeIndexSameAmount && !checkIfAfterIndexSameAmount) {
                successfulBids = seperateList(0, index, allBidsForASection);
                unsuccessfulBids = seperateList(index + 1, allBidsForASection.size() - 1, allBidsForASection);
            }
        } else {
            //all unsuccessful
            for (Bid b : allBidsForASection) {
                unsuccessfulBids.add(b);
            }
        }

        String toCheckStudentID = studentBid.getStudent().getStudentID();
        String toCheckCourseID = studentBid.getCourseID();
        String toCheckSectionID = studentBid.getSectionID();
        boolean toReturn = false;

        for (Bid successBid : successfulBids) {
            Student currentStudent = successBid.getStudent();
            String studentID = currentStudent.getStudentID();
            String courseID = successBid.getCourseID();
            String sectionID = successBid.getSectionID();
            if (studentID.equals(toCheckStudentID) && courseID.equals(toCheckCourseID) && sectionID.equals(toCheckSectionID)) {
                toReturn = true;
            }
        }
        return toReturn;
    }

    /**
     * Drops the current Bid
     *
     * @param bid Current bid
     * @param round Current round
     * @return true if successful, false otherwise
     */
    public boolean dropBid(Bid bid, int round) {
        try {

            String user_id = bid.getStudent().getStudentID();
            String course_id = bid.getCourseID();
            String section_id = bid.getSectionID();
            String status = "Pending";
            double bidAmt = bid.getBidAmount();

            for (int i = 0; i < allBids.size(); i++) {
                if (allBids.get(i).equals(bid)) {
                    allBids.remove(i);
                    return true;
                }
            }
            try {
                Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(DELETE_FROM_BID_TABLE);
                stmt.setString(1, user_id);
                stmt.setString(2, course_id);
                stmt.setString(3, section_id);
                stmt.setInt(4, round);
                //stmt.setString(5, status);
                //stmt.setDouble(6, bidAmt);
                stmt.execute();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * takes in a student ID, returns arraylist of Bids of the student
     *
     * @param studentID Current Student - studentID
     * @return list of student Bids
     */
    public ArrayList<Bid> viewStudentBids(String studentID) {
        ArrayList<Bid> studentBids = new ArrayList<>();

        StudentDAO stuDao = new StudentDAO();
        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(RETRIEVE_ALL_BIDS_OF_USER);
            ResultSet rs = null;
            stmt.setString(1, studentID);
            rs = stmt.executeQuery();
            while (rs.next()) {
                double bidAmt = rs.getDouble(1);
                String course_id = rs.getString(2);
                String section_id = rs.getString(3);
                String status = rs.getString(4);
                int round = rs.getInt(5);
                Bid addThis = new Bid(stuDao.retrieveStudent(studentID), bidAmt, course_id, section_id, status, round);
                studentBids.add(addThis);
            }
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            return null;
        }
        return studentBids;
    }

    /**
     * takes in a student ID, round number, returns arraylist of Bids of the
     * student
     *
     * @param studentID - Takes in the current studentID
     * @param round - Takes in the current round
     * @return list of student bids
     */
    public ArrayList<Bid> viewStudentBids2(String studentID, int round) {
        ArrayList<Bid> studentBids = new ArrayList<Bid>();

        StudentDAO stuDao = new StudentDAO();
        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(RETRIEVE_ALL_BIDS_OF_USER2);
            ResultSet rs = null;
            stmt.setString(1, studentID);
            stmt.setInt(2, round);
            rs = stmt.executeQuery();
            while (rs.next()) {
                double bidAmt = rs.getDouble(1);
                String course_id = rs.getString(2);
                String section_id = rs.getString(3);
                String status = rs.getString(4);
                Bid addThis = new Bid(stuDao.retrieveStudent(studentID), bidAmt, course_id, section_id, status, round);
                studentBids.add(addThis);
            }
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            return null;
        }
        return studentBids;
    }

    /**
     * Retrieve list of Student Enrolled Sections
     * @param studentID takes in the current studentID
     * @return list of section object whom student is enrolled to
     */
    public ArrayList<Section> viewStudentEnrolledSections(String studentID) {
        SectionDAO sectionDAO = new SectionDAO();
        return sectionDAO.retrieveSectionsOfStudent(studentID);
    }

    /**
     * Get all the bids from the current Bidding Round
     *
     * @return an array of roundStatus for currentBidRound
     */
    public String[] getCurrentBidRound() {
        String[] toReturn = new String[2];
        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM bidding_round");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String status = rs.getString(1);
                String currentRound = rs.getString(2);

                toReturn[0] = status;
                toReturn[1] = currentRound;
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
     * Takes in a UserID and returns number of Bids of the User
     *
     * @param userID Takes in a UserID
     * @return int returns number of Bids of the User
     */
    public int countNumOfBids(String userID) {
        Connection conn1 = null;
        PreparedStatement stmt1 = null;
        ResultSet rs = null;
        int num = 0;
        try {
            conn1 = ConnectionManager.getConnection();
            stmt1 = conn1.prepareStatement("SELECT count(*) FROM `bid` WHERE user_id = ? and status <> 'Fail'");
            stmt1.setString(1, userID);
            rs = stmt1.executeQuery();
            while (rs.next()) {
                num = rs.getInt(1);
            }

            rs.close();
            stmt1.close();
            conn1.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return num;
    }

    /**
     * Checks if the student current Bids clashes with the designated bids he
     * trying to bid for
     *
     * @param userID Takes in the current userID
     * @param sectionID Takes in the current sectionID
     * @param courseID Takes in the current courseID
     * @return true if successful, false otherwise
     */
    public boolean checkIfTimeTableClash(String userID, String sectionID, String courseID) {
        SectionDAO sectionDAO = new SectionDAO();
        Section currentSect = sectionDAO.retrieve(courseID, sectionID);
        String currentSectDay = currentSect.getDay();
        String currentSectStartTime = currentSect.getStartDate();
        String currentSectEndTime = currentSect.getEndDate();

        int nextRound = 1;
        if (currentRound == 1){
            nextRound = 2;
        }
        ArrayList<Bid> currentUserBids = null;
        if (roundStatus[0].equals("started")){
            currentUserBids = viewStudentBids2(userID, currentRound);
        } else {
            currentUserBids = viewStudentBids2(userID, nextRound);
        }
        for (Bid b : currentUserBids) {
            if (b.getBiddingRound() != currentRound) {
                return false;
            }
            String toCheckCourseID = b.getCourseID();
            String toCheckSectionID = b.getSectionID();
            Section toCheck = sectionDAO.retrieve(toCheckCourseID, toCheckSectionID);
            String toCheckDay = toCheck.getDay();
            if (toCheckDay.equals(currentSectDay)) {
                String toCheckStartTime = toCheck.getStartDate();
                String toCheckEndTime = toCheck.getEndDate();
                if (toCheckStartTime.equals(currentSectStartTime) && toCheckEndTime.equals(currentSectEndTime)) {
                    return true;
                }

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    Date start = sdf.parse(currentSectStartTime);
                    Date end = sdf.parse(currentSectEndTime);
                    Date start2 = sdf.parse(toCheckStartTime);
                    Date end2 = sdf.parse(toCheckEndTime);

                    if (start.before(start2)) {
                        if (end.after(start2)) {
                            return true;
                        }
                    }
                    if (start.after(start2)) {
                        if (end.before(end2)) {
                            return true;
                        }
                        if (start.before(end2)) {
                            return true;
                        }
                    }
                    if (end.after(start2)) {
                        if (end.before(end2)) {
                            return true;
                        }
                    }
                    if (start2.before(start)) {
                        if (end2.after(start)) {
                            return true;
                        }
                    }
                    if (start2.after(start)) {
                        if (end2.before(end)) {
                            return true;
                        }
                        if (start2.before(end)) {
                            return true;
                        }
                    }
                    if (end2.after(start)) {
                        if (end2.before(end)) {
                            return true;
                        }
                    }
                    if (start.equals(start2) && end.equals(end2)) {
                        return true;
                    }
                    if (start.equals(end2)) {
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * Checks if the student current Exam timetable clashes with the designated
     * bid's exam timetable he is trying to bid for
     *
     * @param userID Takes in the current userID
     * @param courseID Takes in the current courseID
     * @return true if clashes, false otherwise
     */
    public boolean checkIfExamClash(String userID, String courseID) {
        CourseDAO courseDAO = new CourseDAO();
        Course currentCourse = courseDAO.retrieveCourse(courseID);

        Date currentCourseExamDate = currentCourse.getExamDate();
        String currentCourseExamStartTime = currentCourse.getExamStart();
        String currentCourseExamEndTime = currentCourse.getExamEnd();
        
        int nextRound = 1;
        if (currentRound == 1)
            nextRound = 2;

        ArrayList<Bid> currentUserBids = null;
        if (roundStatus[0].equals("started")){
            currentUserBids = viewStudentBids2(userID, currentRound);
        } else {
            currentUserBids = viewStudentBids2(userID, nextRound);
        }
        for (Bid b : currentUserBids) {
            if (b.getBiddingRound() != currentRound) {
                return false;
            }
            String toCheckCourseID = b.getCourseID();
            Course toCheckCourse = courseDAO.retrieveCourse(toCheckCourseID);
            Date toCheckCourseDate = toCheckCourse.getExamDate();

            if (currentCourseExamDate.compareTo(toCheckCourseDate) == 0) {
                String toCheckCourseExamStartTime = toCheckCourse.getExamStart();
                String toCheckCourseExamEndTime = toCheckCourse.getExamEnd();
                if (toCheckCourseExamStartTime.equals(currentCourseExamStartTime) && toCheckCourseExamEndTime.equals(currentCourseExamEndTime)) {
                    return true;
                }

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    Date start = sdf.parse(currentCourseExamStartTime);
                    Date end = sdf.parse(currentCourseExamEndTime);
                    Date start2 = sdf.parse(toCheckCourseExamStartTime);
                    Date end2 = sdf.parse(toCheckCourseExamEndTime);

                    if (start.before(start2)) {
                        if (end.after(start2)) {
                            return true;
                        }
                    }
                    if (start.after(start2)) {
                        if (end.before(end2)) {
                            return true;
                        }
                        if (start.before(end2)) {
                            return true;
                        }
                    }
                    if (end.after(start2)) {
                        if (end.before(end2)) {
                            return true;
                        }
                    }
                    if (start2.before(start)) {
                        if (end2.after(start)) {
                            return true;
                        }
                    }
                    if (start2.after(start)) {
                        if (end2.before(end)) {
                            return true;
                        }
                        if (start2.before(end)) {
                            return true;
                        }
                    }
                    if (end2.after(start)) {
                        if (end2.before(end)) {
                            return true;
                        }
                    }
                    if (start.equals(start2) && end.equals(end2)) {
                        return true;
                    }
                    if (start.equals(end2)) {
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * Views all the bids for this current section by a particular student
     *
     * @param courseID Takes in the current courseID
     * @param sectionID Takes in the current sectionID
     * @param round Takes in the current round
     * @return list of bids for the current section
     */
    public ArrayList<Bid> viewAllBidsForSection(String courseID, String sectionID, int round) {
        ArrayList<Bid> studentBids = new ArrayList<Bid>();

        StudentDAO stuDao = new StudentDAO();
        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(RETRIEVE_ALL_BIDS_FOR_SECTION);
            ResultSet rs = null;
            stmt.setString(1, courseID);
            stmt.setString(2, sectionID);
            stmt.setInt(3, round);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String user_id = rs.getString(1);
                String course_id = rs.getString(2);
                String section_id = rs.getString(3);
                //int currentRound = rs.getInt(4);
                String status = rs.getString(5);
                double bidAmt = rs.getDouble(6);
                Bid addThis = new Bid(stuDao.retrieveStudent(user_id), bidAmt, course_id, section_id, status, round);
                studentBids.add(addThis);
            }
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            return null;
        }
        return studentBids;
    }

    /**
     * Views all the bids for this current section by a particular student in a
     * particular round
     *
     * @param courseID Takes in the current courseID
     * @param sectionID Takes in the current sectionID
     * @param round Takes in the current round
     * @param userID Takes in the current userID
     * @return list of bid based on the current userid, courseid, sectionid and
     * round
     */
    public ArrayList<Bid> viewAllBidsForSection2(String courseID, String sectionID, int round, String userID) {
        ArrayList<Bid> studentBids = new ArrayList<Bid>();

        StudentDAO stuDao = new StudentDAO();
        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(RETRIEVE_ALL_BIDS_FOR_SECTION);
            ResultSet rs = null;
            stmt.setString(1, courseID);
            stmt.setString(2, sectionID);
            stmt.setInt(3, round);
            rs = stmt.executeQuery();
            while (rs.next()) {

                String user_id = rs.getString(1);
                String course_id = rs.getString(2);
                String section_id = rs.getString(3);
                //int currentRound = rs.getInt(4);
                String status = rs.getString(5);
                double bidAmt = rs.getDouble(6);

                if (user_id.equals(userID)) {
                    Bid addThis = new Bid(stuDao.retrieveStudent(user_id), bidAmt, course_id, section_id, status, currentRound);
                    studentBids.add(addThis);
                }
            }
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            return null;
        }
        return studentBids;
    }

    /**
     * Retrieve the position of the first bid amount
     *
     * @param bidAmount Takes in the bid amount to check for
     * firstIndexOfBidAmount
     * @param list Takes in the bid list to check
     * @return the counter that matches the bidAmount
     */
    public int getFirstIndexOfBidAmount(double bidAmount, ArrayList<Bid> list) {
        int counter = 0;
        for (Bid b : list) {
            double amt = b.getBidAmount();
            if (bidAmount == amt) {
                return counter;
            }
            counter++;
        }
        return counter;
    }

    /**
     * Retrieve the position of the last bid amount(clearing price for the bid
     * list)
     *
     * @param bidAmount Takes in a bidAmount to check for lastIndex bidAmount
     * @param list Takes in an bid arrayList
     * @return the counter that matches the bidAmount
     */
    public int getLastIndexOfBidAmount(double bidAmount, ArrayList<Bid> list) {
        int counter = list.size() - 1;
        for (int i = list.size() - 1; i >= 0; i--) {
            Bid b = list.get(i);
            double amt = b.getBidAmount();
            if (bidAmount == amt) {
                return counter;
            }
            counter--;
        }
        return counter;
    }

    /**
     * Adds all the bids that make it through the round into a seperate
     * ArrayList
     *
     * @param indexToStart Takes in the indexToStart to check
     * @param indexToEnd Takes in the indexToEnd to check
     * @param list Takes in the bid list to check
     * @return list of bids based on the startIndex and endIndex
     */
    public ArrayList<Bid> seperateList(int indexToStart, int indexToEnd, ArrayList<Bid> list) {
        ArrayList<Bid> toReturn = new ArrayList<>();
        for (int i = indexToStart; i <= indexToEnd; i++) {
            Bid bidToAdd = list.get(i);
            toReturn.add(bidToAdd);
        }
        return toReturn;
    }

    /**
     * Updates the bid status- returns true if successful, false otherwise
     *
     * @param userID Takes in the userID
     * @param courseID Takes in the courseID
     * @param sectionID Takes in the sectionID
     * @param currentRound Takes in the current Round
     * @param status Takes in the current Status
     * @return true if successful, false otherwise
     */
    public boolean updateBidStatus(String userID, String courseID, String sectionID, int currentRound, String status) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("UPDATE bid SET status = ? WHERE user_id = ? AND course_id = ? AND section_id = ? AND round = ?");
            stmt.setString(1, status);
            stmt.setString(2, userID);
            stmt.setString(3, courseID);
            stmt.setString(4, sectionID);
            stmt.setInt(5, currentRound);
            stmt.execute();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            //ConnectionManager.close(conn, stmt);
        }
        return true;
    }

    /**
     * Updates the minimum Bid amount for the course
     *
     * @param amount Takes in the current amount
     * @param courseID Takes in the current courseID
     * @param sectionID Takes in the current sectionID
     * @return true if successful, false otherwise
     */
    public boolean updateMinimumBid(double amount, String courseID, String sectionID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement(UPDATE_MIN_BID);
            stmt.setDouble(1, amount);
            stmt.setString(2, courseID);
            stmt.setString(3, sectionID);
            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            ConnectionManager.close(conn, stmt);
        }
        return true;
    }

    /**
     * Inserts the minimum bid amount for the selected course and section,
     * returns true if successful, false otherwise
     *
     * @param amount Take in the current amount
     * @param courseID Takes in the current courseID
     * @param sectionID Takes in the current sectionID
     * @return true if successful, false otherwise
     */
    public boolean insertMinimumBid(double amount, String courseID, String sectionID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement(ADD_MIN_BIDS);
            stmt.setString(1, courseID);
            stmt.setString(2, sectionID);
            stmt.setDouble(3, amount);
            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            ConnectionManager.close(conn, stmt);
        }
        return true;
    }

    /**
     * Deletes all minimum bid amount, returns true if successful, false
     * otherwise.
     *
     * @return true if successful, false otherwise
     */
    public boolean deleteAllMinimumBid() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement(DELETE_ALL_MIN_BIDS);
            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            ConnectionManager.close(conn, stmt);
        }
        return true;
    }

    /**
     * Deletes all Bids, returns true if successful, false otherwise
     *
     * @return true if successful, false otherwise
     */
    public boolean deleteAllBids() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("DELETE FROM bid");
            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            ConnectionManager.close(conn, stmt);
        }
        return true;
    }

    public boolean deleteFailedBids() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("DELETE FROM bid where status = \'Fail\'");
            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            ConnectionManager.close(conn, stmt);
        }
        return true;
    }

    /**
     * Returns arraylist of bids in one user's bidding cart
     *
     * @param user_id Takes in the current userID
     * @return a list of bid based on the current userID
     */
    public ArrayList<Bid> getBiddingCart(String user_id) {
        ArrayList<Bid> bidList = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(RETRIEVE_BIDDING_CART);) {

            stmt.setString(1, user_id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bidList.add(new Bid(rs.getString(2), rs.getString(3), "Pending"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bidList;
    }

    // ADDED FOR JSON USE - UPDATE BID
    /**
     * Checks for all the bids in the student object and whether they match the
     * specified course ID and student ID
     *
     * @param userID Takes in the userID
     * @param courseID Takes in the courseID
     * @param sectionID Takes in the sectionID
     * @return true if successful, false otherwise
     */
    public boolean checkBid(String userID, String courseID, String sectionID) {
        ArrayList<Bid> studentBids = viewStudentBids(userID);

        for (Bid b : studentBids) {
            String existingCourseInBid = b.getCourseID();
            String existingSectionInBid = b.getSectionID();

            if (existingCourseInBid.equals(courseID) && existingSectionInBid.equals(sectionID)) {
                return true;
            }
        }

        return false;
    }

    /**
     * updates the bid status for the current specified student ID
     *
     * @param userID Takes in the userID
     * @param courseID Takes in the courseID
     * @param sectionID Takes in the sectionID
     * @param currentBidAmt Takes in the current Bid Amount
     * @param newBidAmt Takes in the new Bid Amount
     */
    public void updateStudentBid(String userID, String courseID, String sectionID, double currentBidAmt, double newBidAmt) {
        if (checkBid(userID, courseID, sectionID)) {
            StudentDAO studentDAO = new StudentDAO();

            // refund edollar to Student from his previous bid
            studentDAO.refundStudent(userID, currentBidAmt);

            // update the new bid amount
            try (Connection conn = ConnectionManager.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(UPDATE_STUDENT_BID)) {
                stmt.setDouble(1, newBidAmt);
                stmt.setString(2, courseID);
                stmt.setString(3, sectionID);
                stmt.setString(4, userID);
                stmt.execute();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            // deduct from user eDollar
            studentDAO.deduct(userID, newBidAmt);
        }
    }

    /**
     * Checks if the student current Bids clashes with the designated bids he
     * trying to bid for
     *
     * @param userID Takes in the current userID
     * @param sectionID Takes in the current sectionID
     * @param courseID Takes in the current courseID
     * @return true if timetable clashes, false otherwise
     */
    public boolean checkIfTimeTableClash2(String userID, String sectionID, String courseID) {
        SectionDAO sectionDAO = new SectionDAO();
        Section currentSect = sectionDAO.retrieve(courseID, sectionID);
        String currentSectDay = currentSect.getDay();
        String currentSectStartTime = currentSect.getStartDate();
        String currentSectEndTime = currentSect.getEndDate();

        ArrayList<Bid> currentUserBids = viewStudentBids(userID);
        ArrayList<Section> currentUserSections = viewStudentEnrolledSections(userID);
        for (Bid b : currentUserBids) {
            if (b.getBiddingRound() != currentRound) {
                return false;
            }

            String toCheckCourseID = b.getCourseID();
            String toCheckSectionID = b.getSectionID();
            Section toCheck = sectionDAO.retrieve(toCheckCourseID, toCheckSectionID);
            String toCheckDay = toCheck.getDay();
            if (!b.getCourseID().equals(courseID) && toCheckDay.equals(currentSectDay)) {
                String toCheckStartTime = toCheck.getStartDate();
                String toCheckEndTime = toCheck.getEndDate();
                if (toCheckStartTime.equals(currentSectStartTime) && toCheckEndTime.equals(currentSectEndTime)) {
                    return true;
                }

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    Date start = sdf.parse(currentSectStartTime);
                    Date end = sdf.parse(currentSectEndTime);
                    Date start2 = sdf.parse(toCheckStartTime);
                    Date end2 = sdf.parse(toCheckEndTime);

                    if (start.before(start2)) {
                        if (end.after(start2)) {
                            return true;
                        }
                    }
                    if (start.after(start2)) {
                        if (end.before(end2)) {
                            return true;
                        }
                        if (start.before(end2)) {
                            return true;
                        }
                    }
                    if (end.after(start2)) {
                        if (end.before(end2)) {
                            return true;
                        }
                    }
                    if (start2.before(start)) {
                        if (end2.after(start)) {
                            return true;
                        }
                    }
                    if (start2.after(start)) {
                        if (end2.before(end)) {
                            return true;
                        }
                        if (start2.before(end)) {
                            return true;
                        }
                    }
                    if (end2.after(start)) {
                        if (end2.before(end)) {
                            return true;
                        }
                    }
                    if (start.equals(start2) && end.equals(end2)) {
                        return true;
                    }
                    if (start.equals(end2)) {
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        for (Section s : currentUserSections) {
            String toCheckCourseID = s.getCourseID();
            String toCheckSectionID = s.getSectionID();
            Section toCheck = sectionDAO.retrieve(toCheckCourseID, toCheckSectionID);
            String toCheckDay = toCheck.getDay();
            if (!s.getCourseID().equals(courseID) && toCheckDay.equals(currentSectDay)) {
                String toCheckStartTime = toCheck.getStartDate();
                String toCheckEndTime = toCheck.getEndDate();
                if (toCheckStartTime.equals(currentSectStartTime) && toCheckEndTime.equals(currentSectEndTime)) {
                    return true;
                }

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    Date start = sdf.parse(currentSectStartTime);
                    Date end = sdf.parse(currentSectEndTime);
                    Date start2 = sdf.parse(toCheckStartTime);
                    Date end2 = sdf.parse(toCheckEndTime);

                    if (start.before(start2)) {
                        if (end.after(start2)) {
                            return true;
                        }
                    }
                    if (start.after(start2)) {
                        if (end.before(end2)) {
                            return true;
                        }
                        if (start.before(end2)) {
                            return true;
                        }
                    }
                    if (end.after(start2)) {
                        if (end.before(end2)) {
                            return true;
                        }
                    }
                    if (start2.before(start)) {
                        if (end2.after(start)) {
                            return true;
                        }
                    }
                    if (start2.after(start)) {
                        if (end2.before(end)) {
                            return true;
                        }
                        if (start2.before(end)) {
                            return true;
                        }
                    }
                    if (end2.after(start)) {
                        if (end2.before(end)) {
                            return true;
                        }
                    }
                    if (start.equals(start2) && end.equals(end2)) {
                        return true;
                    }
                    if (start.equals(end2)) {
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * Checks if the student current Exam timetable clashes with the designated
     * bid's exam timetable hes trying to bid for
     *
     * @param userID Takes in the current userID
     * @param courseID Takes in the current courseID
     * @return true if timetable clashes, false otherwise
     */
    public boolean checkIfExamClash2(String userID, String courseID) {
        CourseDAO courseDAO = new CourseDAO();
        Course currentCourse = courseDAO.retrieveCourse(courseID);

        Date currentCourseExamDate = currentCourse.getExamDate();
        String currentCourseExamStartTime = currentCourse.getExamStart();
        String currentCourseExamEndTime = currentCourse.getExamEnd();
        ArrayList<Bid> currentUserBids = viewStudentBids(userID);
        ArrayList<Section> currentUserSections = viewStudentEnrolledSections(userID);
        for (Bid b : currentUserBids) {
            if (b.getBiddingRound() != currentRound) {
                return false;
            }
            String toCheckCourseID = b.getCourseID();
            Course toCheckCourse = courseDAO.retrieveCourse(toCheckCourseID);
            Date toCheckCourseDate = toCheckCourse.getExamDate();

            if (b.getCourseID().equals(courseID)) {
                return false;
            }

            if (currentCourseExamDate.compareTo(toCheckCourseDate) == 0) {
                String toCheckCourseExamStartTime = toCheckCourse.getExamStart();
                String toCheckCourseExamEndTime = toCheckCourse.getExamEnd();
                if (toCheckCourseExamStartTime.equals(currentCourseExamStartTime) && toCheckCourseExamEndTime.equals(currentCourseExamEndTime)) {
                    return true;
                }

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    Date start = sdf.parse(currentCourseExamStartTime);
                    Date end = sdf.parse(currentCourseExamEndTime);
                    Date start2 = sdf.parse(toCheckCourseExamStartTime);
                    Date end2 = sdf.parse(toCheckCourseExamEndTime);

                    if (start.before(start2)) {
                        if (end.after(start2)) {
                            return true;
                        }
                    }
                    if (start.after(start2)) {
                        if (end.before(end2)) {
                            return true;
                        }
                        if (start.before(end2)) {
                            return true;
                        }
                    }
                    if (end.after(start2)) {
                        if (end.before(end2)) {
                            return true;
                        }
                    }
                    if (start2.before(start)) {
                        if (end2.after(start)) {
                            return true;
                        }
                    }
                    if (start2.after(start)) {
                        if (end2.before(end)) {
                            return true;
                        }
                        if (start2.before(end)) {
                            return true;
                        }
                    }
                    if (end2.after(start)) {
                        if (end2.before(end)) {
                            return true;
                        }
                    }
                    if (start.equals(start2) && end.equals(end2)) {
                        return true;
                    }
                    if (start.equals(end2)) {
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        for (Section s : currentUserSections) {
            String toCheckCourseID = s.getCourseID();
            Course toCheckCourse = courseDAO.retrieveCourse(toCheckCourseID);
            Date toCheckCourseDate = toCheckCourse.getExamDate();

            if (s.getCourseID().equals(courseID)) {
                return false;
            }

            if (currentCourseExamDate.compareTo(toCheckCourseDate) == 0) {
                String toCheckCourseExamStartTime = toCheckCourse.getExamStart();
                String toCheckCourseExamEndTime = toCheckCourse.getExamEnd();
                if (toCheckCourseExamStartTime.equals(currentCourseExamStartTime) && toCheckCourseExamEndTime.equals(currentCourseExamEndTime)) {
                    return true;
                }

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    Date start = sdf.parse(currentCourseExamStartTime);
                    Date end = sdf.parse(currentCourseExamEndTime);
                    Date start2 = sdf.parse(toCheckCourseExamStartTime);
                    Date end2 = sdf.parse(toCheckCourseExamEndTime);

                    if (start.before(start2)) {
                        if (end.after(start2)) {
                            return true;
                        }
                    }
                    if (start.after(start2)) {
                        if (end.before(end2)) {
                            return true;
                        }
                        if (start.before(end2)) {
                            return true;
                        }
                    }
                    if (end.after(start2)) {
                        if (end.before(end2)) {
                            return true;
                        }
                    }
                    if (start2.before(start)) {
                        if (end2.after(start)) {
                            return true;
                        }
                    }
                    if (start2.after(start)) {
                        if (end2.before(end)) {
                            return true;
                        }
                        if (start2.before(end)) {
                            return true;
                        }
                    }
                    if (end2.after(start)) {
                        if (end2.before(end)) {
                            return true;
                        }
                    }
                    if (start.equals(start2) && end.equals(end2)) {
                        return true;
                    }
                    if (start.equals(end2)) {
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * retrieve Bid amount of specific user for a given courseID and section ID.
     *
     * @param userID Takes in the current userID
     * @param courseID Takes in the current courseID
     * @param sectionID Takes in the current sectionID
     * @return the bid Amount of the user
     */
    public double retrieveBidAmountOfUser(String userID, String courseID, String sectionID) {
        double bidEDollar = 0;

        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(RETRIEVE_BID_EDOLLAR)) {

            stmt.setString(1, userID);
            stmt.setString(2, courseID);
            stmt.setString(3, sectionID);

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                bidEDollar = rs.getDouble(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bidEDollar;
    }

    /**
     * retrieve ArrayList of all successfulBids given the courseID and sectionID
     *
     * @param courseID Takes in the current courseID
     * @param sectionID Takes in the current sectionID
     * @return list of successful bids
     */
    public ArrayList<Bid> retrieveNumOfSuccessfulBids(String courseID, String sectionID) {
        ArrayList<Bid> toReturn = new ArrayList<Bid>();
        StudentDAO studentDAO = new StudentDAO();

        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(RETRIEVE_SUCCESSFUL_BIDS)) {

            stmt.setString(1, courseID);
            stmt.setString(2, sectionID);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String userid = rs.getString(1);
                    String courseid = rs.getString(2);
                    String sectionid = rs.getString(3);
                    double bidAmount = rs.getDouble(4);
                    Student student = studentDAO.retrieveStudent(userid);
                    toReturn.add(new Bid(student, bidAmount, courseid, sectionid, "Success", currentRound));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    /**
     * retrieve Arraylist of Bids for round 1
     *
     * @return list of bids for round 1
     */
    public ArrayList<Bid> retrieveRound1Bids() {
        StudentDAO studentDAO = new StudentDAO();
        ArrayList<Bid> round1Bids = new ArrayList<>();

        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECTED_BID_ROUND1)) {
            String[] bidStatus = getCurrentBidRound();
            int round = Integer.parseInt(bidStatus[1]);
            stmt.setInt(1, round);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String user_id = rs.getString(1);
                    Student student = studentDAO.retrieveStudent(user_id);
                    String courseID = rs.getString(2);
                    String sectionID = rs.getString(3);
                    String status = rs.getString(4);
                    double bidAmount = rs.getDouble(6);
                    round1Bids.add(new Bid(student, bidAmount, courseID, sectionID, status, currentRound));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return round1Bids;
    }

    /**
     * retrieve ArrayList of Bids with Section and courses from Bidlist
     *
     * @return list of bids with section course
     */
    public ArrayList<Bid> retrieveCourseSectionFromBids() {
        ArrayList<Bid> sectionFromBids = new ArrayList<>();

        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(RETRIEVE_SECTION_FROM_BID)) {
            String[] bidStatus = getCurrentBidRound();
            int round = Integer.parseInt(bidStatus[1]);
            stmt.setInt(1, round);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sectionFromBids.add(new Bid(rs.getString(1), rs.getString(2), "Pending"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sectionFromBids;
    }

    /**
     * retrieve all successful bids from round 2
     *
     * @return all successful bids from round 2
     */
    public ArrayList<Bid> retrieveAllSuccessfulBidsinRound2() {
        ArrayList<Bid> bids = new ArrayList<Bid>();

        StudentDAO stuDao = new StudentDAO();
        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(RETRIEVE_SUCCESSFUL_BIDS_FROM_BID_IN_ROUND2);
            ResultSet rs = null;
            rs = stmt.executeQuery();
            while (rs.next()) {
                String user_id = rs.getString(1);
                String course_id = rs.getString(2);
                String section_id = rs.getString(3);
                int currentRound = rs.getInt(4);
                String status = rs.getString(5);
                double bidAmt = rs.getDouble(6);
                Bid addThis = new Bid(stuDao.retrieveStudent(user_id), bidAmt, course_id, section_id, status, currentRound);
                bids.add(addThis);
            }
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            return null;
        }
        return bids;
    }
}
