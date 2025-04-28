package com.quizapp.servlet.quiz;

import com.quizapp.dao.QuestionDAO;
import com.quizapp.dao.QuizAttemptDAO;
import com.quizapp.model.Question;
import com.quizapp.model.Quiz;
import com.quizapp.model.QuizAttempt;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/quiz/submit")
public class SubmitAnswerServlet extends HttpServlet {
    private QuestionDAO questionDAO;
    private QuizAttemptDAO quizAttemptDAO;
    
    @Override
    public void init() throws ServletException {
        questionDAO = new QuestionDAO();
        quizAttemptDAO = new QuizAttemptDAO();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        
        QuizAttempt currentAttempt = (QuizAttempt) session.getAttribute("currentAttempt");
        Quiz currentQuiz = (Quiz) session.getAttribute("currentQuiz");
        Integer currentQuestionIndex = (Integer) session.getAttribute("currentQuestionIndex");
        
        if (currentAttempt == null || currentQuiz == null || currentQuestionIndex == null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        try {
            // Get all questions for this quiz
            List<Question> questions = questionDAO.getQuestionsByQuizId(currentQuiz.getId());
            
            // Get current question
            Question currentQuestion = questions.get(currentQuestionIndex);
            
            // Get user's answer
            String userAnswer = request.getParameter("answer");
            
            // Check if answer is correct and update score
            boolean isCorrect = userAnswer != null && userAnswer.equals(currentQuestion.getCorrectAnswer());
            if (isCorrect) {
                currentAttempt.setScore(currentAttempt.getScore() + 1);
            }
            
            // Save user's answer to database
            quizAttemptDAO.saveAnswer(currentAttempt.getId(), currentQuestion.getId(), userAnswer, isCorrect);
            
            // Move to next question
            currentQuestionIndex++;
            session.setAttribute("currentQuestionIndex", currentQuestionIndex);
            
            // Check if we've reached the end of the quiz
            if (currentQuestionIndex >= questions.size()) {
                // Quiz is complete
                currentAttempt.setCompleted(true);
                quizAttemptDAO.updateQuizAttempt(currentAttempt);
                
                // Clear quiz-related session attributes
                session.removeAttribute("currentAttempt");
                session.removeAttribute("currentQuiz");
                session.removeAttribute("currentQuestionIndex");
                
                // Redirect to results page
                response.sendRedirect(request.getContextPath() + "/quiz/finish/" + currentAttempt.getId());
            } else {
                // Continue to next question
                response.sendRedirect(request.getContextPath() + "/quiz/take");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error occurred");
        }
    }
}