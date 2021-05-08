package ch.heigvd.res;

import ch.heigvd.res.config.ConfigReadException;
import ch.heigvd.res.config.ConfigReader;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    static final Logger LOG = Logger.getLogger(Main.class.getName());

    /**
     * Class main is responsible to instanciate all classes
     * and init the pranks
     * @authors Aloïs Christen & Delphine Scherler
     * @date 2021/05/08
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ParseException {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");

        // Get config
        LOG.log(Level.INFO, "-- INIT --");
        ConfigReader configReader = new ConfigReader();
        try{
            configReader.readConfig();
        } catch (ConfigReadException e) {
            LOG.log(Level.SEVERE, e.getMessage());
            return;
        }

        // Create objects
        ClientSMTP clientSMTP = new ClientSMTP(configReader.getIpAddress(), configReader.getServerPort());
        GroupFactory groupFactory = new GroupFactory(configReader.getEmails());
        List<Group> groups = groupFactory.generateGroups(configReader.getNbGroups());
        List<Prank> pranks = configReader.getPranks();
        PrankSender prankSender = new PrankSender(groups, pranks, clientSMTP);

        // Send pranks
        LOG.log(Level.INFO, "-- END OF INIT --");
        prankSender.sendPranks();
        LOG.log(Level.INFO, "-- QUITTING --");

    }
}
