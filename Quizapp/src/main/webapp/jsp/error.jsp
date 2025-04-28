<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error - Quiz App</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
    <jsp:include page="includes/header.jsp" />

    <div class="container">
        <div class="error-container">
            <div class="error-icon">
                <img src="${pageContext.request.contextPath}/images/error-icon.svg" alt="Error">
            </div>
            
            <h1>Oops! Something went wrong</h1>
            
            <c:choose>
                <c:when test="${not empty errorMessage}">
                    <p class="error-message">${errorMessage}</p>
                </c:when>
                <c:when test="${not empty pageContext.exception}">
                    <p class="error-message">Error: ${pageContext.exception.message}</p>
                </c:when>
                <c:when test="${not empty param.status}">
                    <c:choose>
                        <c:when test="${param.status == '404'}">
                            <p class="error-message">The page you are looking for does not exist.</p>
                        </c:when>
                        <c:when test="${param.status == '403'}">
                            <p class="error-message">You don't have permission to access this resource.</p>
                        </c:when>
                        <c:when test="${param.status == '500'}">
                            <p class="error-message">Internal server error. Our team has been notified.</p>
                        </c:when>
                        <c:otherwise>
                            <p class="error-message">An unexpected error occurred. Please try again later.</p>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>
                    <p class="error-message">An unexpected error occurred. Please try again later.</p>
                </c:otherwise>
            </c:choose>
            
            <div class="error-actions">
                <a href="javascript:history.back()" class="btn secondary">Go Back</a>
                <a href="${pageContext.request.contextPath}/" class="btn primary">Home</a>
            </div>
        </div>
    </div>

    <jsp:include page="includes/footer.jsp" />
</body>
</html>