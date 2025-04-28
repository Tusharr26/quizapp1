<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Quiz App</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
    <jsp:include page="/jsp/include/header.jsp" />

    <div class="container">
        <div class="auth-container">
            <div class="auth-form">
                <h1>Sign In</h1>
                <p>Welcome back! Please login to your account.</p>
                
                <c:if test="${not empty errorMessage}">
                    <div class="error-message">${errorMessage}</div>
                </c:if>
                
                <form action="${pageContext.request.contextPath}/auth/login" method="post">
                    <div class="form-group">
                        <label for="username">Username</label>
                        <input type="text" id="username" name="username" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" required>
                    </div>
                    
                    <div class="form-actions">
                        <button type="submit" class="btn primary">Sign In</button>
                    </div>
                </form>
                
                <div class="auth-links">
                    <p>Don't have an account? <a href="${pageContext.request.contextPath}/auth/register">Create one</a></p>
                    <p><a href="${pageContext.request.contextPath}/auth/forgot-password">Forgot your password?</a></p>
                </div>
            </div>
            
            <div class="auth-image">
                <img src="${pageContext.request.contextPath}/images/login-illustration.svg" alt="Login">
                <div class="auth-overlay">
                    <h2>Test Your Knowledge</h2>
                    <p>Access hundreds of quizzes and challenge yourself!</p>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="/jsp/include/footer.jsp" />
    
    <script>
        $(document).ready(function() {
            // Focus on username field
            $('#username').focus();
            
            // Simple form validation
            $('form').submit(function(e) {
                let username = $('#username').val().trim();
                let password = $('#password').val().trim();
                
                if (username === '' || password === '') {
                    e.preventDefault();
                    $('.error-message').text('Please enter both username and password').show();
                    return false;
                }
            });
        });
    </script>
</body>
</html>