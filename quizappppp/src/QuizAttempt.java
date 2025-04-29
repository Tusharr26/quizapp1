import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class QuizAttempt {
    private int id;
    private StringProperty quizTitle;
    private int score;
    private int totalQuestions;
    private StringProperty scoreText;
    private StringProperty dateTaken;
    
    public QuizAttempt(int id, String quizTitle, int score, int totalQuestions, String dateTaken) {
        this.id = id;
        this.quizTitle = new SimpleStringProperty(quizTitle);
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.scoreText = new SimpleStringProperty(score + "/" + totalQuestions + 
                " (" + String.format("%.1f", (score * 100.0) / totalQuestions) + "%)");
        this.dateTaken = new SimpleStringProperty(dateTaken);
    }
    
    public int getId() {
        return id;
    }
    
    public StringProperty quizTitleProperty() {
        return quizTitle;
    }
    
    public StringProperty scoreTextProperty() {
        return scoreText;
    }
    
    public StringProperty dateTakenProperty() {
        return dateTaken;
    }
}