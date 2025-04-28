<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - Quiz App</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
    <jsp:include page="/jsp/include/header.jsp" />

    <div class="container">
        <div class="auth-container">
            <div class="auth-form">
                <h1>Create Account</h1>
                <p>Join our community and start exploring quizzes!</p>
                
                <c:if test="${not empty errorMessage}">
                    <div class="error-message">${errorMessage}</div>
                </c:if>
                
                <form action="${pageContext.request.contextPath}/auth/register" method="post" id="registerForm">
                    <div class="form-group">
                        <label for="username">Username</label>
                        <input type="text" id="username" name="username" value="${username}" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="email">Email Address</label>
                        <input type="email" id="email" name="email" value="${email}" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" required>
                        <div class="password-strength-meter">
                            <div class="meter-bar"></div>
                        </div>
                        <div class="password-requirements">
                            Password should be at least 8 characters and include a mix of letters, numbers, and symbols.
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="confirmPassword">Confirm Password</label>
                        <input type="password" id="confirmPassword" name="confirmPassword" required>
                    </div>
                    
                    <div class="form-check">
                        <input type="checkbox" id="termsAgreement" name="termsAgreement" required>
                        <label for="termsAgreement">I agree to the <a href="${pageContext.request.contextPath}/terms" target="_blank">Terms of Service</a> and <a href="${pageContext.request.contextPath}/privacy" target="_blank">Privacy Policy</a></label>
                    </div>
                    
                    <div class="form-actions">
                        <button type="submit" class="btn primary">Create Account</button>
                    </div>
                </form>
                
                <div class="auth-links">
                    <p>Already have an account? <a href="${pageContext.request.contextPath}/auth/login">Sign In</a></p>
                </div>
            </div>
            
            <div class="auth-image">
                <img src="${pageContext.request.contextPath}/images/register-illustration.svg" alt="Register">
                <div class="auth-overlay">
                    <h2>Join Our Community</h2>
                    <p>Create, take, and share quizzes on any topic!</p>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="/jsp/include/footer.jsp" />
    
    <script>
        $(document).ready(function() {
            // Focus on username field
            $('#username').focus();
            
            // Password strength meter
            $('#password').on('input', function() {
                let password = $(this).val();
                let strength = 0;
                
                if (password.length >= 8) strength += 1;
                if (password.match(/[a-z]/) && password.match(/[A-Z]/)) strength += 1;
                if (password.match(/\d+/)) strength += 1;
                if (password.match(/[^a-zA-Z0-9]/)) strength += 1;
                
                let meterBar = $('.meter-bar');
                meterBar.removeClass('weak medium strong very-strong');
                
                if (password.length === 0) {
                    meterBar.css('width', '0%');
                } else if (strength === 1) {
                    meterBar.addClass('weak').css('width', '25%');
                } else if (strength === 2) {
                    meterBar.addClass('medium').css('width', '50%');
                } else if (strength === 3) {
                    meterBar.addClass('strong').css('width', '75%');
                } else if (strength === 4) {
                    meterBar.addClass('very-strong').css('width', '100%');
                }
            });
            
            // Form validation
            $('#registerForm').submit(function(e) {
                let username = $('#username').val().trim();
                let email = $('#email').val().trim();
                let password = $('#password').val();
                let confirmPassword = $('#confirmPassword').val();
                let termsAgreement = $('#termsAgreement').is(':checked');
                let error = '';
                
                if (username === '') {
                    error = 'Username is required';
                } else if (email === '') {
                    error = 'Email is required';
                } else if (!isValidEmail(email)) {
                    error = 'Please enter a valid email address';
                } else if (password === '') {
                    error = 'Password is required';
                } else if (password.length < 8) {
                    error = 'Password must be at least 8 characters long';
                } else if (password !== confirmPassword) {
                    error = 'Passwords do not match';
                } else if (!termsAgreement) {
                    error = 'You must agree to the Terms of Service and Privacy Policy';
                }
                
                if (error !== '') {
                    e.preventDefault();
                    $('.error-message').text(error).show();
                    return false;
                }
            });
            
            function isValidEmail(email) {
                let regex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
                return regex.test(email);
            }
        });
    </script>
</body>
</html>