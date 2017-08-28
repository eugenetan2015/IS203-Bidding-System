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
import bios.dao.ConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import bios.dao.*;
import java.text.SimpleDateFormat;

/**
 *
 * @author Admin
 */
public class ShoppingCart {

    private static final String ADD_TO_BIDDING_CART = "INSERT INTO `is203`.`bidding_cart` (`User_id`, `Course_id`, `Section_id`) VALUES (?, ?, ?);";
    private static final String RETRIEVE_SHOPPING_CART_ITEMS = "select c.course_id, c.title, c.school, s.section_id, s.instructor, s.day, s.startTime, s.endTime, s.venue, s.size from section s inner join course c on s.course_id = s.course_id inner join bidding_cart bc on c.course_id = bc.course_id and s.section_id = bc.section_id where c.course_id = s.course_id and bc.user_id = ?";
    private static final String DELETE_ITEM_FROM_SHOPPING_CART = "DELETE FROM `is203`.`bidding_cart` WHERE `bidding_cart`.`User_id` = ? AND `bidding_cart`.`Course_id` = ? AND `bidding_cart`.`Section_id` = ?";
    private static final String CHECK_NUM_IN_BIDDING_CART = "SELECT count(*) FROM `bidding_cart` WHERE user_id = ?";
    private static final String CHECK_COURSE_IN_CART = "select * from bidding_cart where course_id = ? and user_id = ?";
    private static final String CLEAR_CART_BY_USER = "DELETE FROM `is203`.`bidding_cart` WHERE `bidding_cart`.`User_id` = ?";
    private static final String CLEAR_CART = "DELETE FROM bidding_cart";
    private static final String CHECK_COURSE_COMPLETED = "SELECT * FROM `course_completed` where user_id = ? and course_id = ?";
    private static final String CHECK_PREREQ = "SELECT * FROM `course_completed` cc inner join prerequisite pr on cc.course_id = pr.prerequisite_id where user_id = ? and pr.course_id = ?";
    private static final String CHECK_NO_PREREQ = "SELECT * FROM course c inner join prerequisite pr on c.course_id = pr.course_id where c.course_id = ?";
    private ArrayList<ShoppingCartItem> shoppingCartItems;

    /**
     * Specific Constructor
     */
    public ShoppingCart() {
        this.shoppingCartItems = new ArrayList<>();
    }

    /**
     * Get all the ShoppingCartItem in the ShoppingCart
     * @return the shoppingCartItem
     */
    public ArrayList<ShoppingCartItem> getShoppingCartItems() {
        return shoppingCartItems;
    }

    /**
     * Check the Prerequisite with the ShoppingCartItem
     * @param cartItem - the cart Item to be added
     * @return true if student have completed prerequisite else return false
     */
    public boolean checkPreReq(ShoppingCartItem cartItem) {
        String user_id = cartItem.getStudent().getStudentID();
        String course_id = cartItem.getSection().getCourseID();
        StudentDAO studentDAO = new StudentDAO();
        boolean toReturn = studentDAO.hasCompletedPrerequisite(user_id, course_id);
        return toReturn;
    }

    /**
     * Check the Course Completed with the ShoppingCartItem
     * @param cartItem - the cart Item to check
     * @return true if student have completed the course else return false
     */
    public boolean checkCourseCompleted(ShoppingCartItem cartItem) {
        String user_id = cartItem.getStudent().getStudentID();
        String course_id = cartItem.getSection().getCourseID();
        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(CHECK_COURSE_COMPLETED);
            stmt.setString(1, user_id);
            stmt.setString(2, course_id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                return true;
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check the Section Enrolled with the ShoppingCartItem
     * @param cartItem - the cart Item to check
     * @return true if student have section enrolled else return false
     */
    public boolean checkIfSectionEnrolled(ShoppingCartItem cartItem) {
        String user_id = cartItem.getStudent().getStudentID();
        String course_id = cartItem.getSection().getCourseID();
        String section_id = cartItem.getSection().getSectionID();
        
        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * from section_student where User_id = ? and course_id = ? and section_id = ?");
            stmt.setString(1, user_id);
            stmt.setString(2, course_id);
            stmt.setString(3, section_id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                return true;
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     * Display the ShoppingCartItems
     * @param user_id - the user_id to display
     * @return an arrayList of section in the shoppingCart
     */
    public ArrayList<Section> displayShoppingCartItems(String user_id) {

        SectionDAO secDao = new SectionDAO();
        ArrayList<Section> cartSections = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement(RETRIEVE_SHOPPING_CART_ITEMS);
            stmt.setString(1, user_id);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String course_ID = rs.getString(1);
                String title = rs.getString(2);
                String school = rs.getString(3);
                String section = rs.getString(4);
                String instructor = rs.getString(5);
                String day = rs.getString(6);
                String start_time = rs.getString(7);
                String end_time = rs.getString(8);

                cartSections.add(secDao.retrieve(course_ID, section));
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cartSections;
    }

    /**
     * Add a new ShoppingCartItem
     * @param shoppingCartItem - the shoppingCartItem to be added
     * @return true if shoppingCartItem is added to cart else return false
     */
    public boolean addShoppingCartItem(ShoppingCartItem shoppingCartItem) {
        String user_id = shoppingCartItem.getStudent().getStudentID();
        String course_id = shoppingCartItem.getSection().getCourseID();
        String section_id = shoppingCartItem.getSection().getSectionID();
        shoppingCartItems.add(shoppingCartItem);
        Connection conn1 = null;
        PreparedStatement stmt1 = null;
        ResultSet rs = null;
        try {
            conn1 = ConnectionManager.getConnection();
            stmt1 = conn1.prepareStatement(CHECK_NUM_IN_BIDDING_CART);
            stmt1.setString(1, user_id);
            rs = stmt1.executeQuery();
            int num = 0;
            while (rs.next()) {
                num = rs.getInt(1);
            }
            if (num < 5) {
                Connection conn = null;
                PreparedStatement stmt = null;
                try {
                    conn = ConnectionManager.getConnection();
                    stmt = conn.prepareStatement(ADD_TO_BIDDING_CART);
                    stmt.setString(1, user_id);
                    stmt.setString(2, course_id);
                    stmt.setString(3, section_id);
                    stmt.execute();
                    stmt.close();
                    conn.close();
                    return true;
                } catch (SQLException e) {
                    return false;
                }
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    /**
     * Check the ShoppingCart
     * @param shoppingCartItem - the shoppingCartItem to check
     * @return true if number of shoppingCartItem is less than 5 else return false
     */
    public boolean checkCart(ShoppingCartItem shoppingCartItem) {
        String user_id = shoppingCartItem.getStudent().getStudentID();
        Connection conn1 = null;
        PreparedStatement stmt1 = null;
        ResultSet rs = null;
        try {
            conn1 = ConnectionManager.getConnection();
            stmt1 = conn1.prepareStatement(CHECK_NUM_IN_BIDDING_CART);
            stmt1.setString(1, user_id);
            rs = stmt1.executeQuery();
            int num = 0;
            while (rs.next()) {
                num = rs.getInt(1);
            }
            if (num < 5) {
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    /**
     * Check the Course in Shopping Cart 
     * @param shoppingCartItem - the shoppingCartItem to be added
     * @return true if the course in cart else return false
     */
    public boolean checkCourseInCart(ShoppingCartItem shoppingCartItem) {
        String user_id = shoppingCartItem.getStudent().getStudentID();
        String course_id = shoppingCartItem.getSection().getCourseID();
        String section_id = shoppingCartItem.getSection().getSectionID();
        Connection conn1 = null;
        PreparedStatement stmt1 = null;
        ResultSet rs = null;
        try {
            conn1 = ConnectionManager.getConnection();
            stmt1 = conn1.prepareStatement(CHECK_COURSE_IN_CART);
            stmt1.setString(1, course_id);
            stmt1.setString(2, user_id);
            rs = stmt1.executeQuery();
            while (rs.next()) {
                return true;
            }

        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    /**
     *
     * Drop Shopping Cart Item
     * @param shoppingCartItem - the shoppingCartItem to be added
     * @param user_id - the user_id to drop the shopping cart item
     * @return true if shoppingCartItem is dropped from the shopping cart else return false
     */
    public boolean dropShoppingCartItem(Section shoppingCartItem, String user_id) {
        String course_id = shoppingCartItem.getCourseID();
        String section_id = shoppingCartItem.getSectionID();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement(DELETE_ITEM_FROM_SHOPPING_CART);
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
     * Drop Shopping Cart Item
     * @param student - the student object to retrieve shopping cart item
     * @param section - the student object to retrieve shopping cart item
     * @return a ShoppingCartItem if the ShoppingCartItem object student is equal to the current student object
     * and the section is equal to the current section else return null
     */
    public ShoppingCartItem retrieveShoppingCartItem(Student student, Section section) {
        for (ShoppingCartItem s : shoppingCartItems) {
            if (s.getStudent().equals(student) && s.getSection().equals(section)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Retrieve Shopping Cart Total
     * @return the total bid amount in the Shopping Cart
     */
    public double getCartTotal() {
        double total = 0.0;
        for (ShoppingCartItem s : shoppingCartItems) {
            total += s.getAmount();
        }
        return total;
    }

    /**
     * Clear the Shopping Cart
     * @param user_id - the user_id to clear the shopping cart
     * @return true if shopping cart have been clear else false
     */
    public boolean clear(String user_id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement(CLEAR_CART_BY_USER);
            stmt.setString(1, user_id);
            stmt.execute();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            return false;
        }
        shoppingCartItems.clear();
        return true;
    }

    /**
     * Clear All the ShoppingCartItem in the Shopping Cart
     * @return true if shopping cart have been clear else false
     */
    public boolean clear() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement(CLEAR_CART);
            stmt.execute();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            return false;
        }
        shoppingCartItems.clear();
        return true;
    }

    /**
     * Count the number of items in Shopping Cart
     * @param userID - the userID to count the number of shoppingCartItem
     * @return the number of items in bidding cart
     */
    public int countNumInCart(String userID) {
        Connection conn1 = null;
        PreparedStatement stmt1 = null;
        ResultSet rs = null;
        int num = 0;
        try {
            conn1 = ConnectionManager.getConnection();
            stmt1 = conn1.prepareStatement("SELECT count(*) FROM `bidding_cart` WHERE user_id = ?");
            stmt1.setString(1, userID);
            rs = stmt1.executeQuery();
            while (rs.next()) {
                num = rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return num;
    }
    
    /**
     * Display all Section Items of User
     * @param user_id - the user_id to display all section items
     * @return an ArrayList of Section object in the sectionEnrolled
     */
    public ArrayList<Section> displaySectionItems(String user_id){
        SectionDAO secDao = new SectionDAO();
        ArrayList<Section> cartSections = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("select ss.user_id, s.section_id, s.course_id, s.startTime, s.endTime from section_student ss inner join section s on ss.section_id = s.section_id and ss.course_id = s.course_id where user_id = ?");
            stmt.setString(1, user_id);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String userID = rs.getString(1);
                String section_id = rs.getString(2);
                String course_id = rs.getString(3);
                String start_time = rs.getString(4);
                String end_time = rs.getString(5);

                cartSections.add(secDao.retrieve(course_id, section_id));
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cartSections;
    }
    
    /**
     * Check If Class Time Table Clash
     * @param userID - the userID to check
     * @param sectionID - the sectionID to check
     * @param courseID - the courseID to check
     * @param check - the check to check
     * @return true if classTimeTableclash else return false
     */
    public boolean checkIfTimeTableClash(String userID, String sectionID, String courseID, boolean check) {
        SectionDAO sectionDAO = new SectionDAO();
        Section currentSect = sectionDAO.retrieve(courseID, sectionID);
        String currentSectDay = currentSect.getDay();
        String currentSectStartTime = currentSect.getStartDate();
        String currentSectEndTime = currentSect.getEndDate();
        
        ArrayList<Section> currentUserShoppingCart = new ArrayList<>();
        if(check){
             currentUserShoppingCart = displayShoppingCartItems(userID);
        }else{
            currentUserShoppingCart = displaySectionItems(userID);
        }
        
        for (Section s : currentUserShoppingCart) {
            String toCheckDay = s.getDay();
            if (toCheckDay.equals(currentSectDay)) {
                String toCheckStartTime = s.getStartDate();
                String toCheckEndTime = s.getEndDate();
                if (toCheckStartTime.equals(currentSectStartTime) && toCheckEndTime.equals(currentSectEndTime)) {
                    return true;
                }

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    Date start = sdf.parse(currentSectStartTime);
                    Date end = sdf.parse(currentSectEndTime);
                    Date start2 = sdf.parse(toCheckStartTime);
                    Date end2 = sdf.parse(toCheckEndTime);

                    if (start2.before(end) && end2.after(end)) {
                        return true;
                    } else if (end2.after(start) && start2.before(start)) {
                        return true;
                    } else if (start2.after(start) && end2.before(end)) {
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
     *
     * Check If Exam Time Table Clash
     * @param userID - the userID to check
     * @param courseID - the courseID to check
     * @param check - the check to check
     * @return true if examTimeTableClash else return false
     */
    public boolean checkIfExamClash(String userID, String courseID, boolean check) {
        SectionDAO sectionDAO = new SectionDAO();
        CourseDAO courseDAO = new CourseDAO();
        Course currentCourse = courseDAO.retrieveCourse(courseID);

        Date currentCourseExamDate = currentCourse.getExamDate();
        String currentCourseExamStartTime = currentCourse.getExamStart();
        String currentCourseExamEndTime = currentCourse.getExamEnd();
        
        ArrayList<Section> currentUserShoppingCart = new ArrayList<>();
        if(check){ 
             currentUserShoppingCart = displayShoppingCartItems(userID);
        }else{
            currentUserShoppingCart = displaySectionItems(userID);
        }
        
        for (Section s : currentUserShoppingCart) {
            String toCheckCourseID = s.getCourseID();
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

                    if (start2.before(end) && end2.after(end)) {
                        return true;
                    } else if (end2.after(start) && start2.before(start)) {
                        return true;
                    } else if (start2.after(start) && end2.before(end)) {
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
     * Count the total number of Bids and total number of Items in Cart
     * @param user_id - the user_id to check
     * @return true if the number of bids of user plus the number of item in cart is less than 5
     * else return false
     */
    public boolean countBidsAndCart(String user_id) {
        BidDAO bidDAO = new BidDAO();
        int numOfBids = bidDAO.countNumOfBids(user_id);
        int numInCart = countNumInCart(user_id);

        System.out.println(numOfBids);
        System.out.println(numInCart);
        
        if ((numOfBids + numInCart) < 5) {
            return true;
        }

        return false;
    }
}
