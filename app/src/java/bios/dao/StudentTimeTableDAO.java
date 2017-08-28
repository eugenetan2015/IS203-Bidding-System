/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bios.dao;

import bios.entity.Course;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Wilson
 */
public class StudentTimeTableDAO {

    private static final String ADD_DATE = "INSERT INTO student_timetable (start_date) VALUES (?)";
    private static final String DELETE_DATE = "DELETE FROM student_timetable";
    private static final String RETRIEVE_DATE = "SELECT start_date from student_timetable";
    private static final long MILLIS_PER_DAY = 24 * 60 * 60 * 1000L;

    SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-dd");

    /**
     * Retrieves the exam date from database
     *
     * @param startDate Takes in a startDate of Date object
     * @return true if addition of StartDate Object is successful, returns false
     * otherwise
     */
    public boolean addDate(Date startDate) {
        //deleteDate();

        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(ADD_DATE)) {

            stmt.setString(1, df.format(startDate));
            stmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves Date object from the start_date Table
     *
     * @return Date object
     */
    public Date retrieveDate() {
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(RETRIEVE_DATE)) {

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getDate(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the examDate
     *
     * @return a new date if the day is not Monday else return currentDate
     * (which is on Monday)
     */
    public ArrayList<Date> getExamDates() {
        CourseDAO courseDAO = new CourseDAO();
        ArrayList<Course> allCourses = courseDAO.retrieveAllCourses();
        ArrayList<Date> examDateList = new ArrayList<>();

        for (Course c : allCourses) {
            examDateList.add(c.getExamDate());
        }

        return examDateList;
    }

    /**
     * Add numbers of day to Date
     *
     * @param d the date to addDays
     * @param numDays number of days to add
     * @return Date object
     */
    public Date addDays(Date d, int numDays) {
        return new Date(d.getTime() + numDays * MILLIS_PER_DAY);
    }

    /**
     * Find the earliest exam date
     * @param examDateList list of exam dates
     * @return the earliest exam Date
     */
    public Date findEarliestExamDate(ArrayList<Date> examDateList) {
        Date earliestDate = null;

        if (examDateList != null && examDateList.size() > 0) {
            for (Date d : examDateList) {
                if (d != null) {
                    if (earliestDate == null) {
                        earliestDate = d;
                    }
                    earliestDate = d.before(earliestDate) ? d : earliestDate;
                }
            }
        }
        return earliestDate;
    }

    /**
     * Create the From Date which starts on Monday
     *
     * @param year the year
     * @param month the month
     * @param day the day
     * @return a new date if the day is not Monday else return currentDate
     * (which is on Monday)
     */
    public Date createFromDate(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        // if dayOfWeek is 2 - Monday, return back the same date.
        if (dayOfWeek == 2) {
            return c.getTime();
        }

        // If not set the current date to Monday
        c.set(Calendar.DAY_OF_WEEK, 2);

        return c.getTime();
    }

    /**
     * Create the To Date
     *
     * @param year - int year of the date , 0000-9999
     * @param month - int month of the date , 1-12
     * @param day - day of the date, 1-31
     * @return Date - Date object with the specified year, month and day
     */
    public Date createToDate(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        // this is to determine the day of the week 
        switch (dayOfWeek) {
            case 1:
                day += 1;
                break;
            case 3:
                day -= 1;
                break;
            case 4:
                day -= 2;
                break;
            case 5:
                day -= 3;
                break;
            default:
                break;
        }

        if (dayOfWeek != 2) {
            c.set(Calendar.DAY_OF_MONTH, day);
        }

        return c.getTime();
    }
}
