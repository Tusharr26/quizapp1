package com.quizapp.servlet.admin;

import com.quizapp.dao.QuizDAO;
import com.quizapp.dao.QuestionDAO;
import com.quizapp.dao.QuizAttemptDAO;
import com.quizapp.model.Quiz;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/admin/quizzes")
public class ManageQuizzesServlet extends HttpServlet {
    
    private QuizDAO quizDAO;
    private QuestionDAO questionDAO;
    private QuizAttemptDAO quizAttemptDAO;
    
    @Override
    public void init() {
        quizDAO = new QuizDAO();
        questionDAO = new QuestionDAO();
        quizAttemptDAO = new QuizAttemptDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Get all quizzes
            List<Quiz> quizzes = quizDAO.getAllQuizzes();
            request.setAttribute("quizzes", quizzes);
            
            // Forward to the JSP page
            request.getRequestDispatcher("/WEB-INF/admin/manage-quizzes.jsp").forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Error retrieving quizzes: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        String quizIdStr = request.getParameter("quizId");
        HttpSession session = request.getSession();
        
        if (quizIdStr == null || quizIdStr.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Quiz ID is required");
            response.sendRedirect(request.getContextPath() + "/admin/quizzes");
            return;
        }
        
        try {
            long quizId = Long.parseLong(quizIdStr);
            Quiz quiz = quizDAO.getQuizById(quizId);
            
            if (quiz == null) {
                session.setAttribute("errorMessage", "Quiz not found");
                response.sendRedirect(request.getContextPath() + "/admin/quizzes");
                return;
            }
            
            if ("delete".equals(action)) {
                // Delete all associated data
                List<com.quizapp.model.Question> questions = questionDAO.getQuestionsByQuizId(quizId);
                
                for (com.quizapp.model.Question question : questions) {
                    // Delete options for each question
                    optionDAO.deleteOptionsByQuestionId(question.getId());
                }
                
                // Delete all questions for this quiz
                questionDAO.deleteQuestionsByQuizId(quizId);
                
                // Delete all quiz attempts for this quiz
                List<com.quizapp.model.QuizAttempt> attempts = quizAttemptDAO.getQuizAttemptsByQuizId(quizId);
                for (com.quizapp.model.QuizAttempt attempt : attempts) {
                    // Delete all answers for this attempt
                    ansDAO.deleteAnswersByAttemptId(attempt.getId());
                    // Delete the attempt
                    quizAttemptDAO.deleteQuizAttempt(attempt.getId());
                }
                
                // Finally, delete the quiz
                quizDAO.deleteQuiz(quizId);
                
                session.setAttribute("successMessage", "Quiz deleted successfully!");
                
            } else if ("publish".equals(action)) {
                // Toggle publish status
                quiz.setPublished(!quiz.isPublished());
                quizDAO.updateQuiz(quiz);
                
                String status = quiz.isPublished() ? "published" : "unpublished";
                session.setAttribute("successMessage", "Quiz " + status + " successfully!");
                
            } else {
                session.setAttribute("errorMessage", "Invalid action");
            }
            
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Error processing request: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/quizzes");
    }
    
    // Need to declare these for the doPost method to reference
    private com.quizapp.dao.OptionDAO optionDAO = new com.quizapp.dao.OptionDAO();
    private com.quizapp.dao.AnsDAO ansDAO = new com.quizapp.dao.AnsDAO();
}