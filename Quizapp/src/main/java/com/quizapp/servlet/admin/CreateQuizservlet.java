package com.quizapp.servlet.admin;

import com.quizapp.dao.QuizDAO;
import com.quizapp.dao.QuestionDAO;
import com.quizapp.dao.OptionDAO;
import com.quizapp.model.Quiz;
import com.quizapp.model.Question;
import com.quizapp.model.Option;
import com.quizapp.model.User;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/quiz/create")
public class CreateQuizservlet extends HttpServlet {
    
    private QuizDAO quizDAO;
    private QuestionDAO questionDAO;
    private OptionDAO optionDAO;
    
    @Override
    public void init() {
        quizDAO = new QuizDAO();
        questionDAO = new QuestionDAO();
        optionDAO = new OptionDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/admin/create-quiz.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String timeLimit = request.getParameter("timeLimit");
        String isPublished = request.getParameter("isPublished");
        
        // Validate quiz data
        if (title == null || title.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Quiz title is required");
            request.getRequestDispatcher("/WEB-INF/admin/create-quiz.jsp").forward(request, response);
            return;
        }
        
        try {
            // Create quiz
            Quiz quiz = new Quiz();
            quiz.setTitle(title);
            quiz.setDescription(description);
            
            // Handle time limit
            int timeLimitValue = 0;
            if (timeLimit != null && !timeLimit.trim().isEmpty()) {
                try {
                    timeLimitValue = Integer.parseInt(timeLimit);
                } catch (NumberFormatException e) {
                    // Use default value
                }
            }
            
            quiz.setTimeLimit(timeLimitValue);
            quiz.setPublished(isPublished != null && isPublished.equals("on"));
            quiz.setCreatorId(currentUser.getId());
            
            // Save quiz to database
            int quizId = quizDAO.createQuiz(quiz);
            quiz.setId((long) quizId);
            
            // Process questions and options
            String[] questionTexts = request.getParameterValues("questionText");
            String[] questionTypes = request.getParameterValues("questionType");
            
            if (questionTexts != null && questionTexts.length > 0) {
                for (int i = 0; i < questionTexts.length; i++) {
                    if (questionTexts[i] != null && !questionTexts[i].trim().isEmpty()) {
                        // Create question
                        Question question = new Question();
                        question.setQuizId(quiz.getId());
                        question.setQuestionText(questionTexts[i]);
                        question.setQuestionType(questionTypes[i]);
                        
                        // Save question to database
                        int questionId = questionDAO.createQuestion(question);
                        question.setId((long) questionId);
                        
                        // Process options for this question
                        String[] optionTexts = request.getParameterValues("optionText" + i);
                        String[] correctOptions = request.getParameterValues("correctOption" + i);
                        
                        if (optionTexts != null && optionTexts.length > 0) {
                            for (int j = 0; j < optionTexts.length; j++) {
                                if (optionTexts[j] != null && !optionTexts[j].trim().isEmpty()) {
                                    // Create option
                                    Option option = new Option();
                                    option.setQuestionId(question.getId());
                                    option.setOptionText(optionTexts[j]);
                                    
                                    // Check if this option is marked as correct
                                    boolean isCorrect = false;
                                    if (correctOptions != null) {
                                        for (String correctOption : correctOptions) {
                                            if (correctOption.equals(String.valueOf(j))) {
                                                isCorrect = true;
                                                break;
                                            }
                                        }
                                    }
                                    option.setCorrect(isCorrect);
                                    
                                    // Save option to database
                                    optionDAO.createOption(option);
                                }
                            }
                        }
                    }
                }
            }
            
            // Redirect to quiz management page
            session.setAttribute("successMessage", "Quiz created successfully!");
            response.sendRedirect(request.getContextPath() + "/admin/quizzes");
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error creating quiz: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/admin/create-quiz.jsp").forward(request, response);
        }
    }
}