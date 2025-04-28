<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${quiz.title} - Take Quiz</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
</head>
<body>
    <jsp:include page="../includes/header.jsp" />
    
    <div class="container">
        <h1>${quiz.title}</h1>
        <div class="quiz-info">
            <span class="badge badge-primary">${quiz.categoryName}</span>
            <span class="time-info"><i class="fas fa-clock"></i> ${quiz.timeLimit} minutes</span>
            <span class="question-info"><i class="fas fa-question-circle"></i> ${quiz.questions.size()} questions</span>
        </div>
        
        <c:if test="${not empty quiz.description}">
            <div class="quiz-description">
                <p>${quiz.description}</p>
            </div>
        </c:if>
        
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">${errorMessage}</div>
        </c:if>
        
        <div class="quiz-instructions">
            <h3>Instructions:</h3>
            <ul>
                <li>You have ${quiz.timeLimit} minutes to complete this quiz.</li>
                <li>The timer will start once you click "Begin Quiz".</li>
                <li>Each question has one correct answer.</li>
                <li>You must answer all questions before submitting.</li>
                <li>Your results will be displayed immediately after submission.</li>
            </ul>
        </div>
        
        <div id="quiz-container" style="display: none;">
            <div class="quiz-header">
                <div class="timer-container">
                    Time Remaining: <span id="timer"></span>
                </div>
                <div class="progress">
                    <div id="progress-bar" class="progress-bar" role="progressbar" style="width: 0%;" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100">0%</div>
                </div>
            </div>
            
            <form id="quiz-form" action="${pageContext.request.contextPath}/quiz/submit" method="post">
                <input type="hidden" name="quizId" value="${quiz.id}">
                <input type="hidden" name="startTime" id="startTime">
                <input type="hidden" name="endTime" id="endTime">
                
                <div id="question-container">
                    <c:forEach items="${quiz.questions}" var="question" varStatus="status">
                        <div class="question" id="question-${status.index}" style="display: none;">
                            <h4>Question ${status.index + 1} of ${quiz.questions.size()}</h4>
                            <p class="question-text">${question.text}</p>
                            
                            <div class="options">
                                <c:forEach items="${question.options}" var="option" varStatus="optStatus">
                                    <div class="form-check">
                                        <input class="form-check-input" type="radio" name="answers[${question.id}]" 
                                            id="option-${question.id}-${optStatus.index}" value="${optStatus.index}" required>
                                        <label class="form-check-label" for="option-${question.id}-${optStatus.index}">
                                            ${option}
                                        </label>
                                    </div>
                                </c:forEach>
                            </div>
                            
                            <div class="question-navigation">
                                <c:if test="${status.index > 0}">
                                    <button type="button" class="btn btn-outline-secondary prev-btn" data-prev="${status.index - 1}">Previous</button>
                                </c:if>
                                
                                <c:choose>
                                    <c:when test="${status.index < quiz.questions.size() - 1}">
                                        <button type="button" class="btn btn-primary next-btn" data-next="${status.index + 1}">Next</button>
                                    </c:when>
                                    <c:otherwise>
                                        <button type="button" class="btn btn-success" id="finish-btn">Finish Quiz</button>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </c:forEach>
                </div>
                
                <div class="question-navigation-dots">
                    <c:forEach begin="0" end="${quiz.questions.size() - 1}" var="i">
                        <span class="dot" data-question="${i}">${i + 1}</span>
                    </c:forEach>
                </div>
                
                <div id="submit-section" style="display: none;">
                    <h4>Review Your Answers</h4>
                    <p>Please review your answers before submitting. Unanswered questions will be marked as incorrect.</p>
                    
                    <div class="unanswered-questions-alert" style="display: none;">
                        <div class="alert alert-warning">
                            <strong>Warning:</strong> You have <span id="unanswered-count"></span> unanswered questions.
                            <div id="unanswered-list"></div>
                        </div>
                    </div>
                    
                    <div class="form-actions">
                        <button type="button" class="btn btn-secondary" id="review-btn">Back to Questions</button>
                        <button type="submit" class="btn btn-primary" id="submit-btn">Submit Quiz</button>
                    </div>
                </div>
            </form>
        </div>
        
        <div id="start-section">
            <button id="start-btn" class="btn btn-lg btn-primary">Begin Quiz</button>
        </div>
    </div>
    
    <script>
    $(document).ready(function() {
        let currentQuestion = 0;
        let totalQuestions = ${quiz.questions.size()};
        let timeLimit = ${quiz.timeLimit} * 60; // convert to seconds
        let timer;
        
        // Start button click handler
        $('#start-btn').click(function() {
            $('#start-section').hide();
            $('#quiz-container').show();
            $('#question-0').show();
            updateProgressBar();
            
            // Set start time
            const startTime = new Date();
            $('#startTime').val(startTime.toISOString());
            
            // Start the timer
            startTimer(timeLimit);
            
            // Set active dot
            $('.dot[data-question="0"]').addClass('active');
        });
        
        // Next button click handler
        $('.next-btn').click(function() {
            const nextQuestionIndex = $(this).data('next');
            navigateToQuestion(nextQuestionIndex);
        });
        
        // Previous button click handler
        $('.prev-btn').click(function() {
            const prevQuestionIndex = $(this).data('prev');
            navigateToQuestion(prevQuestionIndex);
        });
        
        // Navigation dots click handler
        $('.dot').click(function() {
            const questionIndex = $(this).data('question');
            navigateToQuestion(questionIndex);
        });
        
        // Finish button click handler
        $('#finish-btn').click(function() {
            $('.question').hide();
            $('#submit-section').show();
            checkUnansweredQuestions();
        });
        
        // Review button click handler
        $('#review-btn').click(function() {
            $('#submit-section').hide();
            navigateToQuestion(currentQuestion);
        });
        
        // Submit form handler
        $('#quiz-form').submit(function() {
            // Set end time
            const endTime = new Date();
            $('#endTime').val(endTime.toISOString());
            
            // Clear the timer
            clearInterval(timer);
            
            return true;
        });
        
        // Function to navigate to a specific question
        function navigateToQuestion(index) {
            $('.question').hide();
            $(`#question-${index}`).show();
            currentQuestion = index;
            
            // Update active dot
            $('.dot').removeClass('active');
            $(`.dot[data-question="${index}"]`).addClass('active');
            
            // Update progress bar
            updateProgressBar();
            
            // Update dot status (answered or not)
            updateDotStatus();
        }
        
        // Function to update the progress bar
        function updateProgressBar() {
            const progress = ((currentQuestion + 1) / totalQuestions) * 100;
            $('#progress-bar').css('width', `${progress}%`);
            $('#progress-bar').attr('aria-valuenow', progress);
            $('#progress-bar').text(`${Math.round(progress)}%`);
        }
        
        // Function to start the timer
        function startTimer(duration) {
            let timeRemaining = duration;
            updateTimerDisplay(timeRemaining);
            
            timer = setInterval(function() {
                timeRemaining--;
                updateTimerDisplay(timeRemaining);
                
                if (timeRemaining <= 0) {
                    clearInterval(timer);
                    alert("Time's up! Your quiz will be submitted now.");
                    $('#quiz-form').submit();
                }
            }, 1000);
        }
        
        // Function to update the timer display
        function updateTimerDisplay(seconds) {
            const minutes = Math.floor(seconds / 60);
            const remainingSeconds = seconds % 60;
            
            const display = `${minutes}:${remainingSeconds < 10 ? '0' : ''}${remainingSeconds}`;
            $('#timer').text(display);
            
            // Change color when less than 1 minute remains
            if (seconds < 60) {
                $('#timer').addClass('timer-warning');
            }
        }
        
        // Function to check for unanswered questions
        function checkUnansweredQuestions() {
            const unanswered = [];
            
            for (let i = 0; i < totalQuestions; i++) {
                const questionId = $('.question').eq(i).find('input[type="radio"]').attr('name');
                if (!$(`input[name="${questionId}"]:checked`).val()) {
                    unanswered.push(i + 1);
                }
            }
            
            if (unanswered.length > 0) {
                $('#unanswered-count').text(unanswered.length);
                $('#unanswered-list').html('<p>Unanswered questions: ' + unanswered.join(', ') + '</p>');
                $('.unanswered-questions-alert').show();
            } else {
                $('.unanswered-questions-alert').hide();
            }
        }
        
        // Function to update the status of navigation dots
        function updateDotStatus() {
            for (let i = 0; i < totalQuestions; i++) {
                const questionId = $('.question').eq(i).find('input[type="radio"]').attr('name');
                if ($(`input[name="${questionId}"]:checked`).val()) {
                    $(`.dot[data-question="${i}"]`).addClass('answered');
                }
            }
        }
        
        // Update dot status when an answer is selected
        $(document).on('change', 'input[type="radio"]', function() {
            updateDotStatus();
        });
    });
    </script>
    
    <jsp:include page="../includes/footer.jsp" />
</body>
</html>