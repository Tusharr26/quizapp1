<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Manage Quizzes</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
</head>
<body>
    <jsp:include page="../includes/header.jsp" />
    <jsp:include page="../includes/admin-menu.jsp" />
    
    <div class="container">
        <h1>Manage Quizzes</h1>
        
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success">${successMessage}</div>
        </c:if>
        
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">${errorMessage}</div>
        </c:if>
        
        <div class="actions-bar">
            <a href="${pageContext.request.contextPath}/admin/create-quiz" class="btn btn-primary">Create New Quiz</a>
        </div>
        
        <div class="filter-section">
            <form action="${pageContext.request.contextPath}/admin/manage-quizzes" method="get" class="form-inline">
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
                    <label for="statusFilter">Status:</label>
                    <select name="status" id="statusFilter" class="form-control">
                        <option value="">All</option>
                        <option value="active" ${param.status == 'active' ? 'selected' : ''}>Active</option>
                        <option value="inactive" ${param.status == 'inactive' ? 'selected' : ''}>Inactive</option>
                    </select>
                </div>
                
                <button type="submit" class="btn btn-secondary">Filter</button>
                <a href="${pageContext.request.contextPath}/admin/manage-quizzes" class="btn btn-outline-secondary">Reset</a>
            </form>
        </div>
        
        <c:choose>
            <c:when test="${empty quizzes}">
                <div class="alert alert-info">No quizzes found.</div>
            </c:when>
            <c:otherwise>
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Title</th>
                            <th>Category</th>
                            <th>Questions</th>
                            <th>Time Limit</th>
                            <th>Created Date</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${quizzes}" var="quiz">
                            <tr>
                                <td>${quiz.id}</td>
                                <td>${quiz.title}</td>
                                <td>${quiz.categoryName}</td>
                                <td>${quiz.questionCount}</td>
                                <td>${quiz.timeLimit} minutes</td>
                                <td><fmt:formatDate value="${quiz.createdDate}" pattern="MMM dd, yyyy" /></td>
                                <td>
                                    <span class="badge ${quiz.active ? 'badge-success' : 'badge-secondary'}">
                                        ${quiz.active ? 'Active' : 'Inactive'}
                                    </span>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <a href="${pageContext.request.contextPath}/admin/edit-quiz?id=${quiz.id}" class="btn btn-sm btn-primary">Edit</a>
                                        <a href="${pageContext.request.contextPath}/admin/toggle-quiz-status?id=${quiz.id}" 
                                           class="btn btn-sm ${quiz.active ? 'btn-warning' : 'btn-success'}"
                                           onclick="return confirm('Are you sure you want to ${quiz.active ? 'deactivate' : 'activate'} this quiz?')">
                                            ${quiz.active ? 'Deactivate' : 'Activate'}
                                        </a>
                                        <a href="${pageContext.request.contextPath}/admin/delete-quiz?id=${quiz.id}" 
                                           class="btn btn-sm btn-danger"
                                           onclick="return confirm('Are you sure you want to delete this quiz? This action cannot be undone.')">
                                            Delete
                                        </a>
                                        <a href="${pageContext.request.contextPath}/quiz/preview?id=${quiz.id}" class="btn btn-sm btn-outline-primary">Preview</a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                
                <!-- Pagination -->
                <c:if test="${totalPages > 1}">
                    <nav aria-label="Quiz pagination">
                        <ul class="pagination">
                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/admin/manage-quizzes?page=${currentPage - 1}&categoryId=${param.categoryId}&status=${param.status}">Previous</a>
                            </li>
                            
                            <c:forEach begin="1" end="${totalPages}" var="pageNum">
                                <li class="page-item ${pageNum == currentPage ? 'active' : ''}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/admin/manage-quizzes?page=${pageNum}&categoryId=${param.categoryId}&status=${param.status}">${pageNum}</a>
                                </li>
                            </c:forEach>
                            
                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/admin/manage-quizzes?page=${currentPage + 1}&categoryId=${param.categoryId}&status=${param.status}">Next</a>
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