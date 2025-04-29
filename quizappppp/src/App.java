// QuizApp.java - Main application class
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class App extends Application {

    // Database connection
    private Connection connection;
    private User currentUser;
    
    // UI components
    private Stage primaryStage;
    private Scene loginScene;
    private Scene registerScene;
    private Scene mainMenuScene;
    private Scene quizListScene;
    private Scene quizTakingScene;
    private Scene adminScene;
    private Scene createQuizScene;
    private Scene resultScene;
    private Scene historyScene;
    
    // Quiz data
    private List<Quiz> quizzes;
    private Quiz currentQuiz;
    private int currentQuestionIndex;
    private int score;
    private List<Integer> userAnswers;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        initializeDatabase();
        createScenes();
        
        primaryStage.setTitle("Quiz Application");
        primaryStage.setScene(loginScene);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);
        primaryStage.show();
    }
    
    @Override
    public void stop() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeDatabase() {
        try {
            // Initialize SQLite database
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:quiz_app.db");
            System.out.println("Database connection established");
            
            // Create tables if they don't exist
            createTables();
            
            // Add sample data for testing
            addSampleData();
            
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to connect to database: " + e.getMessage());
        }
    }

    private void createTables() throws SQLException {
        Statement statement = connection.createStatement();
        
        // Create users table
        statement.execute("CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "is_admin INTEGER DEFAULT 0)");
        
        // Create quizzes table
        statement.execute("CREATE TABLE IF NOT EXISTS quizzes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "description TEXT)");
        
        // Create questions table
        statement.execute("CREATE TABLE IF NOT EXISTS questions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "quiz_id INTEGER NOT NULL," +
                "question_text TEXT NOT NULL," +
                "FOREIGN KEY (quiz_id) REFERENCES quizzes(id))");
        
        // Create options table
        statement.execute("CREATE TABLE IF NOT EXISTS options (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "question_id INTEGER NOT NULL," +
                "option_text TEXT NOT NULL," +
                "is_correct INTEGER NOT NULL," +
                "FOREIGN KEY (question_id) REFERENCES questions(id))");
        
        // Create quiz_attempts table
        statement.execute("CREATE TABLE IF NOT EXISTS quiz_attempts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "quiz_id INTEGER NOT NULL," +
                "score INTEGER NOT NULL," +
                "total_questions INTEGER NOT NULL," +
                "date_taken TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES users(id)," +
                "FOREIGN KEY (quiz_id) REFERENCES quizzes(id))");
                
        statement.close();
    }
    
    private void addSampleData() {
        try {
            // Check if we already have sample data
            PreparedStatement checkUsers = connection.prepareStatement("SELECT COUNT(*) FROM users");
            ResultSet rs = checkUsers.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                rs.close();
                checkUsers.close();
                return; // Sample data already exists
            }
            rs.close();
            checkUsers.close();
            
            // Add admin user
            PreparedStatement insertAdmin = connection.prepareStatement(
                    "INSERT INTO users (username, password, is_admin) VALUES (?, ?, 1)");
            insertAdmin.setString(1, "admin");
            insertAdmin.setString(2, hashPassword("admin"));
            insertAdmin.executeUpdate();
            insertAdmin.close();
            
            // Add regular user
            PreparedStatement insertUser = connection.prepareStatement(
                    "INSERT INTO users (username, password, is_admin) VALUES (?, ?, 0)");
            insertUser.setString(1, "user");
            insertUser.setString(2, hashPassword("user"));
            insertUser.executeUpdate();
            insertUser.close();
            
            // Add sample quiz
            PreparedStatement insertQuiz = connection.prepareStatement(
                    "INSERT INTO quizzes (title, description) VALUES (?, ?)", 
                    Statement.RETURN_GENERATED_KEYS);
            insertQuiz.setString(1, "Java Basics");
            insertQuiz.setString(2, "Test your knowledge of Java programming language basics.");
            insertQuiz.executeUpdate();
            
            ResultSet quizKeys = insertQuiz.getGeneratedKeys();
            int quizId = quizKeys.getInt(1);
            quizKeys.close();
            insertQuiz.close();
            
            // Add sample questions and options
            addSampleQuestion(quizId, "What is the main method signature in Java?", 
                    new String[]{"public static void main(String[] args)", "public void main(String[] args)", 
                            "static void main(String[] args)", "void main(String args[])"},
                    0);
            
            addSampleQuestion(quizId, "Which of the following is not a Java keyword?", 
                    new String[]{"static", "Boolean", "void", "extends"},
                    1);
            
            addSampleQuestion(quizId, "What is the default value of an int variable?", 
                    new String[]{"0", "null", "undefined", "1"},
                    0);
            
            System.out.println("Sample data added successfully");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void addSampleQuestion(int quizId, String questionText, String[] options, int correctOptionIndex) throws SQLException {
        // Insert question
        PreparedStatement insertQuestion = connection.prepareStatement(
                "INSERT INTO questions (quiz_id, question_text) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS);
        insertQuestion.setInt(1, quizId);
        insertQuestion.setString(2, questionText);
        insertQuestion.executeUpdate();
        
        ResultSet questionKeys = insertQuestion.getGeneratedKeys();
        int questionId = questionKeys.getInt(1);
        questionKeys.close();
        insertQuestion.close();
        
        // Insert options
        PreparedStatement insertOption = connection.prepareStatement(
                "INSERT INTO options (question_id, option_text, is_correct) VALUES (?, ?, ?)");
        
        for (int i = 0; i < options.length; i++) {
            insertOption.setInt(1, questionId);
            insertOption.setString(2, options[i]);
            insertOption.setInt(3, i == correctOptionIndex ? 1 : 0);
            insertOption.executeUpdate();
        }
        
        insertOption.close();
    }
    
    private String hashPassword(String password) {
        // In a real application, you should use a secure hashing algorithm with salt
        // For simplicity, we're just returning the password as-is
        return password;
    }

    private void createScenes() {
        loginScene = createLoginScene();
        registerScene = createRegisterScene();
        mainMenuScene = createMainMenuScene();
        quizListScene = createQuizListScene();
        adminScene = createAdminScene();
        createQuizScene = createQuizCreationScene();
        // The quiz taking scene and results scene will be created dynamically when needed
    }

    private Scene createLoginScene() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("Quiz Application");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        
        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            
            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Login Error", "Please enter both username and password");
                return;
            }
            
            try {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT id, is_admin FROM users WHERE username = ? AND password = ?");
                statement.setString(1, username);
                statement.setString(2, hashPassword(password));
                
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    int userId = resultSet.getInt("id");
                    boolean isAdmin = resultSet.getInt("is_admin") == 1;
                    
                    currentUser = new User(userId, username, isAdmin);
                    resultSet.close();
                    statement.close();
                    
                    if (isAdmin) {
                        primaryStage.setScene(adminScene);
                    } else {
                        primaryStage.setScene(mainMenuScene);
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Login Error", "Invalid username or password");
                }
                
                resultSet.close();
                statement.close();
                
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Error during login: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        
        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> primaryStage.setScene(registerScene));
        
        HBox buttonBox = new HBox(10, loginButton, registerButton);
        buttonBox.setAlignment(Pos.CENTER);
        
        root.getChildren().addAll(titleLabel, new Separator(), usernameLabel, usernameField, 
                passwordLabel, passwordField, buttonBox);
        
        return new Scene(root, 400, 300);
    }

    private Scene createRegisterScene() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("Register New Account");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Choose a username");
        
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Choose a password");
        
        Label confirmPasswordLabel = new Label("Confirm Password:");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm your password");
        
        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            
            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Registration Error", "Please fill in all fields");
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                showAlert(Alert.AlertType.ERROR, "Registration Error", "Passwords do not match");
                return;
            }
            
            try {
                // Check if username already exists
                PreparedStatement checkStatement = connection.prepareStatement(
                        "SELECT id FROM users WHERE username = ?");
                checkStatement.setString(1, username);
                
                ResultSet resultSet = checkStatement.executeQuery();
                if (resultSet.next()) {
                    showAlert(Alert.AlertType.ERROR, "Registration Error", "Username already exists");
                    resultSet.close();
                    checkStatement.close();
                    return;
                }
                resultSet.close();
                checkStatement.close();
                
                // Insert new user
                PreparedStatement insertStatement = connection.prepareStatement(
                        "INSERT INTO users (username, password, is_admin) VALUES (?, ?, 0)");
                insertStatement.setString(1, username);
                insertStatement.setString(2, hashPassword(password));
                insertStatement.executeUpdate();
                insertStatement.close();
                
                showAlert(Alert.AlertType.INFORMATION, "Registration Successful", 
                        "Your account has been created. You can now login.");
                
                primaryStage.setScene(loginScene);
                
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Database Error", 
                        "Error during registration: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        
        Button backButton = new Button("Back to Login");
        backButton.setOnAction(e -> primaryStage.setScene(loginScene));
        
        HBox buttonBox = new HBox(10, registerButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);
        
        root.getChildren().addAll(titleLabel, new Separator(), usernameLabel, usernameField, 
                passwordLabel, passwordField, confirmPasswordLabel, confirmPasswordField, buttonBox);
        
        return new Scene(root, 400, 350);
    }

    private Scene createMainMenuScene() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        
        Label welcomeLabel = new Label();
        welcomeLabel.setStyle("-fx-font-size: 18px;");
        
        Button takeQuizButton = new Button("Take a Quiz");
        takeQuizButton.setPrefWidth(200);
        takeQuizButton.setOnAction(e -> {
            loadQuizzes();
            primaryStage.setScene(quizListScene);
        });
        
        Button viewHistoryButton = new Button("View My Quiz History");
        viewHistoryButton.setPrefWidth(200);
        viewHistoryButton.setOnAction(e -> {
            historyScene = createHistoryScene();
            primaryStage.setScene(historyScene);
        });
        
        Button logoutButton = new Button("Logout");
        logoutButton.setPrefWidth(200);
        logoutButton.setOnAction(e -> {
            currentUser = null;
            primaryStage.setScene(loginScene);
        });
        
        root.getChildren().addAll(welcomeLabel, new Separator(), 
                takeQuizButton, viewHistoryButton, logoutButton);
        
        // Update welcome label when scene is shown
        root.setOnMouseEntered(e -> {
            if (currentUser != null) {
                welcomeLabel.setText("Welcome, " + currentUser.getUsername() + "!");
            }
        });
        
        return new Scene(root, 500, 350);
    }

    private Scene createQuizListScene() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("Available Quizzes");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        ListView<Quiz> quizListView = new ListView<>();
        quizListView.setPrefHeight(250);
        
        Button startQuizButton = new Button("Start Selected Quiz");
        startQuizButton.setDisable(true);
        startQuizButton.setOnAction(e -> {
            currentQuiz = quizListView.getSelectionModel().getSelectedItem();
            if (currentQuiz != null) {
                startQuiz(currentQuiz);
            }
        });
        
        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(e -> primaryStage.setScene(mainMenuScene));
        
        HBox buttonBox = new HBox(10, startQuizButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);
        
        root.getChildren().addAll(titleLabel, quizListView, buttonBox);
        
        // Add selection listener to enable Start button when a quiz is selected
        quizListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> startQuizButton.setDisable(newValue == null));
        
        // Populate quiz list
        root.setOnMouseEntered(e -> {
            if (quizzes != null) {
                quizListView.getItems().setAll(quizzes);
            }
        });
        
        return new Scene(root, 500, 400);
    }
    
    private void loadQuizzes() {
        try {
            quizzes = new ArrayList<>();
            
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT id, title, description FROM quizzes");
            
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                
                // Get question count
                PreparedStatement countStatement = connection.prepareStatement(
                        "SELECT COUNT(*) FROM questions WHERE quiz_id = ?");
                countStatement.setInt(1, id);
                ResultSet countResult = countStatement.executeQuery();
                int questionCount = countResult.next() ? countResult.getInt(1) : 0;
                countResult.close();
                countStatement.close();
                
                quizzes.add(new Quiz(id, title, description, questionCount));
            }
            
            resultSet.close();
            statement.close();
            
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", 
                    "Error loading quizzes: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void startQuiz(Quiz quiz) {
        try {
            // Load all questions for this quiz
            currentQuiz = quiz;
            currentQuiz.setQuestions(new ArrayList<>());
            
            PreparedStatement questionStatement = connection.prepareStatement(
                    "SELECT id, question_text FROM questions WHERE quiz_id = ?");
            questionStatement.setInt(1, quiz.getId());
            
            ResultSet questionResults = questionStatement.executeQuery();
            while (questionResults.next()) {
                int questionId = questionResults.getInt("id");
                String questionText = questionResults.getString("question_text");
                
                Question question = new Question(questionId, questionText);
                
                // Load options for this question
                PreparedStatement optionStatement = connection.prepareStatement(
                        "SELECT id, option_text, is_correct FROM options WHERE question_id = ?");
                optionStatement.setInt(1, questionId);
                
                ResultSet optionResults = optionStatement.executeQuery();
                while (optionResults.next()) {
                    int optionId = optionResults.getInt("id");
                    String optionText = optionResults.getString("option_text");
                    boolean isCorrect = optionResults.getInt("is_correct") == 1;
                    
                    Option option = new Option(optionId, optionText, isCorrect);
                    question.getOptions().add(option);
                }
                
                optionResults.close();
                optionStatement.close();
                
                currentQuiz.getQuestions().add(question);
            }
            
            questionResults.close();
            questionStatement.close();
            
            // Initialize quiz parameters
            currentQuestionIndex = 0;
            score = 0;
            userAnswers = new ArrayList<>();
            
            // Create and display the quiz taking scene
            if (!currentQuiz.getQuestions().isEmpty()) {
                quizTakingScene = createQuizTakingScene();
                primaryStage.setScene(quizTakingScene);
            } else {
                showAlert(Alert.AlertType.ERROR, "Quiz Error", 
                        "This quiz has no questions. Please select another quiz.");
            }
            
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", 
                    "Error loading quiz questions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Scene createQuizTakingScene() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        
        // Quiz title
        Label quizTitleLabel = new Label(currentQuiz.getTitle());
        quizTitleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Question number
        Label questionNumberLabel = new Label(String.format("Question %d of %d", 
                currentQuestionIndex + 1, currentQuiz.getQuestions().size()));
        
        // Question
        Question currentQuestion = currentQuiz.getQuestions().get(currentQuestionIndex);
        Label questionLabel = new Label(currentQuestion.getText());
        questionLabel.setStyle("-fx-font-size: 16px;");
        questionLabel.setWrapText(true);
        
        // Options
        VBox optionsBox = new VBox(10);
        ToggleGroup optionsGroup = new ToggleGroup();
        
        for (Option option : currentQuestion.getOptions()) {
            RadioButton optionButton = new RadioButton(option.getText());
            optionButton.setToggleGroup(optionsGroup);
            optionButton.setUserData(option);
            optionButton.setWrapText(true);
            optionsBox.getChildren().add(optionButton);
        }
        
        // Navigation buttons
        Button submitButton = new Button("Submit Answer");
        submitButton.setOnAction(e -> {
            RadioButton selectedButton = (RadioButton) optionsGroup.getSelectedToggle();
            if (selectedButton == null) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please select an answer");
                return;
            }
            
            Option selectedOption = (Option) selectedButton.getUserData();
            userAnswers.add(selectedOption.getId());
            
            if (selectedOption.isCorrect()) {
                score++;
                showAlert(Alert.AlertType.INFORMATION, "Correct!", "Your answer is correct!");
            } else {
                // Find correct answer for feedback
                String correctAnswer = "";
                for (Option option : currentQuestion.getOptions()) {
                    if (option.isCorrect()) {
                        correctAnswer = option.getText();
                        break;
                    }
                }
                showAlert(Alert.AlertType.INFORMATION, "Incorrect", 
                        "Your answer is incorrect. The correct answer is: " + correctAnswer);
            }
            
            // Move to next question or finish quiz
            currentQuestionIndex++;
            if (currentQuestionIndex < currentQuiz.getQuestions().size()) {
                quizTakingScene = createQuizTakingScene();
                primaryStage.setScene(quizTakingScene);
            } else {
                finishQuiz();
            }
        });
        
        Button quitButton = new Button("Quit Quiz");
        quitButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Quit Quiz");
            alert.setHeaderText("Are you sure you want to quit?");
            alert.setContentText("Your progress will be lost.");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                primaryStage.setScene(quizListScene);
            }
        });
        
        HBox buttonBox = new HBox(10, submitButton, quitButton);
        buttonBox.setAlignment(Pos.CENTER);
        
        root.getChildren().addAll(quizTitleLabel, questionNumberLabel, questionLabel, 
                new Separator(), optionsBox, buttonBox);
        
        return new Scene(root, 600, 400);
    }
    
    private void finishQuiz() {
        try {
            // Record quiz attempt in database
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO quiz_attempts (user_id, quiz_id, score, total_questions) VALUES (?, ?, ?, ?)");
            statement.setInt(1, currentUser.getId());
            statement.setInt(2, currentQuiz.getId());
            statement.setInt(3, score);
            statement.setInt(4, currentQuiz.getQuestions().size());
            statement.executeUpdate();
            statement.close();
            
            // Show results
            resultScene = createResultScene();
            primaryStage.setScene(resultScene);
            
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", 
                    "Error saving quiz results: " + e.getMessage());
            e.printStackTrace();
            primaryStage.setScene(mainMenuScene);
        }
    }

    private Scene createResultScene() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("Quiz Results");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Label quizTitleLabel = new Label(currentQuiz.getTitle());
        quizTitleLabel.setStyle("-fx-font-size: 18px;");
        
        int totalQuestions = currentQuiz.getQuestions().size();
        double percentage = (score * 100.0) / totalQuestions;
        
        Label scoreLabel = new Label(String.format("Score: %d out of %d (%.1f%%)", 
                score, totalQuestions, percentage));
        scoreLabel.setStyle("-fx-font-size: 16px;");
        
        Label feedbackLabel = new Label();
        if (percentage >= 80) {
            feedbackLabel.setText("Excellent! Great job!");
        } else if (percentage >= 60) {
            feedbackLabel.setText("Good work! Keep practicing!");
        } else {
            feedbackLabel.setText("Keep studying and try again!");
        }
        feedbackLabel.setStyle("-fx-font-size: 16px;");
        
        Button mainMenuButton = new Button("Return to Main Menu");
        mainMenuButton.setOnAction(e -> primaryStage.setScene(mainMenuScene));
        
        Button viewHistoryButton = new Button("View My Quiz History");
        viewHistoryButton.setOnAction(e -> {
            historyScene = createHistoryScene();
            primaryStage.setScene(historyScene);
        });
        
        HBox buttonBox = new HBox(10, mainMenuButton, viewHistoryButton);
        buttonBox.setAlignment(Pos.CENTER);
        
        root.getChildren().addAll(titleLabel, quizTitleLabel, scoreLabel, feedbackLabel, 
                new Separator(), buttonBox);
        
        return new Scene(root, 500, 350);
    }
    // Admin Scene - For admin users to manage quizzes
private Scene createAdminScene() {
    VBox root = new VBox(15);
    root.setPadding(new Insets(20));
    root.setAlignment(Pos.CENTER);
    
    Label titleLabel = new Label("Admin Dashboard");
    titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
    
    Button createQuizButton = new Button("Create New Quiz");
    createQuizButton.setPrefWidth(200);
    createQuizButton.setOnAction(e -> primaryStage.setScene(createQuizScene));
    
    Button manageQuizzesButton = new Button("Manage Existing Quizzes");
    manageQuizzesButton.setPrefWidth(200);
    manageQuizzesButton.setOnAction(e -> {
        loadQuizzes();
        primaryStage.setScene(createManageQuizzesScene());
    });
    
    Button viewStatsButton = new Button("View Quiz Statistics");
    viewStatsButton.setPrefWidth(200);
    viewStatsButton.setOnAction(e -> primaryStage.setScene(createStatsScene()));
    
    Button logoutButton = new Button("Logout");
    logoutButton.setPrefWidth(200);
    logoutButton.setOnAction(e -> {
        currentUser = null;
        primaryStage.setScene(loginScene);
    });
    
    root.getChildren().addAll(titleLabel, new Separator(),
            createQuizButton, manageQuizzesButton, viewStatsButton, logoutButton);
    
    return new Scene(root, 500, 400);
}

// Quiz Creation Scene - For creating new quizzes
private Scene createQuizCreationScene() {
    VBox root = new VBox(15);
    root.setPadding(new Insets(20));
    root.setAlignment(Pos.CENTER);
    
    Label titleLabel = new Label("Create New Quiz");
    titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
    
    // Quiz title input
    Label quizTitleLabel = new Label("Quiz Title:");
    TextField quizTitleField = new TextField();
    quizTitleField.setPromptText("Enter quiz title");
    
    // Quiz description input
    Label descriptionLabel = new Label("Quiz Description:");
    TextArea descriptionArea = new TextArea();
    descriptionArea.setPromptText("Enter quiz description");
    descriptionArea.setPrefRowCount(3);
    
    // Questions list
    Label questionsLabel = new Label("Questions:");
    ListView<QuestionEdit> questionsListView = new ListView<>();
    questionsListView.setPrefHeight(200);
    
    // Add new question button
    Button addQuestionButton = new Button("Add Question");
    addQuestionButton.setOnAction(e -> {
        Dialog<QuestionEdit> dialog = createQuestionDialog(null);
        dialog.showAndWait().ifPresent(question -> {
            questionsListView.getItems().add(question);
        });
    });
    
    // Edit selected question button
    Button editQuestionButton = new Button("Edit Selected");
    editQuestionButton.setDisable(true);
    editQuestionButton.setOnAction(e -> {
        QuestionEdit selectedQuestion = questionsListView.getSelectionModel().getSelectedItem();
        if (selectedQuestion != null) {
            Dialog<QuestionEdit> dialog = createQuestionDialog(selectedQuestion);
            dialog.showAndWait().ifPresent(editedQuestion -> {
                int index = questionsListView.getSelectionModel().getSelectedIndex();
                questionsListView.getItems().set(index, editedQuestion);
            });
        }
    });
    
    // Remove selected question button
    Button removeQuestionButton = new Button("Remove Selected");
    removeQuestionButton.setDisable(true);
    removeQuestionButton.setOnAction(e -> {
        QuestionEdit selectedQuestion = questionsListView.getSelectionModel().getSelectedItem();
        if (selectedQuestion != null) {
            questionsListView.getItems().remove(selectedQuestion);
        }
    });
    
    // Enable/disable edit and remove buttons based on selection
    questionsListView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                boolean hasSelection = newValue != null;
                editQuestionButton.setDisable(!hasSelection);
                removeQuestionButton.setDisable(!hasSelection);
            });
    
    HBox questionButtonsBox = new HBox(10, addQuestionButton, editQuestionButton, removeQuestionButton);
    questionButtonsBox.setAlignment(Pos.CENTER);
    
    // Save and Cancel buttons
    Button saveButton = new Button("Save Quiz");
    saveButton.setOnAction(e -> {
        String quizTitle = quizTitleField.getText().trim();
        String quizDescription = descriptionArea.getText().trim();
        
        if (quizTitle.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a quiz title");
            return;
        }
        
        if (questionsListView.getItems().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please add at least one question");
            return;
        }
        
        // Save quiz to database
        try {
            connection.setAutoCommit(false);
            
            // Insert quiz
            PreparedStatement quizStatement = connection.prepareStatement(
                    "INSERT INTO quizzes (title, description) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            quizStatement.setString(1, quizTitle);
            quizStatement.setString(2, quizDescription);
            quizStatement.executeUpdate();
            
            ResultSet quizKeys = quizStatement.getGeneratedKeys();
            int quizId = quizKeys.getInt(1);
            quizKeys.close();
            quizStatement.close();
            
            // Insert questions and options
            for (QuestionEdit question : questionsListView.getItems()) {
                PreparedStatement questionStatement = connection.prepareStatement(
                        "INSERT INTO questions (quiz_id, question_text) VALUES (?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                questionStatement.setInt(1, quizId);
                questionStatement.setString(2, question.getText());
                questionStatement.executeUpdate();
                
                ResultSet questionKeys = questionStatement.getGeneratedKeys();
                int questionId = questionKeys.getInt(1);
                questionKeys.close();
                questionStatement.close();
                
                // Insert options
                PreparedStatement optionStatement = connection.prepareStatement(
                        "INSERT INTO options (question_id, option_text, is_correct) VALUES (?, ?, ?)");
                
                for (OptionEdit option : question.getOptions()) {
                    optionStatement.setInt(1, questionId);
                    optionStatement.setString(2, option.getText());
                    optionStatement.setInt(3, option.isCorrect() ? 1 : 0);
                    optionStatement.executeUpdate();
                }
                
                optionStatement.close();
            }
            
            connection.commit();
            connection.setAutoCommit(true);
            
            showAlert(Alert.AlertType.INFORMATION, "Success", "Quiz saved successfully");
            primaryStage.setScene(adminScene);
            
        } catch (SQLException ex) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            
            showAlert(Alert.AlertType.ERROR, "Database Error", 
                    "Error saving quiz: " + ex.getMessage());
            ex.printStackTrace();
        }
    });
    
    Button cancelButton = new Button("Cancel");
    cancelButton.setOnAction(e -> primaryStage.setScene(adminScene));
    
    HBox buttonBox = new HBox(10, saveButton, cancelButton);
    buttonBox.setAlignment(Pos.CENTER);
    
    root.getChildren().addAll(titleLabel, quizTitleLabel, quizTitleField,
            descriptionLabel, descriptionArea, questionsLabel, questionsListView,
            questionButtonsBox, new Separator(), buttonBox);
    
    return new Scene(root, 600, 600);
}

// Helper method for creating question dialog
private Dialog<QuestionEdit> createQuestionDialog(QuestionEdit existingQuestion) {
    Dialog<QuestionEdit> dialog = new Dialog<>();
    dialog.setTitle(existingQuestion == null ? "Add Question" : "Edit Question");
    dialog.setHeaderText(existingQuestion == null ? "Create a new question" : "Edit existing question");
    
    ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
    
    // Create the form content
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));
    
    TextField questionField = new TextField();
    questionField.setPromptText("Question text");
    if (existingQuestion != null) {
        questionField.setText(existingQuestion.getText());
    }
    
    grid.add(new Label("Question:"), 0, 0);
    grid.add(questionField, 1, 0);
    
    // Options section
    Label optionsLabel = new Label("Options (select the correct one):");
    grid.add(optionsLabel, 0, 1, 2, 1);
    
    VBox optionsBox = new VBox(5);
    ToggleGroup optionsGroup = new ToggleGroup();
    
    List<HBox> optionRows = new ArrayList<>();
    List<TextField> optionFields = new ArrayList<>();
    List<RadioButton> optionRadios = new ArrayList<>();
    
    // Initialize with 4 options or existing options
    int initialOptionCount = existingQuestion != null ? 
            Math.max(4, existingQuestion.getOptions().size()) : 4;
    
    for (int i = 0; i < initialOptionCount; i++) {
        TextField optionField = new TextField();
        optionField.setPromptText("Option " + (i + 1));
        optionField.setPrefWidth(300);
        
        RadioButton correctRadio = new RadioButton();
        correctRadio.setToggleGroup(optionsGroup);
        
        if (existingQuestion != null && i < existingQuestion.getOptions().size()) {
            OptionEdit option = existingQuestion.getOptions().get(i);
            optionField.setText(option.getText());
            if (option.isCorrect()) {
                correctRadio.setSelected(true);
            }
        }
        
        HBox optionRow = new HBox(10, correctRadio, optionField);
        optionsBox.getChildren().add(optionRow);
        
        optionRows.add(optionRow);
        optionFields.add(optionField);
        optionRadios.add(correctRadio);
    }
    
    Button addOptionButton = new Button("Add Option");
    addOptionButton.setOnAction(e -> {
        TextField optionField = new TextField();
        optionField.setPromptText("Option " + (optionRows.size() + 1));
        optionField.setPrefWidth(300);
        
        RadioButton correctRadio = new RadioButton();
        correctRadio.setToggleGroup(optionsGroup);
        
        HBox optionRow = new HBox(10, correctRadio, optionField);
        optionsBox.getChildren().add(optionRow);
        
        optionRows.add(optionRow);
        optionFields.add(optionField);
        optionRadios.add(correctRadio);
    });
    
    Button removeOptionButton = new Button("Remove Last Option");
    removeOptionButton.setOnAction(e -> {
        int size = optionRows.size();
        if (size > 2) { // Keep at least 2 options
            optionsBox.getChildren().remove(size - 1);
            optionRows.remove(size - 1);
            optionFields.remove(size - 1);
            optionRadios.remove(size - 1);
        }
    });
    
    HBox optionButtonsBox = new HBox(10, addOptionButton, removeOptionButton);
    
    grid.add(optionsBox, 0, 2, 2, 1);
    grid.add(optionButtonsBox, 0, 3, 2, 1);
    
    dialog.getDialogPane().setContent(grid);
    
    // Request focus on the question field by default
    Platform.runLater(() -> questionField.requestFocus());
    
    // Convert the result to QuestionEdit when the save button is clicked
    dialog.setResultConverter(dialogButton -> {
        if (dialogButton == saveButtonType) {
            String questionText = questionField.getText().trim();
            
            if (questionText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Question text cannot be empty");
                return null;
            }
            
            // Get all options
            List<OptionEdit> options = new ArrayList<>();
            boolean hasCorrectOption = false;
            
            for (int i = 0; i < optionFields.size(); i++) {
                String optionText = optionFields.get(i).getText().trim();
                if (!optionText.isEmpty()) {
                    boolean isCorrect = optionRadios.get(i).isSelected();
                    if (isCorrect) {
                        hasCorrectOption = true;
                    }
                    options.add(new OptionEdit(optionText, isCorrect));
                }
            }
            
            if (options.size() < 2) {
                showAlert(Alert.AlertType.ERROR, "Error", "At least two options are required");
                return null;
            }
            
            if (!hasCorrectOption) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please select a correct option");
                return null;
            }
            
            return new QuestionEdit(questionText, options);
        }
        return null;
    });
    
    return dialog;
}

// Scene for managing existing quizzes
private Scene createManageQuizzesScene() {
    VBox root = new VBox(15);
    root.setPadding(new Insets(20));
    root.setAlignment(Pos.CENTER);
    
    Label titleLabel = new Label("Manage Quizzes");
    titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
    
    ListView<Quiz> quizListView = new ListView<>();
    quizListView.setPrefHeight(250);
    
    if (quizzes != null) {
        quizListView.getItems().setAll(quizzes);
    }
    
    Button editButton = new Button("Edit Selected Quiz");
    editButton.setDisable(true);
    editButton.setOnAction(e -> {
        Quiz selectedQuiz = quizListView.getSelectionModel().getSelectedItem();
        if (selectedQuiz != null) {
            // Load quiz for editing
            loadQuizForEditing(selectedQuiz);
        }
    });
    
    Button deleteButton = new Button("Delete Selected Quiz");
    deleteButton.setDisable(true);
    deleteButton.setOnAction(e -> {
        Quiz selectedQuiz = quizListView.getSelectionModel().getSelectedItem();
        if (selectedQuiz != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete Quiz");
            alert.setContentText("Are you sure you want to delete the quiz: " + selectedQuiz.getTitle() + "?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                deleteQuiz(selectedQuiz);
                loadQuizzes();
                quizListView.getItems().setAll(quizzes);
            }
        }
    });
    
    Button backButton = new Button("Back to Admin Dashboard");
    backButton.setOnAction(e -> primaryStage.setScene(adminScene));
    
    // Add selection listener to enable edit/delete buttons when a quiz is selected
    quizListView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                boolean hasSelection = newValue != null;
                editButton.setDisable(!hasSelection);
                deleteButton.setDisable(!hasSelection);
            });
    
    HBox buttonBox = new HBox(10, editButton, deleteButton, backButton);
    buttonBox.setAlignment(Pos.CENTER);
    
    root.getChildren().addAll(titleLabel, quizListView, buttonBox);
    
    return new Scene(root, 500, 400);
}

// Scene for viewing quiz statistics
private Scene createStatsScene() {
    VBox root = new VBox(15);
    root.setPadding(new Insets(20));
    root.setAlignment(Pos.CENTER);
    
    Label titleLabel = new Label("Quiz Statistics");
    titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
    
    TableView<QuizStat> statsTable = new TableView<>();
    statsTable.setPrefHeight(300);
    
    TableColumn<QuizStat, String> quizColumn = new TableColumn<>("Quiz Title");
    quizColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
    quizColumn.setPrefWidth(200);
    
    TableColumn<QuizStat, Integer> attemptsColumn = new TableColumn<>("Attempts");
    attemptsColumn.setCellValueFactory(cellData -> cellData.getValue().attemptsProperty().asObject());
    attemptsColumn.setPrefWidth(100);
    
    TableColumn<QuizStat, Double> avgScoreColumn = new TableColumn<>("Avg. Score (%)");
    avgScoreColumn.setCellValueFactory(cellData -> cellData.getValue().avgScoreProperty().asObject());
    avgScoreColumn.setPrefWidth(120);
    
    statsTable.getColumns().addAll(quizColumn, attemptsColumn, avgScoreColumn);
    
    Button backButton = new Button("Back to Admin Dashboard");
    backButton.setOnAction(e -> primaryStage.setScene(adminScene));
    
    root.getChildren().addAll(titleLabel, statsTable, backButton);
    
    // Load statistics data
    try {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT q.id, q.title, " +
                "COUNT(qa.id) as attempts, " +
                "AVG(qa.score * 100.0 / qa.total_questions) as avg_score " +
                "FROM quizzes q " +
                "LEFT JOIN quiz_attempts qa ON q.id = qa.quiz_id " +
                "GROUP BY q.id " +
                "ORDER BY q.title");
        
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String title = resultSet.getString("title");
            int attempts = resultSet.getInt("attempts");
            double avgScore = resultSet.getDouble("avg_score");
            
            statsTable.getItems().add(new QuizStat(id, title, attempts, avgScore));
        }
        
        resultSet.close();
        statement.close();
        
    } catch (SQLException e) {
        showAlert(Alert.AlertType.ERROR, "Database Error", 
                "Error loading statistics: " + e.getMessage());
        e.printStackTrace();
    }
    
    return new Scene(root, 500, 400);
}

// Helper method to delete a quiz
private void deleteQuiz(Quiz quiz) {
    try {
        connection.setAutoCommit(false);
        
        // First delete all quiz attempts
        PreparedStatement deleteAttemptsStatement = connection.prepareStatement(
                "DELETE FROM quiz_attempts WHERE quiz_id = ?");
        deleteAttemptsStatement.setInt(1, quiz.getId());
        deleteAttemptsStatement.executeUpdate();
        deleteAttemptsStatement.close();
        
        // Get all question IDs for this quiz
        PreparedStatement getQuestionsStatement = connection.prepareStatement(
                "SELECT id FROM questions WHERE quiz_id = ?");
        getQuestionsStatement.setInt(1, quiz.getId());
        ResultSet questionResults = getQuestionsStatement.executeQuery();
        
        while (questionResults.next()) {
            int questionId = questionResults.getInt("id");
            
            // Delete options for each question
            PreparedStatement deleteOptionsStatement = connection.prepareStatement(
                    "DELETE FROM options WHERE question_id = ?");
            deleteOptionsStatement.setInt(1, questionId);
            deleteOptionsStatement.executeUpdate();
            deleteOptionsStatement.close();
        }
        
        questionResults.close();
        getQuestionsStatement.close();
        
        // Delete all questions
        PreparedStatement deleteQuestionsStatement = connection.prepareStatement(
                "DELETE FROM questions WHERE quiz_id = ?");
        deleteQuestionsStatement.setInt(1, quiz.getId());
        deleteQuestionsStatement.executeUpdate();
        deleteQuestionsStatement.close();
        
        // Finally delete the quiz
        PreparedStatement deleteQuizStatement = connection.prepareStatement(
                "DELETE FROM quizzes WHERE id = ?");
        deleteQuizStatement.setInt(1, quiz.getId());
        deleteQuizStatement.executeUpdate();
        deleteQuizStatement.close();
        
        connection.commit();
        connection.setAutoCommit(true);
        
        showAlert(Alert.AlertType.INFORMATION, "Success", "Quiz deleted successfully");
        
    } catch (SQLException e) {
        try {
            connection.rollback();
            connection.setAutoCommit(true);
        } catch (SQLException rollbackEx) {
            rollbackEx.printStackTrace();
        }
        
        showAlert(Alert.AlertType.ERROR, "Database Error", 
                "Error deleting quiz: " + e.getMessage());
        e.printStackTrace();
    }
}

// Helper method to load a quiz for editing
private void loadQuizForEditing(Quiz quiz) {
    // Implementation would go here
    // For now, just show a message that this feature is not yet implemented
    showAlert(Alert.AlertType.INFORMATION, "Not Implemented", 
            "Quiz editing is not yet implemented in this version.");
}

// Helper classes for the admin functionality
class QuestionEdit {
    private String text;
    private List<OptionEdit> options;
    
    public QuestionEdit(String text, List<OptionEdit> options) {
        this.text = text;
        this.options = options;
    }
    
    public String getText() {
        return text;
    }
    
    public List<OptionEdit> getOptions() {
        return options;
    }
    
    @Override
    public String toString() {
        return text;
    }
}

class OptionEdit {
    private String text;
    private boolean correct;
    
    public OptionEdit(String text, boolean correct) {
        this.text = text;
        this.correct = correct;
    }
    
    public String getText() {
        return text;
    }
    
    public boolean isCorrect() {
        return correct;
    }
}

class QuizStat {
    private int id;
    private String title;
    private int attempts;
    private double avgScore;
    
    public QuizStat(int id, String title, int attempts, double avgScore) {
        this.id = id;
        this.title = title;
        this.attempts = attempts;
        this.avgScore = avgScore;
    }
    
    public javafx.beans.property.StringProperty titleProperty() {
        return new javafx.beans.property.SimpleStringProperty(title);
    }
    
    public javafx.beans.property.IntegerProperty attemptsProperty() {
        return new javafx.beans.property.SimpleIntegerProperty(attempts);
    }
    
    public javafx.beans.property.DoubleProperty avgScoreProperty() {
        return new javafx.beans.property.SimpleDoubleProperty(avgScore);
    }
}
    
    
    private Scene createHistoryScene() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("My Quiz History");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        TableView<QuizAttempt> historyTable = new TableView<>();
        historyTable.setPrefHeight(300);
        
        TableColumn<QuizAttempt, String> quizColumn = new TableColumn<>("Quiz Title");
        quizColumn.setCellValueFactory(cellData -> cellData.getValue().quizTitleProperty());
        quizColumn.setPrefWidth(200);
        
        TableColumn<QuizAttempt, String> scoreColumn = new TableColumn<>("Score");
        scoreColumn.setCellValueFactory(cellData -> cellData.getValue().scoreTextProperty());
        scoreColumn.setPrefWidth(100);
        
        TableColumn<QuizAttempt, String> dateColumn = new TableColumn<>("Date Taken");
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateTakenProperty());
        dateColumn.setPrefWidth(150);
        
        historyTable.getColumns().addAll(quizColumn, scoreColumn, dateColumn);
        
        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(e -> primaryStage.setScene(mainMenuScene));
        
        root.getChildren().addAll(titleLabel, historyTable, backButton);
        
        // Load history data
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT qa.id, q.title, qa.score, qa.total_questions, qa.date_taken " +
                    "FROM quiz_attempts qa " +
                    "JOIN quizzes q ON qa.quiz_id = q.id " +
                    "WHERE qa.user_id = ? " +
                    "ORDER BY qa.date_taken DESC");
            statement.setInt(1, currentUser.getId());
            
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String quizTitle = resultSet.getString("title");
                int score = resultSet.getInt("score");
                int totalQuestions = resultSet.getInt("total_questions");
                String dateTaken = resultSet.getString("date_taken");
                
                QuizAttempt attempt = new QuizAttempt(id, quizTitle, score, totalQuestions, dateTaken);
                historyTable.getItems().add(attempt);
            }
            
            resultSet.close();
            statement.close();
            
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", 
                    "Error loading quiz history: " + e.getMessage());
            e.printStackTrace();
        }
        
        return new Scene(root, 500, 400);
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}