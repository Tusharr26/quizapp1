<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<footer class="site-footer">
    <div class="container">
        <div class="footer-content">
            <div class="footer-logo">
                <img src="${pageContext.request.contextPath}/images/logo.svg" alt="Quiz App">
                <h3>Quiz App</h3>
                <p>Test your knowledge with interactive quizzes.</p>
            </div>
            
            <div class="footer-links">
                <div class="footer-links-column">
                    <h4>Quick Links</h4>
                    <ul>
                        <li><a href="${pageContext.request.contextPath}/">Home</a></li>
                        <li><a href="${pageContext.request.contextPath}/quizzes">Browse Quizzes</a></li>
                        <li><a href="${pageContext.request.contextPath}/leaderboard">Leaderboard</a></li>
                        <li><a href="${pageContext.request.contextPath}/about">About</a></li>
                    </ul>
                </div>
                
                <div class="footer-links-column">
                    <h4>Account</h4>
                    <ul>
                        <c:choose>
                            <c:when test="${empty sessionScope.user}">
                                <li><a href="${pageContext.request.contextPath}/auth/login">Sign In</a></li>
                                <li><a href="${pageContext.request.contextPath}/auth/register">Register</a></li>
                            </c:when>
                            <c:otherwise>
                                <li><a href="${pageContext.request.contextPath}/dashboard">Dashboard</a></li>
                                <li><a href="${pageContext.request.contextPath}/profile">Profile</a></li>
                                <li><a href="${pageContext.request.contextPath}/my-quizzes">My Quizzes</a></li>
                                <li><a href="${pageContext.request.contextPath}/auth/logout">Sign Out</a></li>
                            </c:otherwise>
                        </c:choose>
                    </ul>
                </div>
                
                <div class="footer-links-column">
                    <h4>Support</h4>
                    <ul>
                        <li><a href="${pageContext.request.contextPath}/contact">Contact Us</a></li>
                        <li><a href="${pageContext.request.contextPath}/faq">FAQ</a></li>
                        <li><a href="${pageContext.request.contextPath}/terms">Terms of Service</a></li>
                        <li><a href="${pageContext.request.contextPath}/privacy">Privacy Policy</a></li>
                    </ul>
                </div>
            </div>
            
            <div class="footer-newsletter">
                <h4>Stay Updated</h4>
                <p>Subscribe to our newsletter for updates and new quizzes.</p>
                <form action="${pageContext.request.contextPath}/subscribe" method="post">
                    <div class="newsletter-form">
                        <input type="email" name="email" placeholder="Enter your email" required>
                        <button type="submit" class="btn primary">Subscribe</button>
                    </div>
                </form>
                <div class="social-links">
                    <a href="#" target="_blank" rel="noopener noreferrer" aria-label="Facebook">
                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M18 2h-3a5 5 0 0 0-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 0 1 1-1h3z"></path>
                        </svg>
                    </a>
                    <a href="#" target="_blank" rel="noopener noreferrer" aria-label="Twitter">
                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M23 3a10.9 10.9 0 0 1-3.14 1.53 4.48 4.48 0 0 0-7.86 3v1A10.66 10.66 0 0 1 3 4s-4 9 5 13a11.64 11.64 0 0 1-7 2c9 5 20 0 20-11.5a4.5 4.5 0 0 0-.08-.83A7.72 7.72 0 0 0 23 3z"></path>
                        </svg>
                    </a>
                    <a href="#" target="_blank" rel="noopener noreferrer" aria-label="Instagram">
                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <rect x="2" y="2" width="20" height="20" rx="5" ry="5"></rect>
                            <path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z"></path>
                            <line x1="17.5" y1="6.5" x2="17.51" y2="6.5"></line>
                        </svg>
                    </a>
                </div>
            </div>
        </div>
        
        <div class="footer-bottom">
            <p>&copy; <%= java.time.Year.now().getValue() %> Quiz App. All rights reserved.</p>
        </div>
    </div>
</footer>