import java.util.ArrayList;
import java.util.Date;

public class Mail {

    private String from;
    private ArrayList<Person> to;
    private String subject;
    private Date date;
    private String text;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public ArrayList<Person> getTo() {
        return to;
    }

    public void setTo(ArrayList<Person> to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
