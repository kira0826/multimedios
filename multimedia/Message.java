package multimedia;

import java.io.Serializable;

public class Message implements  Serializable {

    private String value;
    private String from; 
    private String to;

    



    public Message(String value, String from, String to) {
        this.value = value;
        this.from = from;
        this.to = to;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
    } 


    
}
