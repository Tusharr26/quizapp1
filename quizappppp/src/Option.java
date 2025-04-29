public class Option {
    private int id;
    private String text;
    private boolean isCorrect;
    
    public Option(int id, String text, boolean isCorrect) {
        this.id = id;
        this.text = text;
        this.isCorrect = isCorrect;
    }
    
    public int getId() {
        return id;
    }
    
    public String getText() {
        return text;
    }
    
    public boolean isCorrect() {
        return isCorrect;
    }
}