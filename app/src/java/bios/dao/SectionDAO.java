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
public class SectionDAO {

    private ArrayList<Section> allSections;
    private static final String RETRIEVE_ALL = "select * from section order by course_id, section_id";
    private static final String RETRIEVE_ALL_BY_SCHOOL = "select * from section s inner join course c on s.course_id = c.course_id where c.school = ? order by s.course_id, s.section_id";
    private static final String RETRIEVE_NUM_FILLED_SECTION = "select count(*) from section_student where course_id=? and section_id=?";
    private static final String RETRIEVE_SECTION = "select * from section where section_id=? and course_id=?";
    private static final String RETRIEVE_STUDENTS = "select * from section_student order by course_id, user_id";

    /* ADDED FOR JSON UPDATE_BID */
    private static final String RETRIEVE_COURSE_ENROLLED = "select course_id, section_id from section_student where user_id = ?";
    private static final String RETRIEVE_SECTION_VACANCY_SIZE = "select size from section where course_id = ? and section_id = ?";

    /* ADDED FOR JSON DUMP SECTION */
    private static final String RETRIEVE_STUDENT_ID_BID_AMOUNT = "select user_id, bidAmount from section_student where course_id = ? and section_id = ? order by user_id";

    /**
     *Creates a SectionDAO() object with empty arrayList containing Section objects
     */
    public SectionDAO() {
        this.allSections = new ArrayList<Section>();
        //add sections to this array
    }

    /**
     * Retrieve all successful bids as Strings from section student table 
     * @return all the successfully enrolled student from the database
     */
    public ArrayList<String[]> retrieveAllSectionStudent() {
        ArrayList<String[]> stuSections = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement(RETRIEVE_STUDENTS);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String userid = rs.getString(1);
                String courseid = rs.getString(2);
                String sectionid = rs.getString(3);
                String eDollars = Double.toString(rs.getDouble(4));
                stuSections.add(new String[]{userid, courseid, sectionid, eDollars});
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stuSections;
    }

    /**
     * Drop a specified section from a student object
     * @param user_id Takes in an user_id
     * @param course_id Takes in a course_id
     * @param section_id Takes in a section_id
     * @return true if successful, false otherwise
     */
    public boolean dropSection(String user_id, String course_id, String section_id) {
        ArrayList<Section> stuSections = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("DELETE FROM `is203`.`section_student` WHERE `section_student`.`User_id` = ? AND `section_student`.`Course_id` = ? AND `section_student`.`Section_id` = ?");
            stmt.setString(1, user_id);
            stmt.setString(2, course_id);
            stmt.setString(3, section_id);
            stmt.execute();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    /**
     * retrieves All sections of a Student object
     * @param user_id Takes in an user_id of String type
     * @return the list of section based on the userID
     */
    public ArrayList<Section> retrieveSectionsOfStudent(String user_id) {
        ArrayList<Section> stuSections = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("select course_id, section_id from section_student where user_id = ?");
            stmt.setString(1, user_id);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String course_id = rs.getString(1);
                String section_id = rs.getString(2);
                Section addSec = retrieve(course_id, section_id);
                stuSections.add(addSec);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stuSections;
    }

    /**
     * Refunds student bid amount to the specified student 
     * @param user_id Takes in an user_id
     * @param course_id Takes in a course_id
     * @param section_id Takes in section_id
     * @return double bid amount of the student for the specified student id, course_id and section_id
     */
    public double refundStudentBidAmount(String user_id, String course_id, String section_id) {
        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select course_id, section_id, bidAmount from section_student where user_id = ?");
            stmt.setString(1, user_id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String courseID = rs.getString(1);
                String sectionID = rs.getString(2);
                double bidAmt = rs.getDouble(3);

                if (courseID.equals(course_id)) {
                    if (section_id.equals(sectionID)) {
                        return bidAmt;
                    }
                }
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Retrieve ArrayList of All sections in the sections table
     * @return a list of sections from the database
     */
    public ArrayList<Section> retrieveAll() {
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(RETRIEVE_ALL);
                ResultSet rs = stmt.executeQuery();) {

            while (rs.next()) {
                String section_id = rs.getString(1);
                String day = rs.getString(2);
                String startTime = rs.getString(3);
                String endTime = rs.getString(4);
                String instructor = rs.getString(5);
                String venue = rs.getString(6);
                int size = rs.getInt(7);
                String course_id = rs.getString(8);

                allSections.add(new Section(section_id, course_id, day, startTime, endTime, instructor, venue, size));
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allSections;
    }

    /**
     * Retrieve ArrayList of all Sections specified by a school
     * @param school Takes in a school of String data type
     * @return a Section ArrayList based on the school
     */
    public ArrayList<Section> retrieveAllBySchool(String school) {
        ArrayList<Section> toReturn = new ArrayList<>();
        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(RETRIEVE_ALL_BY_SCHOOL);
            stmt.setString(1, school);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String section_id = rs.getString(1);
                String day = rs.getString(2);
                String startTime = rs.getString(3);
                String endTime = rs.getString(4);
                String instructor = rs.getString(5);
                String venue = rs.getString(6);
                int size = rs.getInt(7);
                String course_id = rs.getString(8);

                toReturn.add(new Section(section_id, course_id, day, startTime, endTime, instructor, venue, size));
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
     * retrieve number of successful bid of a Sections of a specified course.
     * @param course_id Takes in a course_id of String data type
     * @param section_id Takes in a section_id of String data type
     * @return int number of filled slots of a specified section of a specified course 
     */
    public int retrieveNumOfFilledSections(String course_id, String section_id) {
        int count = 0;
        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(RETRIEVE_NUM_FILLED_SECTION);
            stmt.setString(1, course_id);
            stmt.setString(2, section_id);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Retrieve a specified section given courseID and sectionID
     * @param courseID Takes in a courseID of String type
     * @param sectionID Takes in a sectionID of String type
     * @return Section object
     */
    public Section retrieve(String courseID, String sectionID) {
        Section toReturn = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement(RETRIEVE_SECTION);
            stmt.setString(1, sectionID);
            stmt.setString(2, courseID);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String sid = rs.getString(1);
                String day = rs.getString(2);
                String startTime = rs.getString(3);
                String endTime = rs.getString(4);
                String instructor = rs.getString(5);
                String venue = rs.getString(6);
                int size = rs.getInt(7);
                String cid = rs.getString(8);

                toReturn = new Section(sid, cid, day, startTime, endTime, instructor, venue, size);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return toReturn;
    }

    /**
     * Adds a student object to a specified section 
     * @param student Takes in a Student object
     * @param section Takes in a Section object
     * @return true if successful, false otherwise
     */
    public boolean addStudent(Student student, Section section) {
        for (Section s : allSections) {
            if (s.equals(section)) {
                //check if there is capacity
                if (s.checkVacancy() > 0) {
                    s.getStudentList().add(student);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Removes a student from a specified section
     * @param student Takes in a Student object
     * @param section Takes in a Section object
     * @return true if successful, false if otherwise
     */
    public boolean removeStudent(Student student, Section section) {
        for (Section s : allSections) {
            if (s.equals(section)) {
                for (int i = 0; i < s.getStudentList().size(); i++) {
                    if (s.getStudentList().get(i).equals(student)) {
                        s.getStudentList().remove(i);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Inserts a specified user into a specified course and  specific section
     * @param userID Takes in an userID of String type
     * @param courseID Takes in a courseID of String type
     * @param sectionID Takes in a sectionID of String type
     * @param bidAmount Takes in a bidAmount of double type
     * @return true if successful, false if otherwise
     */
    public boolean insertSectionStudent(String userID, String courseID, String sectionID, double bidAmount) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("INSERT INTO section_student (User_id, Course_id, Section_id, BidAmount) VALUES (?, ?, ?, ?)");
            stmt.setString(1, userID);
            stmt.setString(2, courseID);
            stmt.setString(3, sectionID);
            stmt.setDouble(4, bidAmount);
            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            ConnectionManager.close(conn, stmt);
        }
        return true;
    }

    // ADDED FOR JSON USE - UPDATE-BID

    /**
     * Refunds student of a specific course and specified section of their bid amount
     * @param user_id Takes in an user_id of String type
     * @param course_id Takes in a course_id of String type
     * @param section_id Takes in a section_id of String type
     * @return BidAMout to be refunded
     */
    public double refundStudentBidAmount2(String user_id, String course_id, String section_id) {

        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select course_id, section_id, bidAmount from bid where user_id = ?");
            stmt.setString(1, user_id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String courseID = rs.getString(1);
                String sectionID = rs.getString(2);
                double bidAmt = rs.getDouble(3);

                if (courseID.equals(course_id)) {
                    return bidAmt;

                }
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Checks if student is enrolled within the specified course and section
     * @param userID Takes in a userID of String type
     * @param courseID Takes in a courseID of String type
     * @param sectionID Takes in a sectionID of String type
     * @return true if course if enrolled, false otherwise
     */
    public boolean checkCourseEnrolled(String userID, String courseID, String sectionID) {
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(RETRIEVE_COURSE_ENROLLED)) {
            stmt.setString(1, userID);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String courseCodeRs = rs.getString(1);
                    String sectionIDRs = rs.getString(2);

                    if (courseID.equals(courseCodeRs) && sectionID.equals(sectionIDRs)) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     * Retrieve number of vacancy within a course and specified section
     * @param courseID Takes in a courseID of String type
     * @param sectionID Takes in a sectionID of String type
     * @return the vacancy from the database
     */
    public int retrieveSectionVacancySize(String courseID, String sectionID) {
        int vacancyCount = 0;
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(RETRIEVE_SECTION_VACANCY_SIZE)) {
            stmt.setString(1, courseID);
            stmt.setString(2, sectionID);

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                vacancyCount = rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return vacancyCount;
    }

    // ADDED FOR JSON USE - DUMP SECTION

    /**
     * Retrieves arrayList of Sections of a specified course and section
     * @param courseID Takes in a courseID of String type
     * @param sectionID Takes in a sectionID of String type
     * @return list of Section Arraylist of student enrolled courses
     */
    public ArrayList<Section> retrieveEnrolledCourse(String courseID, String sectionID) {
        ArrayList<Section> sectionList = new ArrayList<>();

        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(RETRIEVE_STUDENT_ID_BID_AMOUNT)) {
            stmt.setString(1, courseID);
            stmt.setString(2, sectionID);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String userID = rs.getString(1);
                    double bid_amount = rs.getDouble(2);

                    sectionList.add(new Section(userID, bid_amount));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return sectionList;
    }

    /**
     * retrieve bid amount of student of a specified course and section
     * @param courseID Takes in a courseID of String type
     * @param sectionID Takes in a sectionID of String type
     * @return the bid amount of the student
     */
    public double retrieveBidAmt(String courseID, String sectionID) {
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(RETRIEVE_STUDENT_ID_BID_AMOUNT)) {
            stmt.setString(1, courseID);
            stmt.setString(2, sectionID);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    double bid_amount = rs.getDouble(2);

                    return bid_amount;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }
}
