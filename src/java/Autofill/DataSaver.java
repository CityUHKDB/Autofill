package Autofill;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class DataSaver extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession(true);
        
        // if user have logined
        if (session.getAttribute("user") != null) {
            User user = (User)session.getAttribute("user");
            
            // read data
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String data = reader.readLine();
            reader.close();
            
            // Store in database
            Connection con = null;
            PreparedStatement pstmt = null;
            try {
                con = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3307/Autofill",
                    "root",
                    "admin"
                );
                pstmt = con.prepareStatement(
                    "UPDATE User SET data = ? WHERE username = ?"
                );
                pstmt.setString(1, data);
                pstmt.setString(2, user.getUsername());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (pstmt != null) {pstmt.close();}
                    if (con != null) {con.close();}
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
            // save data to bean
            user.setData(data);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

}
