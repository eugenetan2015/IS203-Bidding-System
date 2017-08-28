/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bios.bootstrap.util;

/**
 *
 * @author Wilson and Yew Kit
 */
public class AdminUtility {

    private static String SECRET_KEY = "By2nhv5sbwTFQKgS";
    private static String LOCAL_UPLOAD_DIR = "uploads";
    private static String LOCAL_TIME_TABLE_DIR = "json";

    /**
     * Get the secret key used for JSON Web Token
     * @return the secret key
     */
    public static String getSecretKey() {
        return SECRET_KEY;
    }

    /**
     * Get the local upload directory used for bootstrap
     * @return the upload directory used for bootstrap
     */
    public static String getLocalUploadDir() {
        return LOCAL_UPLOAD_DIR;
    }

    /**
     * Get the Student Timetable upload directory used for Student Timetable Function
     * @return the timetable directory used for Student Timetable
     */
    public static String getLocalTimetableDir() {
        return LOCAL_TIME_TABLE_DIR;
    }
}
