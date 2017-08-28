/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import bios.bootstrap.util.AdminUtility;
import bios.bootstrap.util.ValidationUtility;
import bios.dao.LoginDAO;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import is203.JWTUtility;
import com.google.gson.*;
import java.util.HashMap;
import java.util.TreeMap;

/**
 *
 * @author Tan Ming Kwang and Cristabel
 */
@WebServlet(name = "JsonLogin", urlPatterns = {"/authenticate", "/json/authenticate"})
public class JsonLogin extends HttpServlet {

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
        PrintWriter out = response.getWriter();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jsonOutput = new JsonObject();
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        LoginDAO loginDAO = new LoginDAO();
        ValidationUtility vu = new ValidationUtility();
        JsonArray messageArray = new JsonArray();

        TreeMap<String, String> map = vu.checkLoginParameterBlank(username, password);
        jsonOutput.addProperty("status", "error");

        if (map.isEmpty()) {
            if (loginDAO.verifyAdmin(username, password)) {
                jsonOutput.addProperty("status", "success");
                String token = JWTUtility.sign(AdminUtility.getSecretKey(), username);
                jsonOutput.addProperty("token", token);
            } else {
                JsonPrimitive element = new JsonPrimitive("invalid username/password");
                messageArray.add(element);
                jsonOutput.add("message", messageArray);
            }
        }

        if (map != null) {
            if (map.size() > 0) {
                jsonOutput.add("message", gson.toJsonTree(map.values()));
            }
        }
        out.print(gson.toJson(jsonOutput));
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
        //uncomment for testing
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
