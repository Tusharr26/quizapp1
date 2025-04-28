<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${currentQuiz.title} - Quiz</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
    <jsp:include page="/jsp/include/header.jsp"/>
    
    <div class="container">
        <div class="quiz-container">
            <h1>${currentQuiz.title}</h1>
            <p>${currentQuiz.description}</p>
            
            <div class="quiz-progress">
                <div class="progress-bar">
                    <div class="progress" style="width: ${(currentQuestionIndex / questions.size()) * 100}%"></div>
                </div>
                <p>Question ${currentQuestionIndex + 1} of ${questions.size()}</p>
            </div>
            
            <c:if test="${feedbackOnly}">
                <div class="feedback-container ${isCorrect ? 'correct' : 'incorrect'}">
                    <h3>${isCorrect ? 'Correct!' : 'Incorrect!'}</h3>
                    <p>Your answer: ${selectedOption.text}</p>
                    <c:if test="${!isCorrect}">
                        <p>The correct answer was: 
                            <c:forEach items="${currentQuestion.options}" var="option">
                                <c:if test="${option.correct}">
                                    ${option.text}
                                </c:if>
                            </c:forEach>
                        </p>
                    </c:if>
                    <button class="btn primary" onclick="window.location.href='${pageContext.request.contextPath}/quiz/take'">
                        Next Question
                    </button>
                </div>
            </c:if>
            
            <c:if test="${quizCompleted}">
                <div class="quiz-completed">
                    <h2>Quiz Completed!</h2>
                    <p>You've answered all the questions.</p>
                    <button class="btn primary" onclick="window.location.href='${pageContext.request.contextPath}/quiz/finish'">
                        View Results
                    </button>
                </div>
            </c:if>
            
            <c:if test="${!feedbackOnly && !quizCompleted}">
                <div class="question-card">
                    <h2>${currentQuestion.text}</h2>
                    
                    <form action="${pageContext.request.contextPath}/quiz/submit" method="post">
                        <div class="options-container">
                            <c:forEach items="${currentQuestion.options}" var="option">
                                <div class="option">
                                    <input type="radio" id="option-${option.id}" name="selectedOption" value="${option.id}" required>
                                    <label for="option-${option.id}">${option.text}</label>
                                </div>
                            </c:forEach>
                        </div>
                        
                        <c:if test="${not empty errorMessage}">
                            <div class="error-message">${errorMessage}</div>
                        </c:if>
                        
                        <div class="button-container">
                            <button type="submit" class="btn primary">Submit Answer</button>
                        </div>
                    </form>
                </div>
            </c:if>
        </div>
    </div>
    
    <jsp:include page="/jsp/include/footer.jsp" />
</body>