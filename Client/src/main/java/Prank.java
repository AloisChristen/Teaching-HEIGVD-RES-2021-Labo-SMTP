public class Prank {
    private String message;
    private String subject;

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
