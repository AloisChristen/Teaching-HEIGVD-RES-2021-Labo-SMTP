package ch.heigvd.res;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe generating random groups from a list of emails
 * @authors Aloïs Christen & Delphine Scherler
 * @date 2021/05/08
 */
public class GroupFactory {

    static final Logger LOG = Logger.getLogger(GroupFactory.class.getName());

    private static final int minSize = 3;
    private static final int maxSize = 10;
    private Random rand;
    private List<String> emails;

    /**
     * Contstruct the factory
     * @param emails the emails the factory use to make the groups
     */
    public GroupFactory(List<String> emails){
        this.emails = emails;
        rand = new Random(Instant.now().toEpochMilli());
    }

    /**
     * Generate some new random groups
     * @param nbOfGroups the number of groups we want to generate
     * @return The groups generated
     */
    public List<Group> generateGroups(int nbOfGroups){
        LOG.log(Level.INFO, "Generating groups...");
        List<Group> groups = new ArrayList<>();
        for(int i = 0; i < nbOfGroups; i++){
            int groupSize = rand.nextInt(maxSize - minSize) + minSize;
            List<String> recepterEmails = new ArrayList<>();
            String senderEmail = selectOneEmail();
            for(int j = 1; j < groupSize; j++){
                recepterEmails.add(selectOneEmail());
            }
            // put emails back in the list
            emails.add(senderEmail);
            for(String email : recepterEmails){
                emails.add(email);
            }
            groups.add(new Group(senderEmail, recepterEmails));
        }
        return groups;
    }

    /**
     * Select one email from the list, and take it of the list,
     * so it won't be selected again for the same group
     * @return The selected email
     */
    private String selectOneEmail(){
        int randIndex = rand.nextInt(emails.size());
        String email = emails.get(randIndex);
        emails.remove(randIndex);
        return email;
    }

}
