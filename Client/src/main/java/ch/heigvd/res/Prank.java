package ch.heigvd.res;

/**
 * Represents a prank, with a subjet and message
 * @authors Aloïs Christen & Delphine Scherler
 * @date 2021/05/08
 */
public class Prank {
    private String subject;
    private String message;


    public Prank(String subject, String message) {
        this.message = message;
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubject(){ return subject;}

    public void setSubject(String subject){this.subject = subject;}
}
