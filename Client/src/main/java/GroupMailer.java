import java.util.Collections;
import java.util.Date;
import java.util.List;

public class GroupMailer {

//    public GroupMailer(Group group, String joke) {
//        this.email = email;
//        this.smtpServer = smtpServer;
//    }

    public static Mail generateMail(Group group, Prank prank){
        Mail mail = new Mail();
        List<String> emails = group.getEmails();
        mail.setFrom(emails.get(0));
        emails.remove(0);
        mail.setTo(emails);
        mail.setText(prank.getMessage());
        mail.setSubject(prank.getSubject());
        mail.setDate(new Date());
        return mail;
    }
}
