<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quiz Result</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
    <jsp:include page="../includes/header.jsp" />
    <jsp:include page="../includes/navigation.jsp" />
    
    <div class="container">
        <h1>Quiz Result</h1>
        
        <div class="result-summary">
            <h2>${quiz.title}</h2>
            <div class="quiz-info">
                <span class="badge badge-primary">${quiz.categoryName}</span>
                <span class="date-info">Completed on: <fmt:formatDate value="${result.completedDate}" pattern="MMM dd, yyyy HH:mm" /></span>
            </div>
            
            <div class="score-container">
                <div class="score-circle ${result.passStatus ? 'pass' : 'fail'}">
                    <div class="score-value">${result.score}%</div>
                    <div class="score-label">${result.passStatus ? 'PASS' : 'FAIL'}</div>
                </div>
                
                <div class="score-details">
                    <div class="detail-item">
                        <span class="detail-label">Correct Answers:</span>
                        <span class="detail-value">${result.correctAnswers} of ${result.totalQuestions}</span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Time Taken:</span>
                        <span class="detail-value">${result.formattedTimeTaken}</span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Passing Score:</span>
                        <span class="detail-value">${quiz.passingScore}%</span>
                    </div>
                </div>
            </div>
            
            <div class="result-actions">
                <a href="${pageContext.request.contextPath}/quiz/take?id=${quiz.id}" class="btn btn-primary">Take Again</a>
                <a href="${pageContext.request.contextPath}/quiz/list" class="btn btn-secondary">Back to Quizzes</a>
                <c:if test="${quiz.showAnswers}">
                    <button id="show-answers-btn" class="btn btn-info">Review Answers</button>
                </c:if>
            </div>
        </div>
        
        <div class="performance-charts">
            <div class="row">
                <div class="col-md-6">
                    <div class="chart-container">
                        <h3>Performance Overview</h3>
                        <canvas id="resultChart"></canvas>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="chart-container">
                        <h3>Time Distribution</h3>
                        <canvas id="timeChart"></canvas>
                    </div>
                </div>
            </div>
        </div>
        
        <c:if test="${quiz.showAnswers}">
            <div id="answers-review" style="display: none;">
                <h3>Question Review</h3>
                
                <div class="answer-filters">
                    <button class="btn btn-sm btn-outline-secondary filter-btn" data-filter="all">All Questions</button>
                    <button class="btn btn-sm btn-outline-success filter-btn" data-filter="correct">Correct Only</button>
                    <button class="btn btn-sm btn-outline-danger filter-btn" data-filter="incorrect">Incorrect Only</button>
                </div>
                
                <div class="questions-review">
                    <c:forEach items="${result.questionResults}" var="qResult" varStatus="status">
                        <div class="question-review-item ${qResult.correct ? 'correct' : 'incorrect'}">
                            <div class="question-header">
                                <h4>Question ${status.index + 1}</h4>
                                <span class="result-badge ${qResult.correct ? 'badge-success' : 'badge-danger'}">
                                    ${qResult.correct ? 'Correct' : 'Incorrect'}
                                </span>
                            </div>
                            
                            <p class="question-text">${qResult.questionText}</p>
                            
                            <div class="options-review">
                                <c:forEach items="${qResult.options}" var="option" varStatus="optStatus">
                                    <div class="option-item ${optStatus.index == qResult.correctOption ? 'correct-option' : ''} 
                                                        ${optStatus.index == qResult.selectedOption ? 'selected-option' : ''}">
                                        <span class="option-marker">
                                            <c:choose>
                                                <c:when test="${optStatus.index == qResult.correctOption && optStatus.index == qResult.selectedOption}">
                                                    <i class="fas fa-check-circle text-success"></i>
                                                </c:when>
                                                <c:when test="${optStatus.index == qResult.correctOption}">
                                                    <i class="fas fa-check-circle text-success"></i>
                                                </c:when>
                                                <c:when test="${optStatus.index == qResult.selectedOption}">
                                                    <i class="fas fa-times-circle text-danger"></i>
                                                </c:when>
                                                <c:otherwise>
                                                    <i class="far fa-circle"></i>
                                                </c:otherwise>
                                            </c:choose>
                                        </span>
                                        <span class="option-text">${option}</span>
                                    </div>
                                </c:forEach>
                            </div>
                            
                            <c:if test="${not empty qResult.explanation}">
                                <div class="explanation">
                                    <p><strong>Explanation:</strong> ${qResult.explanation}</p>
                                </div>
                            </c:if>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </c:if>
    </div>
    
    <script>
    $(document).ready(function() {
        // Toggle answers review section
        $('#show-answers-btn').click(function() {
            $('#answers-review').toggle();
            $(this).text(function(i, text) {
                return text === "Review Answers" ? "Hide Answers" : "Review Answers";
            });
        });
        
        // Filter questions in review
        $('.filter-btn').click(function() {
            const filter = $(this).data('filter');
            $('.filter-btn').removeClass('active');
            $(this).addClass('active');
            
            if (filter === 'all') {
                $('.question-review-item').show();
            } else if (filter === 'correct') {
                $('.question-review-item').hide();
                $('.question-review-item.correct').show();
            } else if (filter === 'incorrect') {
                $('.question-review-item').hide();
                $('.question-review-item.incorrect').show();
            }
        });
        
        // Create performance chart
        const resultCtx = document.getElementById('resultChart').getContext('2d');
        const resultChart = new Chart(resultCtx, {
            type: 'doughnut',
            data: {
                labels: ['Correct', 'Incorrect'],
                datasets: [{
                    data: [${result.correctAnswers}, ${result.totalQuestions - result.correctAnswers}],
                    backgroundColor: ['#28a745', '#dc3545'],
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                }
            }
        });
        
        // Create time distribution chart (if time data is available)
        const timeCtx = document.getElementById('timeChart').getContext('2d');
        const timeChart = new Chart(timeCtx, {
            type: 'bar',
            data: {
                labels: ['Your Time', 'Average Time', 'Time Limit'],
                datasets: [{
                    label: 'Time (minutes)',
                    data: [
                        ${result.timeTakenMinutes}, 
                        ${quiz.averageCompletionTime}, 
                        ${quiz.timeLimit}
                    ],
                    backgroundColor: ['#007bff', '#17a2b8', '#6c757d'],
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        title: {
                            display: true,
                            text: 'Minutes'
                        }
                    }
                },
                plugins: {
                    legend: {
                        display: false
                    }
                }
            }
        });
    });
    </script>
    
    <jsp:include page="../includes/footer.jsp" />
</body>
</html>