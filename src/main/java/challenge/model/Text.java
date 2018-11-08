package challenge.model;

public class Text extends Content{

    String text;

    public Text(String text){
        super("text");
        this.text = text;
    }

    public Text(String type, String text){
        super("text");
        this.text = text;
    }

    public Text(){
        super("text");
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }




}