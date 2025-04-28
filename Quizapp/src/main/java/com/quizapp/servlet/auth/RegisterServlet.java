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

@WebServlet("/auth/register")
public class RegisterServlet extends HttpServlet {
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
        
        // Forward to registration page
        request.getRequestDispatcher("/WEB-INF/jsp/auth/register.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String errorMessage = null;
        
        try {
            // Validate input
            if (username == null || username.trim().isEmpty()) {
                errorMessage = "Username is required";
            } else if (email == null || email.trim().isEmpty()) {
                errorMessage = "Email is required";
            } else if (password == null || password.trim().isEmpty()) {
                errorMessage = "Password is required";
            } else if (!password.equals(confirmPassword)) {
                errorMessage = "Passwords do not match";
            } else {
                // Check if username already exists
                User existingUser = userDAO.getUserByUsername(username);
                if (existingUser != null) {
                    errorMessage = "Username already exists";
                } else {
                    // Create user
                    User user = new User();
                    user.setUsername(username);
                    user.setEmail(email);
                    user.setPassword(PasswordUtil.hashPassword(password));
                    
                    // Save user
                    int userId = userDAO.createUser(user);
                    user.setId((long) userId);
                    
                    // Create session
                    HttpSession session = request.getSession();
                    session.setAttribute("user", user);
                    
                    // Redirect to dashboard
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                    return;
                }
            }
        } catch (SQLException e) {
            errorMessage = "Database error occurred. Please try again later.";
            e.printStackTrace();
        }
        
        // Registration failed - return to form with error
        request.setAttribute("errorMessage", errorMessage);
        request.setAttribute("username", username);
        request.setAttribute("email", email);
        request.getRequestDispatcher("/WEB-INF/jsp/auth/register.jsp").forward(request, response);
    }
}