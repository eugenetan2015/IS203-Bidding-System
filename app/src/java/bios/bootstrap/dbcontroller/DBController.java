/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bios.bootstrap.dbcontroller;

import bios.dao.ConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

/**
 *
 * @author Tan Ming Kwang
 */
public class DBController {

    private static Connection conn;
    private static final String INSERT_STUDENT = "INSERT INTO `is203`.`student` (`User_id`, `Password`, `Name`, `School`, `eDollar`, `Type`) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String INSERT_COURSE = "INSERT INTO `is203`.`course` (`Course_id`, `School`, `Title`, `Description`, `Exam_Date`, `Exam_Start`, `Exam_End`) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String INSERT_SECTION = "INSERT INTO `is203`.`section` (`Section_id`, `Day`, `StartTime`, `EndTime`, `Instructor`, `Venue`, `Size`, `Course_id`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String INSERT_PREREQUISITE = "INSERT INTO `is203`.`prerequisite` (`Course_id`, `Prerequisite_id`) VALUES (?, ?)";
    private static final String INSERT_COURSE_COMPLETED = "INSERT INTO `is203`.`course_completed` (`User_id`, `Course_id`) VALUES (?, ?)";
    private static final String INSERT_BID = "INSERT INTO `is203`.`bid` (`User_id`, `Course_id`, `Section_id`, `Round`, `Status`, `BidAmount`) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String INSERT_BIDDING_ROUND = "INSERT INTO `bidding_round`(`status`, `current_round`) VALUES (?, ?)";
    private static final String DELETE_ALL_STUDENTS = "DELETE FROM student";
    private static final String DELETE_ALL_BIDS = "DELETE FROM bid";
    private static final String DELETE_ALL_BIDS_IN_CART = "DELETE fROM bidding_cart";
    private static final String DELETE_ALL_COURSE_COMPLETED = "DELETE FROM course_completed";
    private static final String DELETE_ALL_PREREQUISITE = "DELETE FROM prerequisite";
    private static final String DELETE_ALL_SECTION_STUDENT = "DELETE FROM section_student";
    private static final String DELETE_ALL_SECTION = "DELETE FROM section";
    private static final String DELETE_ALL_COURSE = "DELETE FROM course";
    private static final String DELETE_ALL_BIDDING_ROUND = "DELETE FROM bidding_round";
    private static final String UPDATE_ROUND = "UPDATE bidding_round SET current_round = ? , status = ?";
    private static final int BATCH_SIZE = 1000;
    private static final String DELETE_ALL_MIN = "DELETE FROM minimum_bid";
    private static final String DELETE_STUDENT_TIMETABLE = "DELETE FROM student_timetable";

    /**
     * Invoke all the drop and insert methods
     */
    public static void initialize() {
        dropBiddingCartTable();
        dropBidTable();
        dropCourseCompletedTable();
        dropPrerequisiteTable();
        dropSectionStudentTable();
        dropSectionTable();
        dropCourseTable();
        dropStudentTable();
        dropBiddingRoundTable();
        dropMinBidTable();
        dropStudentTimeTable();
        insertAdmin();
        insertBiddingRound();
    }

    /**
     * Drop the minimum_bid table from the database
     *
     * @return true when min bid table have been dropped from database, false
     * otherwise.
     */
    public static boolean dropMinBidTable() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement(DELETE_ALL_MIN);
            stmt.execute();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Drop the Student Timetable from the database
     *
     * @return true when student timetable table have been dropped from database, false
     * otherwise.
     */
    public static boolean dropStudentTimeTable() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement(DELETE_STUDENT_TIMETABLE);
            stmt.execute();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Insert Admin account into database
     *
     * @return true when admin have been inserted successfully into database,
     * false otherwise.
     */
    public static boolean insertAdmin() {
        String userId = "admin";
        String password = "password";
        String name = "Administrator";
        String school = "SMU";
        try {
            conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(INSERT_STUDENT);
            stmt.setString(1, userId);
            stmt.setString(2, password);
            stmt.setString(3, name);
            stmt.setString(4, school);
            stmt.setString(5, null);
            stmt.setString(6, "admin");
            stmt.execute();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * Insert a list of Students into database
     *
     * @param entry List of Student to be added to database
     * @return true when the student records have been successfully entered into
     * database, false otherwise.
     */
    public static boolean insertStudent(ArrayList<String[]> entry) {
        PreparedStatement stmt = null;
        try {
            Connection conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement(INSERT_STUDENT);
            int counter = 0;
            for (String[] eachEntry : entry) {
                String userId = eachEntry[0];
                String password = eachEntry[1];
                String name = eachEntry[2];
                String school = eachEntry[3];
                String eDollars = eachEntry[4];

                //conn.setAutoCommit(false);
                stmt.setString(1, userId);
                stmt.setString(2, password);
                stmt.setString(3, name);
                stmt.setString(4, school);
                stmt.setString(5, eDollars);
                stmt.setString(6, "student");
                stmt.addBatch();
                if (++counter % BATCH_SIZE == 0) {
                    stmt.executeBatch();
                }
            }

            stmt.executeBatch();

        } catch (Exception e) {
            return false;
        } finally {
            if (conn != null) {
                ConnectionManager.close(conn, stmt);
            }
        }
        return true;
    }

    /**
     * Insert a list of Courses into database
     *
     * @param entry List of Course to be added to database
     * @return true when the course records have been successfully entered into
     * database, false otherwise
     */
    public static boolean insertCourse(ArrayList<String[]> entry) {
        PreparedStatement stmt = null;
        try {
            int counter = 0;
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement(INSERT_COURSE);
            for (String[] eachEntry : entry) {
                String course = eachEntry[0];
                String school = eachEntry[1];
                String title = eachEntry[2];
                String description = eachEntry[3];
                String examDate = eachEntry[4];
                String examStart = eachEntry[5];
                String examEnd = eachEntry[6];

                //conn.setAutoCommit(false);
                stmt.setString(1, course);
                stmt.setString(2, school);
                stmt.setString(3, title);
                stmt.setString(4, description);
                stmt.setString(5, examDate);
                stmt.setString(6, examStart);
                stmt.setString(7, examEnd);
                stmt.addBatch();
                if (++counter % BATCH_SIZE == 0) {
                    stmt.executeBatch();
                }
            }
            stmt.executeBatch();
            //conn.commit();
        } catch (Exception e) {
            return false;
        } finally {
            if (conn != null) {
                ConnectionManager.close(conn, stmt);
            }
        }
        return true;
    }

    /**
     * Insert a list of Sections into database
     *
     * @param entry List of Sections to be added to database
     * @return true when the section records have been successfully entered into
     * database, false otherwise.
     */
    public static boolean insertSection(ArrayList<String[]> entry) {
        PreparedStatement stmt = null;
        try {
            int counter = 0;
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement(INSERT_SECTION);
            for (String[] eachEntry : entry) {
                String course = eachEntry[0];
                String section = eachEntry[1];
                String dayNum = eachEntry[2];
                String start = eachEntry[3];
                String end = eachEntry[4];
                String instructor = eachEntry[5];
                String venue = eachEntry[6];
                String stringSize = eachEntry[7];
                int size = Integer.parseInt(stringSize);
                String day = convertDayNumToString(dayNum);

                //conn.setAutoCommit(false);
                stmt.setString(1, section);
                stmt.setString(2, day);
                stmt.setString(3, start);
                stmt.setString(4, end);
                stmt.setString(5, instructor);
                stmt.setString(6, venue);
                stmt.setInt(7, size);
                stmt.setString(8, course);
                stmt.addBatch();
                if (++counter % BATCH_SIZE == 0) {
                    stmt.executeBatch();
                }
            }
            stmt.executeBatch();
            //conn.commit();
        } catch (Exception e) {
            return false;
        } finally {
            if (conn != null) {
                ConnectionManager.close(conn, stmt);
            }
        }
        return true;
    }

    /**
     * Insert a list of Prerequisite into database
     *
     * @param entry List of prerequisite records to be added to database
     * @return true when the prerequisite records have been successfully entered
     * into database, , false otherwise.
     */
    public static boolean insertPrerequisite(ArrayList<String[]> entry) {
        PreparedStatement stmt = null;
        try {
            int counter = 0;
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement(INSERT_PREREQUISITE);
            for (String[] eachEntry : entry) {
                String courseId = eachEntry[0];
                String prerequisiteId = eachEntry[1];

                //conn.setAutoCommit(false);
                stmt.setString(1, courseId);
                stmt.setString(2, prerequisiteId);
                stmt.addBatch();
                if (++counter % BATCH_SIZE == 0) {
                    stmt.executeBatch();
                }
            }
            stmt.executeBatch();
            //conn.commit();
        } catch (Exception e) {
            return false;
        } finally {
            if (conn != null) {
                ConnectionManager.close(conn, stmt);
            }
        }
        return true;
    }

    /**
     * A method that insert a list of Course Completed into database
     *
     * @param entry List of course_completed Records to be added to database
     * @return true when the course_completed records have been successfully
     * entered into database, false otherwise.
     */
    public static boolean insertCourseCompleted(ArrayList<String[]> entry) {
        PreparedStatement stmt = null;
        try {
            int counter = 0;
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement(INSERT_COURSE_COMPLETED);
            for (String[] eachEntry : entry) {
                String userid = eachEntry[0];
                String courseid = eachEntry[1];

                //conn.setAutoCommit(false);
                stmt.setString(1, userid);
                stmt.setString(2, courseid);
                stmt.addBatch();
                if (++counter % BATCH_SIZE == 0) {
                    stmt.executeBatch();
                }
            }
            stmt.executeBatch();
            //conn.commit();
        } catch (Exception e) {
            return false;
        } finally {
            if (conn != null) {
                ConnectionManager.close(conn, stmt);
            }
        }
        return true;
    }

    /**
     * Insert a list of Bids into database
     *
     * @param entry List of bid Records to be added to database
     * @param currentRound The current bidding round
     * @return true when the bid records have been successfully entered into
     * database, false otherwise.
     */
    public static boolean insertBid(ArrayList<String[]> entry, int currentRound) {
        PreparedStatement stmt = null;
        try {
            int counter = 0;
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement(INSERT_BID);
            for (String[] eachEntry : entry) {
                String userid = eachEntry[0];
                String bidAmount = eachEntry[1];
                String courseid = eachEntry[2];
                String sectionid = eachEntry[3];

                //conn.setAutoCommit(false);
                stmt.setString(1, userid);
                stmt.setString(2, courseid);
                stmt.setString(3, sectionid);
                stmt.setInt(4, currentRound);
                stmt.setString(5, "Pending");
                stmt.setString(6, bidAmount);
                stmt.addBatch();
                if (++counter % BATCH_SIZE == 0) {
                    stmt.executeBatch();
                }
            }
            stmt.executeBatch();
            conn.commit();
        } catch (Exception e) {
            return false;
        } finally {
            if (conn != null) {
                ConnectionManager.close(conn, stmt);
            }
        }
        return true;
    }

    /**
     * Insert Bidding Round records into database
     *
     * @return true when the biddingRound records have been successfully entered
     * into database, false otherwise.
     */
    public static boolean insertBiddingRound() {
        try {
            conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(INSERT_BIDDING_ROUND);
            stmt.setString(1, "started");
            stmt.setInt(2, 1);
            stmt.execute();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Drop the student table from the database
     *
     * @return true when the student records have been successfully dropped from
     * database, false otherwise.
     */
    public static boolean dropStudentTable() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement(DELETE_ALL_STUDENTS);
            stmt.execute();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Drop the bid table from the database
     *
     * @return true when the bid records have been successfully dropped from the
     * database, false otherwise.
     */
    public static boolean dropBidTable() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement(DELETE_ALL_BIDS);
            stmt.execute();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Drop the bidding_cart table from the database
     *
     * @return true when the bid records have been successfully dropped from the
     * database, false otherwise.
     */
    public static boolean dropBiddingCartTable() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement(DELETE_ALL_BIDS_IN_CART);
            stmt.execute();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Drop the course_completed table from the database
     *
     * @return true when the course_completed records have been successfully
     * dropped from database, false otherwise.
     */
    public static boolean dropCourseCompletedTable() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement(DELETE_ALL_COURSE_COMPLETED);
            stmt.execute();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Drop the prerequisite table from the database
     *
     * @return true when the prerequisite records have been successfully dropped
     * from the database, false otherwise.
     */
    public static boolean dropPrerequisiteTable() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement(DELETE_ALL_PREREQUISITE);
            stmt.execute();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Drop the section_student table from the database
     *
     * @return true when the section_student records have been successfully
     * dropped from the database, false otherwise.
     */
    public static boolean dropSectionStudentTable() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement(DELETE_ALL_SECTION_STUDENT);
            stmt.execute();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Drop the section table from the database
     *
     * @return true when the section records have been successfully dropped from
     * the database, false otherwise.
     */
    public static boolean dropSectionTable() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement(DELETE_ALL_SECTION);
            stmt.execute();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Drop the course table from the database
     *
     * @return true when the course records have been successfully dropped from
     * database, false otherwise.
     */
    public static boolean dropCourseTable() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement(DELETE_ALL_COURSE);
            stmt.execute();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Drop the bidding_round table in the database
     *
     * @return true when the bidding round records have been successfully
     * dropped from database, false otherwise.
     */
    public static boolean dropBiddingRoundTable() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement(DELETE_ALL_BIDDING_ROUND);
            stmt.execute();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Convert the day in numeric Format to dayOfWeek
     *
     * @param dayNum The day in numeric format (1 - Monday, 2 - Tuesday, etc)
     * @return dayOfWeek if it matches the dayNum
     */
    public static String convertDayNumToString(String dayNum) {
        if (dayNum.equals("1")) {
            return "Monday";
        } else if (dayNum.equals("2")) {
            return "Tuesday";
        } else if (dayNum.equals("3")) {
            return "Wednesday";
        } else if (dayNum.equals("4")) {
            return "Thursday";
        } else if (dayNum.equals("5")) {
            return "Friday";
        } else if (dayNum.equals("6")) {
            return "Saturday";
        } else {
            return "Sunday";
        }
    }

    /**
     * Update the round status in database
     *
     * @param round The current bidding round
     * @param status The current bidding status
     * @return true when the round status have been updated in the database,
     * false otherwise.
     */
    public static boolean updateRoundStatus(int round, String status) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement(UPDATE_ROUND);
            stmt.setInt(1, round);
            stmt.setString(2, status);
            stmt.execute();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
