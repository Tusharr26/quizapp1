package com.quizapp.servlet.quiz;

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

@WebServlet("/quiz/finish/*")
public class FinishQuizServlet extends HttpServlet {
    private QuizAttemptDAO quizAttemptDAO;
    private QuizDAO quizDAO;
    
    @Override
    public void init() throws ServletException {
        quizAttemptDAO = new QuizAttemptDAO();
        quizDAO = new QuizDAO();
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
        
        // Get attempt ID from URL
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        String[] pathParts = pathInfo.split("/");
        if (pathParts.length < 2) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        try {
            Long attemptId = Long.parseLong(pathParts[1]);
            
            // Load attempt
            QuizAttempt attempt = quizAttemptDAO.getAttemptById(attemptId);
            
            // Verify the attempt belongs to the current user
            if (attempt == null || !attempt.getUserId().equals(currentUser.getId())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                return;
            }
            
            // Load quiz
            Quiz quiz = quizDAO.getQuizById(attempt.getQuizId());
            
            // Set attributes for result page
            request.setAttribute("attempt", attempt);
            request.setAttribute("quiz", quiz);
            
            // Calculate percentage score
            int totalQuestions = quizAttemptDAO.getAnswerCountByAttempt(attemptId);
            if (totalQuestions > 0) {
                double percentage = (double) attempt.getScore() / totalQuestions * 100;
                request.setAttribute("percentage", Math.round(percentage));
            } else {
                request.setAttribute("percentage", 0);
            }
            
            // Forward to results page
            request.getRequestDispatcher("/WEB-INF/jsp/quiz/result.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error occurred");
        }
    }
}