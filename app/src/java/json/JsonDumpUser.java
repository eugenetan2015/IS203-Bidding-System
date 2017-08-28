/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import bios.bootstrap.util.ValidationUtility;
import bios.dao.StudentDAO;
import bios.entity.Student;
import com.google.gson.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.TreeMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Wilson and Yew Kit
 */
@WebServlet(name = "JsonDumpUser", urlPatterns = {"/user-dump", "/json/user-dump"})
public class JsonDumpUser extends HttpServlet {

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
        try (PrintWriter out = response.getWriter()) {
            ValidationUtility vu = new ValidationUtility();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject jsonOutput = new JsonObject();

            String jsonReq = request.getParameter("r");
            String token = request.getParameter("token");
            TreeMap<String, String> map = null;
            JsonArray errorMessageArray = new JsonArray();
            
            // set json property as error first because the property will override with the latest property set
            jsonOutput.addProperty("status", "error");

            map = vu.checkForParameterNull(jsonReq, token);
            // This check whether the parameter entered by the user is null.
            // if the errorMessageArray size is more than 0, its mean that there is null value in the
            // parameters
            if (map.isEmpty()) {
                try {
                    JsonParser jp = new JsonParser();
                    JsonElement root = jp.parse(jsonReq);

                    JsonObject rootObj = root.getAsJsonObject();
                    JsonElement userIDElement = rootObj.get("userid");

                    map = vu.validJSONParameters(userIDElement);

                    if (map.isEmpty()) {
                        String userID = userIDElement.getAsString().toLowerCase();
                        StudentDAO studentDAO = new StudentDAO();
                        Student student = studentDAO.retrieveStudent(userID);

                        map = vu.checkInvalid(student);

                        if (map.isEmpty()) {
                            jsonOutput.addProperty("status", "success");
                            jsonOutput.addProperty("userid", student.getStudentID());
                            jsonOutput.addProperty("password", student.getPassword());
                            jsonOutput.addProperty("name", student.getName());
                            jsonOutput.addProperty("school", student.getSchool());
                            jsonOutput.addProperty("edollar", student.geteDollars());
                        }
                    }
                } catch (Exception e) {
                    JsonPrimitive element = new JsonPrimitive("invalid r");
                    errorMessageArray.add(element);
                    jsonOutput.add("message", errorMessageArray);
                }
            }

            // if errorMessageArray is more than 0 then add this array to Json Object
            if (map != null) {
                if (!map.isEmpty()) {
                    jsonOutput.add("message", gson.toJsonTree(map.values()));
                }
            }
            out.print(gson.toJson(jsonOutput));
        }
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
