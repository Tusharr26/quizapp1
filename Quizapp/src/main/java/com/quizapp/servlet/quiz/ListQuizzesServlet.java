package com.quizapp.servlet.quiz;

import com.quizapp.dao.QuizDAO;
import com.quizapp.model.Quiz;
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

@WebServlet("/quizzes")
public class ListQuizzesServlet extends HttpServlet {
    private QuizDAO quizDAO;
    
    @Override
    public void init() throws ServletException {
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
        
        // Get filter parameters
        String category = request.getParameter("category");
        String difficulty = request.getParameter("difficulty");
        String searchQuery = request.getParameter("search");
        
        try {
            List<Quiz> quizzes;
            
            // Apply filters if provided
            if (category != null && !category.isEmpty()) {
                quizzes = quizDAO.getQuizzesByCategory(category);
                request.setAttribute("selectedCategory", category);
            } else if (difficulty != null && !difficulty.isEmpty()) {
                quizzes = quizDAO.getQuizzesByDifficulty(difficulty);
                request.setAttribute("selectedDifficulty", difficulty);
            } else if (searchQuery != null && !searchQuery.isEmpty()) {
                quizzes = quizDAO.searchQuizzes(searchQuery);
                request.setAttribute("searchQuery", searchQuery);
            } else {
                // Get all quizzes
                quizzes = quizDAO.getAllQuizzes();
            }
            
            // Get available categories for filter dropdown
            List<String> categories = quizDAO.getAllCategories();
            
            // Set attributes
            request.setAttribute("quizzes", quizzes);
            request.setAttribute("categories", categories);
            
            // Forward to quiz list page
            request.getRequestDispatcher("/WEB-INF/jsp/quiz/list.jsp").forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error occurred");
        }
    }
}