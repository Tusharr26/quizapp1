<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Browse Quizzes - Quiz App</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
    <jsp:include page="../includes/header.jsp" />

    <div class="container">
        <div class="quizzes-page">
            <div class="page-header">
                <h1>Browse Quizzes</h1>
                <p>Find and take quizzes on various topics</p>
            </div>
            
            <div class="filters-container">
                <form id="filter-form" action="${pageContext.request.contextPath}/quizzes" method="get">
                    <div class="filters">
                        <div class="filter-group">
                            <label for="category">Category:</label>
                            <select name="category" id="category" onchange="this.form.submit()">
                                <option value="">All Categories</option>
                                <c:forEach items="${categories}" var="category">
                                    <option value="${category}" ${category eq selectedCategory ? 'selected' : ''}>${category}</option>
                                </c:forEach>
                            </select>
                        </div>
                        
                        <div class="filter-group">
                            <label for="difficulty">Difficulty:</label>
                            <select name="difficulty" id="difficulty" onchange="this.form.submit()">
                                <option value="">All Difficulties</option>
                                <option value="Easy" ${selectedDifficulty eq 'Easy' ? 'selected' : ''}>Easy</option>
                                <option value="Medium" ${selectedDifficulty eq 'Medium' ? 'selected' : ''}>Medium</option>
                                <option value="Hard" ${selectedDifficulty eq 'Hard' ? 'selected' : ''}>Hard</option>
                            </select>
                        </div>
                        
                        <div class="filter-group search-group">
                            <input type="text" name="search" id="search" placeholder="Search quizzes..." value="${searchQuery}">
                            <button type="submit" class="btn primary">Search</button>
                        </div>
                    </div>
                </form>
            </div>
            
            <c:if test="${not empty selectedCategory || not empty selectedDifficulty || not empty searchQuery}">
                <div class="active-filters">
                    <span>Active filters:</span>
                    <c:if test="${not empty selectedCategory}">
                        <span class="filter-tag">
                            Category: ${selectedCategory}
                            <a href="${pageContext.request.contextPath}/quizzes?difficulty=${selectedDifficulty}&search=${searchQuery}" class="remove-filter">×</a>
                        </span>
                    </c:if>
                    <c:if test="${not empty selectedDifficulty}">
                        <span class="filter-tag">
                            Difficulty: ${selectedDifficulty}
                            <a href="${pageContext.request.contextPath}/quizzes?category=${selectedCategory}&search=${searchQuery}" class="remove-filter">×</a>
                        </span>
                    </c:if>
                    <c:if test="${not empty searchQuery}">
                        <span class="filter-tag">
                            Search: "${searchQuery}"
                            <a href="${pageContext.request.contextPath}/quizzes?category=${selectedCategory}&difficulty=${selectedDifficulty}" class="remove-filter">×</a>
                        </span>
                    </c:if>
                    <a href="${pageContext.request.contextPath}/quizzes" class="clear-all">Clear All</a>
                </div>
            </c:if>
            
            <div class="quizzes-list">
                <c:choose>
                    <c:when test="${not empty quizzes}">
                        <c:forEach items="${quizzes}" var="quiz">
                            <div class="quiz-card">
                                <div class="quiz-info">
                                    <h3>${quiz.title}</h3>
                                    <p>${quiz.description}</p>
                                    <div class="quiz-meta">
                                        <span class="category">${quiz.category}</span>
                                        <span class="difficulty ${quiz.difficulty.toLowerCase()}">${quiz.difficulty}</span>
                                        <span class="questions">${quiz.questionCount} Questions</span>
                                        <c:if test="${not empty quiz.createdBy}">
                                            <span class="author">By: ${quiz.createdBy}</span>
                                        </c:if>
                                        <span class="date">
                                            <fmt:formatDate value="${quiz.createdDate}" pattern="MMM dd, yyyy" />
                                        </span>
                                    </div>
                                </div>
                                <div class="quiz-actions">
                                    <a href="${pageContext.request.contextPath}/quiz/start/${quiz.id}" class="btn primary">Start Quiz</a>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <div class="empty-icon">
                                <img src="${pageContext.request.contextPath}/images/empty-state.svg" alt="No quizzes found">
                            </div>
                            <h3>No quizzes found</h3>
                            <p>Try adjusting your filters or check back later for new quizzes.</p>
                            <a href="${pageContext.request.contextPath}/quizzes" class="btn primary">Clear Filters</a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
            
            <c:if test="${totalPages > 1}">
                <div class="pagination">
                    <c:if test="${currentPage > 1}">
                        <a href="${pageContext.request.contextPath}/quizzes?page=${currentPage - 1}&category=${selectedCategory}&difficulty=${selectedDifficulty}&search=${searchQuery}" class="btn page-btn prev">Previous</a>
                    </c:if>
                    
                    <c:forEach begin="1" end="${totalPages}" var="pageNum">
                        <a href="${pageContext.request.contextPath}/quizzes?page=${pageNum}&category=${selectedCategory}&difficulty=${selectedDifficulty}&search=${searchQuery}" class="btn page-btn ${pageNum == currentPage ? 'active' : ''}">${pageNum}</a>
                    </c:forEach>
                    
                    <c:if test="${currentPage < totalPages}">
                        <a href="${pageContext.request.contextPath}/quizzes?page=${currentPage + 1}&category=${selectedCategory}&difficulty=${selectedDifficulty}&search=${searchQuery}" class="btn page-btn next">Next</a>
                    </c:if>
                </div>
            </c:if>
        </div>
    </div>

    <jsp:include page="../includes/footer.jsp" />
    
    <script>
        $(document).ready(function() {
            // Fade in quiz cards
            $('.quiz-card').each(function(index) {
                $(this).delay(100 * index).animate({opacity: 1}, 500);
            });
        });
    </script>
</body>
</html>