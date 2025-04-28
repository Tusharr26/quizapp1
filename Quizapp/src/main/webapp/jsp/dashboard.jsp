<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Quiz App</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
    <jsp:include page="/jsp/include/header.jsp" />

    <div class="container">
        <div class="dashboard">
            <div class="welcome-section">
                <h1>Welcome, ${user.username}!</h1>
                <p>Ready to test your knowledge? Browse available quizzes or check your progress below.</p>
                
                <div class="action-buttons">
                    <a href="${pageContext.request.contextPath}/quizzes" class="btn primary">Browse Quizzes</a>
                    <a href="${pageContext.request.contextPath}/profile" class="btn secondary">My Profile</a>
                </div>
            </div>
            
            <c:if test="${not empty errorMessage}">
                <div class="error-message">${errorMessage}</div>
            </c:if>
            
            <div class="dashboard-sections">
                <div class="dashboard-section">
                    <h2>Recent Quizzes</h2>
                    <div class="quiz-list">
                        <c:choose>
                            <c:when test="${not empty recentQuizzes}">
                                <c:forEach items="${recentQuizzes}" var="quiz">
                                    <div class="quiz-card">
                                        <div class="quiz-info">
                                            <h3>${quiz.title}</h3>
                                            <p>${quiz.description}</p>
                                            <div class="quiz-meta">
                                                <span class="category">${quiz.category}</span>
                                                <span class="difficulty ${quiz.difficulty.toLowerCase()}">${quiz.difficulty}</span>
                                                <span class="questions">${quiz.questionCount} Questions</span>
                                            </div>
                                        </div>
                                        <a href="${pageContext.request.contextPath}/quiz/start/${quiz.id}" class="btn primary">Start Quiz</a>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <div class="empty-state">
                                    <p>No quizzes available right now.</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                
                <div class="dashboard-section">
                    <h2>My Quiz History</h2>
                    <div class="attempt-list">
                        <c:choose>
                            <c:when test="${not empty userAttempts}">
                                <table class="attempts-table">
                                    <thead>
                                        <tr>
                                            <th>Quiz</th>
                                            <th>Date</th>
                                            <th>Score</th>
                                            <th>Status</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${userAttempts}" var="attempt">
                                            <tr>
                                                <td>${attempt.quizTitle}</td>
                                                <td><fmt:formatDate value="${attempt.attemptDate}" pattern="MMM dd, yyyy HH:mm" /></td>
                                                <td>${attempt.score}/${attempt.totalQuestions}</td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${attempt.completed}">
                                                            <span class="status completed">Completed</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="status in-progress">In Progress</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${attempt.completed}">
                                                            <a href="${pageContext.request.contextPath}/quiz/finish/${attempt.id}" class="btn small">View Results</a>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <a href="${pageContext.request.contextPath}/quiz/resume/${attempt.id}" class="btn small primary">Resume</a>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <div class="empty-state">
                                    <p>You haven't taken any quizzes yet.</p>
                                    <a href="${pageContext.request.contextPath}/quizzes" class="btn primary">Find Quizzes</a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="/jsp/include/footer.jsp" />
</body>
</html>