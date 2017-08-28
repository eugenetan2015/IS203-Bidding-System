/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bios.dao;

import java.sql.*;

/**
 *
 * @author Tan Ming Kwang
 */
public class LoginDAO {

    private static final String RETRIEVE_ALL = "select * from student";

    /**
     * Authenticates a user whether the user is valid(correct userid and password) returns true
     * @param username Takes in the username 
     * @param password Takes in the password
     * @return true if successful, false otherwise
     */
    public boolean authenticate(String username, String password) {

        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(RETRIEVE_ALL);
                ResultSet rs = stmt.executeQuery();) {

            while (rs.next()) {
                String id = rs.getString(1);
                String pw = rs.getString(2);
                if (id.equals(username) && pw.equals(password)) {
                    ConnectionManager.close(conn);
                    return true;
                }
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
     * Verifies a user whether he is an administrator, returns true if verifcation is correct, false otherwise.
     * @param username Takes in an username
     * @param password Takes in a password
     * @return true if successful, false otherwise
     */
    public boolean verifyAdmin(String username, String password) {
        
        try {Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(RETRIEVE_ALL + " where type=?");
                stmt.setString(1, "admin");
                ResultSet rs = stmt.executeQuery(); 

            while (rs.next()) {
                String id = rs.getString(1);
                String pw = rs.getString(2);
                if (id.equals(username) && pw.equals(password)) {
                    ConnectionManager.close(conn);
                    return true;
                }
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
