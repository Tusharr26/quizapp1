import java.util.ArrayList;
import java.util.List;

public class Question {
    private int id;
    private String text;
    private List<Option> options;
    
    public Question(int id, String text) {
        this.id = id;
        this.text = text;
        this.options = new ArrayList<>();
    }
    
    public int getId() {
        return id;
    }
    
    public String getText() {
        return text;
    }
    
    public List<Option> getOptions() {
        return options;
    }
}