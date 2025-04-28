<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Available Quizzes</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
    <jsp:include page="../includes/header.jsp" />
    <jsp:include page="../includes/navigation.jsp" />
    
    <div class="container">
        <h1>Available Quizzes</h1>
        
        <div class="filter-section">
            <form action="${pageContext.request.contextPath}/quiz/list" method="get" class="form-inline">
                <div class="form-group">
                    <label for="categoryFilter">Category:</label>
                    <select name="categoryId" id="categoryFilter" class="form-control">
                        <option value="">All Categories</option>
                        <c:forEach items="${categories}" var="category">
                            <option value="${category.id}" ${param.categoryId == category.id ? 'selected' : ''}>${category.name}</option>
                        </c:forEach>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="searchTitle">Search by Title:</label>
                    <input type="text" class="form-control" id="searchTitle" name="title" value="${param.title}" placeholder="Quiz title...">
                </div>
                
                <button type="submit" class="btn btn-secondary">Search</button>
                <a href="${pageContext.request.contextPath}/quiz/list" class="btn btn-outline-secondary">Reset</a>
            </form>
        </div>
        
        <c:choose>
            <c:when test="${empty quizzes}">
                <div class="alert alert-info">No quizzes found matching your criteria.</div>
            </c:when>
            <c:otherwise>
                <div class="row">
                    <c:forEach items="${quizzes}" var="quiz">
                        <div class="col-md-4">
                            <div class="card mb-4">
                                <div class="card-header">
                                    <h5 class="card-title">${quiz.title}</h5>
                                </div>
                                <div class="card-body">
                                    <p class="category-badge">${quiz.categoryName}</p>
                                    <p class="card-text">${quiz.description}</p>
                                    <ul class="quiz-details">
                                        <li><i class="fas fa-question-circle"></i> ${quiz.questionCount} questions</li>
                                        <li><i class="fas fa-clock"></i> ${quiz.timeLimit} minutes</li>
                                        <li><i class="fas fa-users"></i> ${quiz.attemptCount} attempts</li>
                                    </ul>
                                </div>
                                <div class="card-footer">
                                    <c:choose>
                                        <c:when test="${quiz.attempted}">
                                            <a href="${pageContext.request.contextPath}/quiz/my-results?quizId=${quiz.id}" class="btn btn-info">View My Results</a>
                                            <a href="${pageContext.request.contextPath}/quiz/take?id=${quiz.id}" class="btn btn-primary">Take Again</a>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="${pageContext.request.contextPath}/quiz/take?id=${quiz.id}" class="btn btn-primary btn-block">Start Quiz</a>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
                
                <!-- Pagination -->
                <c:if test="${totalPages > 1}">
                    <nav aria-label="Quiz pagination">
                        <ul class="pagination">
                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/quiz/list?page=${currentPage - 1}&categoryId=${param.categoryId}&title=${param.title}">Previous</a>
                            </li>
                            
                            <c:forEach begin="1" end="${totalPages}" var="pageNum">
                                <li class="page-item ${pageNum == currentPage ? 'active' : ''}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/quiz/list?page=${pageNum}&categoryId=${param.categoryId}&title=${param.title}">${pageNum}</a>
                                </li>
                            </c:forEach>
                            
                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/quiz/list?page=${currentPage + 1}&categoryId=${param.categoryId}&title=${param.title}">Next</a>
                            </li>
                        </ul>
                    </nav>
                </c:if>
            </c:otherwise>
        </c:choose>
    </div>
    
    <jsp:include page="../includes/footer.jsp" />
</body>
</html>