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
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/admin/quiz/edit")
public class EditQuizServlet extends HttpServlet {
    
    private QuizDAO quizDAO;
    private QuestionDAO questionDAO;
    private optionDAO optionDAO;
    
    @Override
    public void init() {
        quizDAO = new QuizDAO();
        questionDAO = new QuestionDAO();
        optionDAO = new optionDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String quizIdStr = request.getParameter("id");
        
        if (quizIdStr == null || quizIdStr.trim().isEmpty()) {
            resp	onse.sendRedirect(request.getContextPath() + "/admin/quizzes");
            return;
        }
        
        try {
            long quizId = Long.parseLong(quizIdStr);
            Quiz quiz = quizDAO.getQuizById(quizId);
            
            if (quiz == null) {
                response.sendRedirect(request.getContextPath() + "/admin/quizzes");
                return;
            }
            
            // Load questions for this quiz
            List<Question> questions = questionDAO.getQuestionsByQuizId(quizId);
            
            // Load options for each question
            for (Question question : questions) {
                List<Option> options = optionDAO.getOptionsByQuestionId(question.getId());
                question.setOptions(options);
            }
            
            // Set quiz and questions as request attributes
            request.setAttribute("quiz", quiz);
            request.setAttribute("questions", questions);
            
            request.getRequestDispatcher("/WEB-INF/admin/edit-quiz.jsp").forward(request, response);
            
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/quizzes");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String quizIdStr = request.getParameter("quizId");
        
        if (quizIdStr == null || quizIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/quizzes");
            return;
        }
        
        try {
            long quizId = Long.parseLong(quizIdStr);
            Quiz quiz = quizDAO.getQuizById(quizId);
            
            if (quiz == null) {
                response.sendRedirect(request.getContextPath() + "/admin/quizzes");
                return;
            }
            
            // Update quiz details
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String timeLimit = request.getParameter("timeLimit");
            String isPublished = request.getParameter("isPublished");
            
            // Validate quiz data
            if (title == null || title.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Quiz title is required");
                doGet(request, response);
                return;
            }
            
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
            
            // Update quiz in database
            quizDAO.updateQuiz(quiz);
            
            // Handle existing questions that should be updated or deleted
            String[] existingQuestionIds = request.getParameterValues("existingQuestionId");
            String[] deleteQuestionIds = request.getParameterValues("deleteQuestion");
            
            // Delete questions marked for deletion
            if (deleteQuestionIds != null) {
                for (String deleteId : deleteQuestionIds) {
                    long questionId = Long.parseLong(deleteId);
                    // Delete all options for this question
                    optionDAO.deleteOptionsByQuestionId(questionId);
                    // Delete the question
                    questionDAO.deleteQuestion(questionId);
                }
            }
            
            // Update existing questions
            if (existingQuestionIds != null) {
                for (String questionIdStr : existingQuestionIds) {
                    long questionId = Long.parseLong(questionIdStr);
                    
                    // Skip questions marked for deletion
                    if (deleteQuestionIds != null) {
                        boolean isDeleted = false;
                        for (String deleteId : deleteQuestionIds) {
                            if (deleteId.equals(questionIdStr)) {
                                isDeleted = true;
                                break;
                            }
                        }
                        if (isDeleted) continue;
                    }
                    
                    // Update question
                    String questionText = request.getParameter("questionText" + questionId);
                    String questionType = request.getParameter("questionType" + questionId);
                    
                    if (questionText != null && !questionText.trim().isEmpty()) {
                        Question question = questionDAO.getQuestionById(questionId);
                        question.setQuestionText(questionText);
                        question.setQuestionType(questionType);
                        questionDAO.updateQuestion(question);
                        
                        // Handle options for this question
                        String[] existingOptionIds = request.getParameterValues("existingOptionId" + questionId);
                        String[] deleteOptionIds = request.getParameterValues("deleteOption" + questionId);
                        
                        // Delete options marked for deletion
                        if (deleteOptionIds != null) {
                            for (String deleteId : deleteOptionIds) {
                                long optionId = Long.parseLong(deleteId);
                                optionDAO.deleteOption(optionId);
                            }
                        }
                        
                        // Update existing options
                        if (existingOptionIds != null) {
                            for (String optionIdStr : existingOptionIds) {
                                long optionId = Long.parseLong(optionIdStr);
                                
                                // Skip options marked for deletion
                                if (deleteOptionIds != null) {
                                    boolean isDeleted = false;
                                    for (String deleteId : deleteOptionIds) {
                                        if (deleteId.equals(optionIdStr)) {
                                            isDeleted = true;
                                            break;
                                        }
                                    }
                                    if (isDeleted) continue;
                                }
                                
                                // Update option
                                String optionText = request.getParameter("optionText" + questionId + "_" + optionId);
                                String isCorrect = request.getParameter("correctOption" + questionId + "_" + optionId);
                                
                                Option option = optionDAO.getOptionById(optionId);
                                option.setOptionText(optionText);
                                option.setCorrect(isCorrect != null && isCorrect.equals("on"));
                                optionDAO.updateOption(option);
                            }
                        }
                        
                        // Add new options
                        String[] newOptionTexts = request.getParameterValues("newOptionText" + questionId);
                        String[] newCorrectOptions = request.getParameterValues("newCorrectOption" + questionId);
                        
                        if (newOptionTexts != null) {
                            for (int i = 0; i < newOptionTexts.length; i++) {
                                if (newOptionTexts[i] != null && !newOptionTexts[i].trim().isEmpty()) {
                                    Option option = new Option();
                                    option.setQuestionId(questionId);
                                    option.setOptionText(newOptionTexts[i]);
                                    
                                    boolean isCorrect = false;
                                    if (newCorrectOptions != null) {
                                        for (String correctOption : newCorrectOptions) {
                                            if (correctOption.equals(String.valueOf(i))) {
                                                isCorrect = true;
                                                break;
                                            }
                                        }
                                    }
                                    option.setCorrect(isCorrect);
                                    
                                    optionDAO.createOption(option);
                                }
                            }
                        }
                    }
                }
            }
            
            // Process new questions
            String[] newQuestionTexts = request.getParameterValues("newQuestionText");
            String[] newQuestionTypes = request.getParameterValues("newQuestionType");
            
            if (newQuestionTexts != null && newQuestionTexts.length > 0) {
                for (int i = 0; i < newQuestionTexts.length; i++) {
                    if (newQuestionTexts[i] != null && !newQuestionTexts[i].trim().isEmpty()) {
                        // Create question
                        Question question = new Question();
                        question.setQuizId(quizId);
                        question.setQuestionText(newQuestionTexts[i]);
                        question.setQuestionType(newQuestionTypes[i]);
                        
                        // Save question to database
                        int questionId = questionDAO.createQuestion(question);
                        question.setId((long) questionId);
                        
                        // Process options for this question
                        String[] optionTexts = request.getParameterValues("newQuestionOptionText" + i);
                        String[] correctOptions = request.getParameterValues("newQuestionCorrectOption" + i);
                        
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
            
            // Set success message and redirect
            HttpSession session = request.getSession();
            session.setAttribute("successMessage", "Quiz updated successfully!");
            response.sendRedirect(request.getContextPath() + "/admin/quizzes");
            
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Error updating quiz: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/quiz/edit?id=" + quizIdStr);
        }
    }
}