import java.util.List;

public class Group {
    private List<String> emails;

    public Group(List<String> recepters) {
        this.emails = recepters;
    }


    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }
}
