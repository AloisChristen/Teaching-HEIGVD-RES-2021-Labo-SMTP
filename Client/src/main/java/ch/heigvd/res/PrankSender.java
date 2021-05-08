package ch.heigvd.res;

import java.time.Instant;
import java.util.*;

public class PrankSender {

    private List<Group> groups;
    private List<Prank> pranks;
    private ClientSMTP clientSMTP;
    private Random rand;

    public PrankSender(List<Group> groups, List<Prank> pranks, ClientSMTP clientSMTP) {
        this.groups = groups;
        this.pranks = pranks;
        this.clientSMTP = clientSMTP;
        rand = new Random(Instant.now().toEpochMilli());
    }

    private List<Mail> generateMail(Group group, Prank prank){
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

    public void sendPranks(){
        for (Group group : groups){
            int numPrank = rand.nextInt(pranks.size());
            List<Mail> mails = generateMail(group, pranks.get(numPrank));
            for(Mail mail : mails){
                clientSMTP.sendMail(mail);
            }
        }
    }

}
