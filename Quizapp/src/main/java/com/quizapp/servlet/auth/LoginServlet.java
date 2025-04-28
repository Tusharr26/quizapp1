package com.quizapp.servlet.auth;

import com.quizapp.dao.UserDAO;
import com.quizapp.model.User;
import com.quizapp.util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/auth/login")
public class LoginServlet extends HttpServlet {

    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Forward to login page
        request.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String errorMessage = null;
        
        try {
            User user = userDAO.getUserByUsername(username);
            
            if (user != null && PasswordUtil.verifyPassword(password, user.getPassword())) {
                // Authentication successful - create session
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                
                // Redirect to dashboard
                response.sendRedirect(request.getContextPath() + "/dashboard");
                return;
            } else {
                errorMessage = "Invalid username or password";
            }
        } catch (SQLException e) {
            errorMessage = "Database error occurred. Please try again later.";
            e.printStackTrace();
        }
        
        // Authentication failed - return to login form with error
        request.setAttribute("errorMessage", errorMessage);
        request.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(request, response);
    }
}