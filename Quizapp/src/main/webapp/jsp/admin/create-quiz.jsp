<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Create Quiz</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
</head>
<body>
    <jsp:include page="../includes/header.jsp" />
    <jsp:include page="../includes/admin-menu.jsp" />
    
    <div class="container">
        <h1>Create New Quiz</h1>
        
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">${errorMessage}</div>
        </c:if>
        
        <form action="${pageContext.request.contextPath}/admin/save-quiz" method="post" id="quizForm">
            <div class="form-group">
                <label for="title">Quiz Title:</label>
                <input type="text" class="form-control" id="title" name="title" required>
            </div>
            
            <div class="form-group">
                <label for="description">Description:</label>
                <textarea class="form-control" id="description" name="description" rows="3"></textarea>
            </div>
            
            <div class="form-group">
                <label for="category">Category:</label>
                <select class="form-control" id="category" name="categoryId" required>
                    <option value="">Select Category</option>
                    <c:forEach items="${categories}" var="category">
                        <option value="${category.id}">${category.name}</option>
                    </c:forEach>
                </select>
            </div>
            
            <div class="form-group">
                <label for="timeLimit">Time Limit (minutes):</label>
                <input type="number" class="form-control" id="timeLimit" name="timeLimit" min="1" value="30">
            </div>
            
            <div class="form-group">
                <label>Status:</label>
                <div class="form-check">
                    <input class="form-check-input" type="radio" name="active" id="active" value="true" checked>
                    <label class="form-check-label" for="active">Active</label>
                </div>
                <div class="form-check">
                    <input class="form-check-input" type="radio" name="active" id="inactive" value="false">
                    <label class="form-check-label" for="inactive">Inactive</label>
                </div>
            </div>
            
            <h3>Questions</h3>
            <div id="questions-container">
                <div class="question-block">
                    <div class="form-group">
                        <label>Question 1:</label>
                        <textarea class="form-control" name="questions[0].text" required></textarea>
                    </div>
                    
                    <div class="form-group">
                        <label>Options:</label>
                        <div class="option-group">
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="questions[0].correctOption" value="0" required>
                                <input type="text" class="form-control" name="questions[0].options[0]" placeholder="Option 1" required>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="questions[0].correctOption" value="1">
                                <input type="text" class="form-control" name="questions[0].options[1]" placeholder="Option 2" required>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="questions[0].correctOption" value="2">
                                <input type="text" class="form-control" name="questions[0].options[2]" placeholder="Option 3" required>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="questions[0].correctOption" value="3">
                                <input type="text" class="form-control" name="questions[0].options[3]" placeholder="Option 4" required>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <button type="button" class="btn btn-secondary" id="addQuestionBtn">Add Another Question</button>
            
            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Save Quiz</button>
                <a href="${pageContext.request.contextPath}/admin/manage-quizzes" class="btn btn-default">Cancel</a>
            </div>
        </form>
    </div>
    
    <script>
    $(document).ready(function() {
        let questionCount = 1;
        
        $('#addQuestionBtn').click(function() {
            questionCount++;
            const newQuestion = `
                <div class="question-block">
                    <hr>
                    <div class="form-group">
                        <label>Question ${questionCount}:</label>
                        <textarea class="form-control" name="questions[${questionCount-1}].text" required></textarea>
                    </div>
                    
                    <div class="form-group">
                        <label>Options:</label>
                        <div class="option-group">
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="questions[${questionCount-1}].correctOption" value="0" required>
                                <input type="text" class="form-control" name="questions[${questionCount-1}].options[0]" placeholder="Option 1" required>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="questions[${questionCount-1}].correctOption" value="1">
                                <input type="text" class="form-control" name="questions[${questionCount-1}].options[1]" placeholder="Option 2" required>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="questions[${questionCount-1}].correctOption" value="2">
                                <input type="text" class="form-control" name="questions[${questionCount-1}].options[2]" placeholder="Option 3" required>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="questions[${questionCount-1}].correctOption" value="3">
                                <input type="text" class="form-control" name="questions[${questionCount-1}].options[3]" placeholder="Option 4" required>
                            </div>
                        </div>
                    </div>
                    <button type="button" class="btn btn-danger remove-question">Remove Question</button>
                </div>
            `;
            
            $('#questions-container').append(newQuestion);
        });
        
        // Event delegation for removing questions
        $(document).on('click', '.remove-question', function() {
            $(this).closest('.question-block').remove();
            // Re-index the remaining questions
            $('.question-block').each(function(index) {
                const i = index;
                $(this).find('label:first').text('Question ' + (i+1) + ':');
                $(this).find('textarea').attr('name', 'questions['+i+'].text');
                $(this).find('input[type="radio"]').attr('name', 'questions['+i+'].correctOption');
                $(this).find('input[type="text"]').each(function(optIndex) {
                    $(this).attr('name', 'questions['+i+'].options['+optIndex+']');
                });
            });
            questionCount = $('.question-block').length;
        });
    });
    </script>
    
    <jsp:include page="../includes/footer.jsp" />
</body>
</html>