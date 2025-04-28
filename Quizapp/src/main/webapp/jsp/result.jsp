</head>
<body>
    <jsp:include page="../includes/header.jsp" />

    <div class="container">
        <div class="results-container">
            <div class="results-header">
                <h1>Quiz Results</h1>
                <h2>${quiz.title}</h2>
                <p>Completed on <fmt:formatDate value="${attempt.attemptDate}" pattern="MMM dd, yyyy HH:mm" /></p>
            </div>
            
            <div class="results-summary">
                <div class="score-card">
                    <div class="score-circle ${percentage >= 70 ? 'good' : percentage >= 50 ? 'average' : 'poor'}">
                        <div class="percentage">${percentage}%</div>
                    </div>
                    <div class="score-details">
                        <p>Your Score: <strong>${attempt.score}/${totalQuestions}</strong></p>
                        <c:choose>
                            <c:when test="${percentage >= 80}">
                                <p class="score-comment">Excellent work!</p>
                            </c:when>
                            <c:when test="${percentage >= 70}">
                                <p class="score-comment">Great job!</p>
                            </c:when>
                            <c:when test="${percentage >= 50}">
                                <p class="score-comment">Good effort!</p>
                            </c:when>
                            <c:otherwise>
                                <p class="score-comment">Keep practicing!</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                
                <div class="results-chart">
                    <canvas id="resultChart"></canvas>
                </div>
            </div>
            
            <div class="results-breakdown">
                <h3>Question Breakdown</h3>
                <div class="answers-list">
                    <c:forEach items="${answers}" var="answer" varStatus="status">
                        <div class="answer-item ${answer.correct ? 'correct' : 'incorrect'}">
                            <div class="question-number">${status.index + 1}</div>
                            <div class="answer-details">
                                <p class="question-text">${answer.questionText}</p>
                                <p class="answer-text">
                                    <strong>Your answer:</strong> ${answer.userAnswer}
                                    <c:if test="${!answer.correct}">
                                        <br><strong>Correct answer:</strong> ${answer.correctAnswer}
                                    </c:if>
                                </p>
                            </div>
                            <div class="answer-icon">
                                <c:choose>
                                    <c:when test="${answer.correct}">
                                        <svg class="correct-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24">
                                            <path fill="none" d="M0 0h24v24H0z"/>
                                            <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                                        </svg>
                                    </c:when>
                                    <c:otherwise>
                                        <svg class="incorrect-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24">
                                            <path fill="none" d="M0 0h24v24H0z"/>
                                            <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
                                        </svg>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
            
            <div class="results-actions">
                <a href="${pageContext.request.contextPath}/quizzes" class="btn secondary">Browse More Quizzes</a>
                <a href="${pageContext.request.contextPath}/quiz/start/${quiz.id}" class="btn primary">Try Again</a>
            </div>
        </div>
    </div>

    <jsp:include page="../includes/footer.jsp" />
    
    <script>
        $(document).ready(function() {
            // Animate score circle
            let percentage = ${percentage};
            $('.percentage').each(function () {
                $(this).prop('Counter', 0).animate({
                    Counter: percentage
                }, {
                    duration: 1500,
                    easing: 'swing',
                    step: function (now) {
                        $(this).text(Math.ceil(now) + '%');
                    }
                });
            });
            
            // Initialize results chart
            const ctx = document.getElementById('resultChart').getContext('2d');
            const correct = ${attempt.score};
            const incorrect = ${totalQuestions - attempt.score};
            
            const resultChart = new Chart(ctx, {
                type: 'doughnut',
                data: {
                    labels: ['Correct', 'Incorrect'],
                    datasets: [{
                        data: [correct, incorrect],
                        backgroundColor: [
                            '#4CAF50',
                            '#F44336'
                        ],
                        borderWidth: 0
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    cutout: '70%',
                    plugins: {
                        legend: {
                            position: 'bottom'
                        }
                    }
                }
            });
            
            // Fade in answer items
            $('.answer-item').each(function(index) {
                $(this).delay(100 * index).animate({opacity: 1}, 500);
            });
        });
    </script>
</body>
</html>