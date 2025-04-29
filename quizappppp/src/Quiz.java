import java.util.ArrayList;
import java.util.List;

public class Quiz {
    private int id;
    private String title;
    private String description;
    private int questionCount;
    private List<Question> questions;
    
    public Quiz(int id, String title, String description, int questionCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.questionCount = questionCount;
        this.questions = new ArrayList<>();
    }
    
    public int getId() {
        return id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getQuestionCount() {
        return questionCount;
    }
    
    public List<Question> getQuestions() {
        return questions;
    }
    
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
    
    @Override
    public String toString() {
        return title + " (" + questionCount + " questions)";
    }
}