package com.quizapp.servlet;

import com.quizapp.dao.QuizAttemptDAO;
import com.quizapp.dao.QuizDAO;
import com.quizapp.model.Quiz;
import com.quizapp.model.QuizAttempt;
import com.quizapp.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/dashboard")
public class DashBoardServlet extends HttpServlet {
    private QuizDAO quizDAO;
    private QuizAttemptDAO quizAttemptDAO;
    
    @Override
    public void init() throws ServletException {
        quizDAO = new QuizDAO();
        quizAttemptDAO = new QuizAttemptDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        
        try {
            // Get recent quizzes
            List<Quiz> recentQuizzes = quizDAO.getRecentQuizzes(5);
            request.setAttribute("recentQuizzes", recentQuizzes);
            
            // Get user's attempt history
            List<QuizAttempt> userAttempts = quizAttemptDAO.getAttemptsByUser(currentUser.getId());
            request.setAttribute("userAttempts", userAttempts);
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Failed to load dashboard data");
        }
        
        // Forward to dashboard page
        request.getRequestDispatcher("/WEB-INF/jsp/dashboard.jsp").forward(request, response);
    }
}