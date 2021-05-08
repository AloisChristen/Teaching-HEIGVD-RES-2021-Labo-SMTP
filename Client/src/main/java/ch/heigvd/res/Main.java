package ch.heigvd.res;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ParseException {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");

        ConfigReader configReader = new ConfigReader();
        try{
            configReader.readConfig();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        ClientSMTP clientSMTP = new ClientSMTP(configReader.getIpAddress(), configReader.getServerPort());
        GroupFactory groupFactory = new GroupFactory(configReader.getEmails());
        List<Group> groups = groupFactory.generateGroups(configReader.getNbGroups());
        List<Prank> pranks = configReader.getPranks();

        PrankSender prankSender = new PrankSender(groups, pranks, clientSMTP);
        prankSender.sendPranks();

    }
}
