/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bios.servlet;

import bios.bootstrap.util.AdminUtility;
import bios.dao.*;
import bios.entity.*;
import bios.entity.StudentTimeTable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Wilson
 */
@WebServlet(name = "TimetableController", urlPatterns = {"/StudentTimeTableController"})
public class StudentTimeTableController extends HttpServlet {

    final static int NUM_OF_DAYS_SEM1_START = 105;
    final static int NUM_OF_DAY_WINTER_VACATION = 45;
    Date fromDate = null;

    private static final long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;

    // format in YYYY-MM-dd
    SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-dd");
    SimpleDateFormat yDf = new SimpleDateFormat("yyyy");

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession();
        Student studentID = (Student) session.getAttribute("user");

        if (studentID == null) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
            dispatcher.forward(request, response);
        } else {
            StudentTimeTableDAO studentTimeTableDAO = new StudentTimeTableDAO();
            Date date = studentTimeTableDAO.retrieveDate();

            if (date != null) {
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DATE);
                // get the hashmap of all the dates list (Mon to Friday)
                HashMap<String, ArrayList<String>> map = processSem1(studentID, month, day);

                // Start processing all the bids, enrolled course, bid in bidding cart
                List<StudentTimeTable> objList = new ArrayList<>();

                // process student bids, only show when the bidStatus is started.
                // Get the bid status
                BidDAO bidDAO = new BidDAO();
                String[] roundStatus = bidDAO.getCurrentBidRound();
                String bidStatus = roundStatus[0];
                if (bidStatus.equals("started")) {
                    ArrayList<StudentTimeTable> studBids
                            = processStudentBids(map, studentID.getStudentID(), "#378006", true);

                    for (StudentTimeTable studTimeTable : studBids) {
                        objList.add(studTimeTable);
                    }
                }

                // process student enrolled course
                ArrayList<StudentTimeTable> studEnrolledList
                        = processStudentEnrolledCourse(map, studentID.getStudentID(), "#6495ED");

                for (StudentTimeTable studTimeTable : studEnrolledList) {
                    objList.add(studTimeTable);
                }

                // process student bidding cart
                ArrayList<StudentTimeTable> studBiddingCart
                        = processStudentBids(map, studentID.getStudentID(), "red", false);

                for (StudentTimeTable studTimeTable : studBiddingCart) {
                    objList.add(studTimeTable);
                }

                // create json and write to json file
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(objList);
                String jsonDirToWrite = AdminUtility.getLocalTimetableDir() + "/" + studentID.getStudentID() + "_timetable.json";

                try (FileWriter file = new FileWriter(getServletContext().getRealPath("/") + jsonDirToWrite)) {
                    file.write(json);
                }
            }

            if (fromDate != null) {
                request.setAttribute("year", df.format(fromDate));
            }
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/viewMyClasses.jsp");
            dispatcher.forward(request, response);
        }
    }

    /**
     * Finds the earliest start date of the student's timetable
     *
     * @param dateList - List of Date objects that is passed in
     * @return Date - Earliest Date object
     */
    public Date findEarliestDate(ArrayList<Date> dateList) {
        Date earliestDate = null;

        if (dateList != null && dateList.size() > 0) {
            for (Date d : dateList) {
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
     * Adds starting date to the calendar
     *
     * @param fromDate the fromDate which is
     * @return Date object - consists of the starting date
     */
    public Date schoolStartSem1(Date fromDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(fromDate);
        c.add(Calendar.DAY_OF_YEAR, NUM_OF_DAYS_SEM1_START);
        return c.getTime();
    }

    /**
     * Processes the student's bids and converts and prints them into the
     * timetable
     *
     * @param map - Hashmap containing list of dates
     * @param studentID - userid of student
     * @param colorOfEvent - Color of the bid that is shown on timetable
     * @param control - Controls whether the bid to be placed and shown on the
     * timetable or not
     * @return the studentTimetable list
     */
    public ArrayList<StudentTimeTable> processStudentBids(HashMap<String, ArrayList<String>> map, String studentID, String colorOfEvent, boolean control) {
        ArrayList<StudentTimeTable> studBidList = new ArrayList<>();

        int i = 0;
        CourseDAO courseDAO = new CourseDAO();
        SectionDAO sectDAO = new SectionDAO();
        BidDAO bidDAO = new BidDAO();

        ArrayList<Bid> allStudentBids = new ArrayList<>();

        if (control) {
            allStudentBids = bidDAO.viewStudentBids(studentID);
        } else {
            allStudentBids = bidDAO.getBiddingCart(studentID);
        }

        for (Bid b : allStudentBids) {
            if (b.getStatus().equals("Pending")) {
                Course c = courseDAO.retrieveCourse(b.getCourseID());
                Section s = sectDAO.retrieve(b.getCourseID(), b.getSectionID());

                String day = s.getDay();
                String dayLowerCase = day.toLowerCase();

                ArrayList<String> monList = map.get(dayLowerCase);
                for (String date : monList) {
                    String startDate = date + "T" + s.getStartDate();
                    String endDate = date + "T" + s.getEndDate();
                    studBidList.add(new StudentTimeTable(i, startDate, endDate, s.getSectionID(), c.getCourseID(), c.getTitle(), s.getVenue(), colorOfEvent));
                    i++;
                }

                // add the exam date
                studBidList.add(new StudentTimeTable(i, df.format(c.getExamDate()) + "T" + c.getExamStart(), df.format(c.getExamDate()) + "T" + c.getExamEnd(), "EXAM", c.getCourseID(), c.getTitle(), s.getVenue(), colorOfEvent));
            }
        }
        return studBidList;
    }

    /**
     * Processes Student's enrolled courses and prints them onto the timetable
     *
     * @param map Hashmap of dates
     * @param studentID userId of the student
     * @param colorOfEvent color of bid shown on the tiimetable
     * @return Arraylist of StudentTimetable objects
     */
    public ArrayList<StudentTimeTable> processStudentEnrolledCourse(HashMap<String, ArrayList<String>> map, String studentID, String colorOfEvent) {
        ArrayList<StudentTimeTable> studList = new ArrayList<>();
        CourseDAO courseDAO = new CourseDAO();
        SectionDAO sectDAO = new SectionDAO();
        ArrayList<Section> allStudentEnrolled = sectDAO.retrieveSectionsOfStudent(studentID);

        for (Section s : allStudentEnrolled) {
            String courseID = s.getCourseID();
            String sectionID = s.getSectionID();

            Section sectEnrolled = sectDAO.retrieve(courseID, sectionID);
            String day = sectEnrolled.getDay();
            String dayLowerCase = day.toLowerCase();
            ArrayList<String> list = map.get(dayLowerCase);
            Course c = courseDAO.retrieveCourse(courseID);
            for (String str2 : list) {
                String startDate = str2 + "T" + sectEnrolled.getStartDate();
                String endDate = str2 + "T" + sectEnrolled.getEndDate();
                studList.add(new StudentTimeTable(1, startDate, endDate, sectionID, courseID, c.getTitle(), s.getVenue(), colorOfEvent));
            }

            // add the exam date
            studList.add(new StudentTimeTable(1, df.format(c.getExamDate()) + "T" + c.getExamStart(), df.format(c.getExamDate()) + "T" + c.getExamEnd(), "EXAM", c.getCourseID(), c.getTitle(), s.getVenue(), colorOfEvent));
        }

        return studList;
    }

    /**
     * Gets Arraylist of Date objects containing the exam dates
     *
     * @param studentID - userID of the student
     * @return Arraylist of Date objects containing the exam dates
     */
    public ArrayList<Date> getExamDates(String studentID) {
        SectionDAO sectDAO = new SectionDAO();
        BidDAO bidDAO = new BidDAO();
        ArrayList<Bid> allStudentBids = bidDAO.viewStudentBids(studentID);
        ArrayList<Bid> allStudentBiddingCart = bidDAO.getBiddingCart(studentID);
        ArrayList<Section> allStudentEnrolled = sectDAO.retrieveSectionsOfStudent(studentID);

        ArrayList<Date> examDateList = new ArrayList<>();
        CourseDAO courseDAO = new CourseDAO();

        for (Bid b : allStudentBids) {
            Course c = courseDAO.retrieveCourse(b.getCourseID());
            examDateList.add(c.getExamDate());
        }

        for (Section s : allStudentEnrolled) {
            String courseID = s.getCourseID();
            Course c = courseDAO.retrieveCourse(courseID);
            examDateList.add(c.getExamDate());
        }

        for (Bid b : allStudentBiddingCart) {
            Course c = courseDAO.retrieveCourse(b.getCourseID());
            examDateList.add(c.getExamDate());
        }

        return examDateList;
    }

    /**
     * Processes Semester 1
     *
     * @param studentID - student's ID
     * @param month - Month of the date
     * @param day - Day of the Date
     * @return a hashmap with list of dates from Monday to Sunday
     */
    public HashMap<String, ArrayList<String>> processSem1(Student studentID, int month, int day) {
        HashMap<String, ArrayList<String>> map = new HashMap<>();
        ArrayList<String> mondayDateList = new ArrayList<>();
        ArrayList<String> tuesdayDateList = new ArrayList<>();
        ArrayList<String> wednesdayDateList = new ArrayList<>();
        ArrayList<String> thursdayDateList = new ArrayList<>();
        ArrayList<String> fridayDateList = new ArrayList<>();
        ArrayList<String> saturdayDateList = new ArrayList<>();
        ArrayList<String> sundayDateList = new ArrayList<>();

        StudentTimeTableDAO studentTimeTableDAO = new StudentTimeTableDAO();

        // get all the exam Dates
        ArrayList<Date> examDateList
                = getExamDates(studentID.getStudentID());

        // find the earliest date, this value is required to draw the events up till the
        // earliest date
        Date examDate = studentTimeTableDAO.findEarliestExamDate(examDateList);

        if (examDate != null) {
            // get the year
            int year = Integer.parseInt(yDf.format(examDate));
            // 1. create the from date
            fromDate = studentTimeTableDAO.createToDate(year, month, day);

            // 2. create the to date (the entire schedule for sem 1
            Date toDate = schoolStartSem1(fromDate);

            Calendar cal = Calendar.getInstance();
            cal.setTime(fromDate);

            // format in Day. e.g. Monday
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");

            Date oneWeekBeforeExamDate = studentTimeTableDAO.addDays(examDate, -7);

            // loop when current calendar is before toDate and current calendar is before exam Date
            while (cal.getTime().before(toDate) && cal.getTime().before(oneWeekBeforeExamDate)) {
                String dayInFull = dayFormat.format(cal.getTime());
                switch (dayInFull) {
                    case "Monday":
                        mondayDateList.add(df.format(cal.getTime()));
                        break;
                    case "Tuesday":
                        tuesdayDateList.add(df.format(cal.getTime()));
                        break;
                    case "Wednesday":
                        wednesdayDateList.add(df.format(cal.getTime()));
                        break;
                    case "Thursday":
                        thursdayDateList.add(df.format(cal.getTime()));
                        break;
                    case "Friday":
                        fridayDateList.add(df.format(cal.getTime()));
                        break;
                    case "Saturday":
                        saturdayDateList.add(df.format(cal.getTime()));
                        break;
                    case "Sunday":
                        sundayDateList.add(df.format(cal.getTime()));
                        break;
                    default:
                        break;
                }

                // increase the day while looping
                cal.add(Calendar.DATE, 1);
            }

            // Put all the arraylist in map for data retrieval
            map.put("monday", mondayDateList);
            map.put("tuesday", tuesdayDateList);
            map.put("wednesday", wednesdayDateList);
            map.put("thursday", thursdayDateList);
            map.put("friday", fridayDateList);
            map.put("saturday", saturdayDateList);
            map.put("sunday", sundayDateList);
        }
        return map;
    }

    /**
     * Get the date that starts on Monday
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

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
