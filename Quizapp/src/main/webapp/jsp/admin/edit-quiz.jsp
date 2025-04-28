<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Quiz</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
</head>
<body>
    <jsp:include page="../includes/header.jsp" />
    <jsp:include page="../includes/admin-menu.jsp" />
    
    <div class="container">
        <h1>Edit Quiz</h1>
        
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">${errorMessage}</div>
        </c:if>
        
        <form action="${pageContext.request.contextPath}/admin/update-quiz" method="post" id="quizForm">
            <input type="hidden" name="quizId" value="${quiz.id}">
            
            <div class="form-group">
                <label for="title">Quiz Title:</label>
                <input type="text" class="form-control" id="title" name="title" required value="${quiz.title}">
            </div>
            
            <div class="form-group">
                <label for="description">Description:</label>
                <textarea class="form-control" id="description" name="description" rows="3">${quiz.description}</textarea>
            </div>
            
            <div class="form-group">
                <label for="category">Category:</label>
                <select class="form-control" id="category" name="categoryId" required>
                    <option value="">Select Category</option>
                    <c:forEach items="${categories}" var="category">
                        <option value="${category.id}" <c:if test="${category.id == quiz.categoryId}">selected</c:if>>${category.name}</option>
                    </c:forEach>
                </select>
            </div>
            
            <div class="form-group">
                <label for="timeLimit">Time Limit (minutes):</label>
                <input type="number" class="form-control" id="timeLimit" name="timeLimit" min="1" value="${quiz.timeLimit}">
            </div>
            
            <div class="form-group">
                <label>Status:</label>
                <div class="form-check">
                    <input class="form-check-input" type="radio" name="active" id="active" value="true" <c:if test="${quiz.active}">checked</c:if>>
                    <label class="form-check-label" for="active">Active</label>
                </div>
                <div class="form-check">
                    <input class="form-check-input" type="radio" name="active" id="inactive" value="false" <c:if test="${!quiz.active}">checked</c:if>>
                    <label class="form-check-label" for="inactive">Inactive</label>
                </div>
            </div>
            
            <h3>Questions</h3>
            <div id="questions-container">
                <c:forEach items="${quiz.questions}" var="question" varStatus="status">
                    <div class="question-block">
                        <c:if test="${status.index > 0}"><hr></c:if>
                        <div class="form-group">
                            <label>Question ${status.index + 1}:</label>
                            <textarea class="form-control" name="questions[${status.index}].text" required>${question.text}</textarea>
                            <input type="hidden" name="questions[${status.index}].id" value="${question.id}">
                        </div>
                        
                        <div class="form-group">
                            <label>Options:</label>
                            <div class="option-group">
                                <c:forEach items="${question.options}" var="option" varStatus="optStatus">
                                    <div class="form-check">
                                        <input class="form-check-input" type="radio" name="questions[${status.index}].correctOption" value="${optStatus.index}" 
                                            <c:if test="${question.correctOption == optStatus.index}">checked</c:if> required>
                                        <input type="text" class="form-control" name="questions[${status.index}].options[${optStatus.index}]" 
                                            value="${option}" placeholder="Option ${optStatus.index + 1}" required>
                                        <input type="hidden" name="questions[${status.index}].optionIds[${optStatus.index}]" value="${question.optionIds[optStatus.index]}">
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                        <c:if test="${status.index > 0}">
                            <button type="button" class="btn btn-danger remove-question">Remove Question</button>
                        </c:if>
                    </div>
                </c:forEach>
            </div>
            
            <button type="button" class="btn btn-secondary" id="addQuestionBtn">Add Another Question</button>
            
            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Update Quiz</button>
                <a href="${pageContext.request.contextPath}/admin/manage-quizzes" class="btn btn-default">Cancel</a>
            </div>
        </form>
    </div>
    
    <script>
    $(document).ready(function() {
        let questionCount = ${quiz.questions.size()};
        
        $('#addQuestionBtn').click(function() {
            const newQuestion = `
                <div class="question-block">
                    <hr>
                    <div class="form-group">
                        <label>Question ${questionCount + 1}:</label>
                        <textarea class="form-control" name="questions[${questionCount}].text" required></textarea>
                        <input type="hidden" name="questions[${questionCount}].id" value="0">
                    </div>
                    
                    <div class="form-group">
                        <label>Options:</label>
                        <div class="option-group">
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="questions[${questionCount}].correctOption" value="0" required>
                                <input type="text" class="form-control" name="questions[${questionCount}].options[0]" placeholder="Option 1" required>
                                <input type="hidden" name="questions[${questionCount}].optionIds[0]" value="0">
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="questions[${questionCount}].correctOption" value="1">
                                <input type="text" class="form-control" name="questions[${questionCount}].options[1]" placeholder="Option 2" required>
                                <input type="hidden" name="questions[${questionCount}].optionIds[1]" value="0">
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="questions[${questionCount}].correctOption" value="2">
                                <input type="text" class="form-control" name="questions[${questionCount}].options[2]" placeholder="Option 3" required>
                                <input type="hidden" name="questions[${questionCount}].optionIds[2]" value="0">
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="questions[${questionCount}].correctOption" value="3">
                                <input type="text" class="form-control" name="questions[${questionCount}].options[3]" placeholder="Option 4" required>
                                <input type="hidden" name="questions[${questionCount}].optionIds[3]" value="0">
                            </div>
                        </div>
                    </div>
                    <button type="button" class="btn btn-danger remove-question">Remove Question</button>
                </div>
            `;
            
            $('#questions-container').append(newQuestion);
            questionCount++;
        });
        
        // Event delegation for removing questions
        $(document).on('click', '.remove-question', function() {
            $(this).closest('.question-block').remove();
            // Re-index the remaining questions for display purposes
            $('.question-block').each(function(index) {
                $(this).find('label:first').text('Question ' + (index+1) + ':');
            });
            questionCount = $('.question-block').length;
        });
    });
    </script>
    
    <jsp:include page="../includes/footer.jsp" />
</body>
</html>