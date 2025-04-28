<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Welcome to Quiz App</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
    <jsp:include page="include/header.jsp" />

    <div class="hero-section">
        <div class="container">
            <div class="hero-content">
                <h1>Test Your Knowledge with Interactive Quizzes</h1>
                <p>Challenge yourself with quizzes on various topics, track your progress, and compete with friends.</p>
                <div class="hero-actions">
                    <c:choose>
                        <c:when test="${empty sessionScope.user}">
                            <a href="${pageContext.request.contextPath}/auth/login" class="btn primary">Sign In</a>
                            <a href="${pageContext.request.contextPath}/auth/register" class="btn secondary">Create Account</a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/dashboard" class="btn primary">My Dashboard</a>
                            <a href="${pageContext.request.contextPath}/quizzes" class="btn secondary">Browse Quizzes</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div class="hero-image">
                <img src="${pageContext.request.contextPath}/images/quiz-illustration.svg" alt="Quiz Illustration">
            </div>
        </div>
    </div>

    <div class="container">
        <section class="features-section">
            <h2>Why Choose Our Quiz Platform?</h2>
            <div class="features-grid">
                <div class="feature-card">
                    <div class="feature-icon">
                        <img src="${pageContext.request.contextPath}/images/icons/diverse.svg" alt="Diverse Topics">
                    </div>
                    <h3>Diverse Topics</h3>
                    <p>Explore quizzes across various categories including science, history, programming, and more.</p>
                </div>
                <div class="feature-card">
                    <div class="feature-icon">
                        <img src="${pageContext.request.contextPath}/images/icons/track.svg" alt="Track Progress">
                    </div>
                    <h3>Track Progress</h3>
                    <p>Monitor your performance over time and see how you're improving in different subjects.</p>
                </div>
                <div class="feature-card">
                    <div class="feature-icon">
                        <img src="${pageContext.request.contextPath}/images/icons/compete.svg" alt="Compete">
                    </div>
                    <h3>Compete & Compare</h3>
                    <p>Compare your scores with friends and see how you rank on our leaderboards.</p>
                </div>
                <div class="feature-card">
                    <div class="feature-icon">
                        <img src="${pageContext.request.contextPath}/images/icons/anywhere.svg" alt="Anywhere">
                    </div>
                    <h3>Quiz Anywhere</h3>
                    <p>Access our platform on any device - desktop, tablet, or mobile.</p>
                </div>
            </div>
        </section>

        <section class="popular-quizzes">
            <h2>Popular Quizzes</h2>
            <div class="quiz-cards">
                <c:forEach items="${popularQuizzes}" var="quiz">
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
                        <c:choose>
                            <c:when test="${empty sessionScope.user}">
                                <a href="${pageContext.request.contextPath}/auth/login" class="btn primary">Sign In to Start</a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/quiz/start/${quiz.id}" class="btn primary">Start Quiz</a>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:forEach>
            </div>
            <div class="view-all">
                <a href="${pageContext.request.contextPath}/quizzes" class="btn secondary">View All Quizzes</a>
            </div>
        </section>

        <section class="cta-section">
            <div class="cta-content">
                <h2>Ready to Challenge Your Knowledge?</h2>
                <p>Join thousands of users who are expanding their knowledge and having fun with our quizzes.</p>
                <c:choose>
                    <c:when test="${empty sessionScope.user}">
                        <a href="${pageContext.request.contextPath}/auth/register" class="btn primary">Get Started Now</a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/quizzes" class="btn primary">Find New Quizzes</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </section>
    </div>

    <jsp:include page="include/footer.jsp" />
    
    <script>
        $(document).ready(function() {
            // Animation for features on scroll
            $(window).scroll(function() {
                $('.feature-card').each(function() {
                    var position = $(this).offset().top;
                    var scroll = $(window).scrollTop();
                    var windowHeight = $(window).height();
                    
                    if (scroll > position - windowHeight + 100) {
                        $(this).addClass('animated');
                    }
                });
            });
        });
    </script>
</body>
</html>