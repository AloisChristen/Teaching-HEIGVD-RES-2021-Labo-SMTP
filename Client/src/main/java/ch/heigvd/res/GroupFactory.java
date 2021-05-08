package ch.heigvd.res;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GroupFactory {

    private static final int minSize = 3;
    private static final int maxSize = 10;
    private Random rand;
    private List<String> emails;

    public GroupFactory(){
    rand = new Random(Instant.now().toEpochMilli());
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public List<Group> generateGroups(int nbOfGroups){
        List<Group> groups = new ArrayList<>();
        for(int i = 0; i < nbOfGroups; i++){
            int groupSize = rand.nextInt(maxSize - minSize) + minSize;
            List<String> chosenEmails = new ArrayList<>();
            for(int j = 0; j < groupSize; j++){
                int randIndex = rand.nextInt(emails.size());
                chosenEmails.add(emails.get(randIndex));
                emails.remove(randIndex);
            }
            for(String email : chosenEmails){
                emails.add(email);
            }
            groups.add(new Group(chosenEmails));
        }
        return groups;
    }

}
