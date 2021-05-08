package ch.heigvd.res;

import ch.heigvd.res.config.ConfigReader;

import java.time.Instant;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Make and send the pranks, based on the pranks
 * and group given at creation
 * @authors Aloïs Christen & Delphine Scherler
 * @date 2021/05/08
 */
public class PrankSender {

    static final Logger LOG = Logger.getLogger(PrankSender.class.getName());

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

    /**
     * Generate all mails needed for one particular prank
     * @param group The target group
     * @param prank The prank to send
     * @return The list of mails needed to be send
     */
    private List<Mail> generateMail(Group group, Prank prank){
        List<Mail> mails = new ArrayList<>();

        // we choose to send one mail per recepters,
        // so they won't see other receiving the mail
        for (String email : group.recepters){
            Mail mail = new Mail();
            mail.mail_from = group.sender;
            mail.data_from = group.sender;
            mail.rcpt_to = Collections.singletonList(email);
            mail.data_to = Collections.singletonList(email);
            mail.text = prank.getMessage();
            mail.subject = prank.getSubject();
            mail.date = new Date(); // today
            mails.add(mail);
        }
        return mails;
    }

    /**
     * For each group, send one prank at random
     */
    public void sendPranks(){
        LOG.log(Level.INFO, "Sending pranks...");
        for (Group group : groups){
            int numPrank = rand.nextInt(pranks.size());
            List<Mail> mails = generateMail(group, pranks.get(numPrank));
            for(Mail mail : mails){
                clientSMTP.sendMail(mail);
            }
        }
        LOG.log(Level.INFO, "All pranks sent.");
    }

}
