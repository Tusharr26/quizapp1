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
import java.util.Date;

@WebServlet("/quiz/start/*")
public class StartQuizServlet extends HttpServlet {

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
        
        // Get quiz ID from URL
        String pathInfo = request.getPathInfo();
        String[] pathParts = pathInfo.split("/");
        Long quizId = Long.parseLong(pathParts[1]);
        
        // Get current user from session
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("user");
        
        try {
            // Load quiz
            Quiz quiz = quizDAO.getQuizById(quizId);
            
            if (quiz == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Quiz not found");
                return;
            }
            
            // Create new quiz attempt
            QuizAttempt attempt = new QuizAttempt();
            attempt.setQuizId(quizId);
            attempt.setUserId(currentUser.getId());
            attempt.setAttemptDate(new Date());
            attempt.setScore(0);
            attempt.setCompleted(false);
            
            // Save attempt and get generated ID
            int attemptId = quizAttemptDAO.createQuizAttempt(attempt);
            attempt.setId((long) attemptId);
            
            // Store attempt in session
            session.setAttribute("currentAttempt", attempt);
            session.setAttribute("currentQuiz", quiz);
            session.setAttribute("currentQuestionIndex", 0);
            
            // Redirect to quiz taking page
            response.sendRedirect(request.getContextPath() + "/quiz/take");
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error occurred");
        }
    }
}