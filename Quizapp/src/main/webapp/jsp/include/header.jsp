<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<header class="site-header">
    <div class="container">
        <div class="logo">
            <a href="${pageContext.request.contextPath}/">
                <img src="${pageContext.request.contextPath}/images/logo.svg" alt="Quiz App">
                <span>Quiz App</span>
            </a>
        </div>
        
        <nav class="main-nav">
            <ul>
                <li><a href="${pageContext.request.contextPath}/" class="${pageContext.request.servletPath eq '/index.jsp' ? 'active' : ''}">Home</a></li>
                <li><a href="${pageContext.request.contextPath}/quizzes" class="${pageContext.request.servletPath eq '/WEB-INF/jsp/quiz/list.jsp' ? 'active' : ''}">Quizzes</a></li>
                <li><a href="${pageContext.request.contextPath}/leaderboard" class="${pageContext.request.servletPath eq '/WEB-INF/jsp/leaderboard.jsp' ? 'active' : ''}">Leaderboard</a></li>
                <li><a href="${pageContext.request.contextPath}/about" class="${pageContext.request.servletPath eq '/WEB-INF/jsp/about.jsp' ? 'active' : ''}">About</a></li>
            </ul>
        </nav>
        
        <div class="auth-nav">
            <c:choose>
                <c:when test="${empty sessionScope.user}">
                    <a href="${pageContext.request.contextPath}/auth/login" class="btn secondary">Sign In</a>
                    <a href="${pageContext.request.contextPath}/auth/register" class="btn primary">Register</a>
                </c:when>
                <c:otherwise>
                    <div class="user-menu">
                        <button class="user-menu-toggle">
                            <span class="user-name">${sessionScope.user.username}</span>
                            <span class="avatar">
                                <c:choose>
                                    <c:when test="${not empty sessionScope.user.avatarUrl}">
                                        <img src="${sessionScope.user.avatarUrl}" alt="${sessionScope.user.username}">
                                    </c:when>
                                    <c:otherwise>
                                        <div class="default-avatar">${sessionScope.user.username.substring(0, 1).toUpperCase()}</div>
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </button>
                        <div class="dropdown-menu">
                            <a href="${pageContext.request.contextPath}/dashboard">
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
                                    <polyline points="9 22 9 12 15 12 15 22"></polyline>
                                </svg>
                                Dashboard
                            </a>
                            <a href="${pageContext.request.contextPath}/profile">
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                                    <circle cx="12" cy="7" r="4"></circle>
                                </svg>
                                Profile
                            </a>
                            <a href="${pageContext.request.contextPath}/my-quizzes">
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                                    <polyline points="14 2 14 8 20 8"></polyline>
                                    <line x1="16" y1="13" x2="8" y2="13"></line>
                                    <line x1="16" y1="17" x2="8" y2="17"></line>
                                    <polyline points="10 9 9 9 8 9"></polyline>
                                </svg>
                                My Quizzes
                            </a>
                            <a href="${pageContext.request.contextPath}/auth/logout">
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
                                    <polyline points="16 17 21 12 16 7"></polyline>
                                    <line x1="21" y1="12" x2="9" y2="12"></line>
                                </svg>
                                Sign Out
                            </a>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
        
        <button class="mobile-menu-toggle">
            <span></span>
            <span></span>
            <span></span>
        </button>
    </div>
</header>

<script>
    $(document).ready(function() {
        // Mobile menu toggle
        $('.mobile-menu-toggle').click(function() {
            $(this).toggleClass('active');
            $('.main-nav').toggleClass('active');
        });
        
        // User dropdown menu
        $('.user-menu-toggle').click(function(e) {
            e.stopPropagation();
            $('.dropdown-menu').toggleClass('active');
        });
        
        $(document).click(function() {
            $('.dropdown-menu').removeClass('active');
        });
    });
</script>