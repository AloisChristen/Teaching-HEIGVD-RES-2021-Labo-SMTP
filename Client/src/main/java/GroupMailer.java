import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class GroupMailer {

//    public GroupMailer(Group group, String joke) {
//        this.email = email;
//        this.smtpServer = smtpServer;
//    }

    public static List<Mail> generateMail(Group group, Prank prank){
        List<Mail> mails = new ArrayList<>();
        List<String> emails = group.getEmails();
        String sender = emails.get(0);
        emails.remove(0);

        for (String email : emails){
            Mail mail = new Mail();
            mail.setFrom(sender);
            mail.setTo(Collections.singletonList(email));
            mail.setText(prank.getMessage());
            mail.setSubject(prank.getSubject());
            mail.setDate(new Date());
            mails.add(mail);
        }
        return mails;
    }
}
