package ch.heigvd.res;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigReader {

    private static String DEFAULT_CONFIG_DIRECTORY = "./ressources/config";
    private static String EMAILS_FILE = "emails.json";
    private static String PRANKS_FILE = "pranks.json";
    private static String SERVER_CONFIG_FILE = "server_config.json";
    private String ipAddress;
    private int serverPort;
    private List<String> emails;
    private int nbGroups;
    private List<Prank> pranks;

    public ConfigReader() {
    }

    public JSONObject getJsonObjet(String fileName) throws IOException, ParseException {
        Path pathToFile = Paths.get(DEFAULT_CONFIG_DIRECTORY, fileName);
        File mockConfigFile = pathToFile.toFile();

        //Json parser to parse read file
        JSONParser parser = new JSONParser();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(mockConfigFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return (JSONObject) parser.parse(br);
    }

    public void readConfig() throws IOException, ParseException {
        Long longTmp;
        JSONObject obj;

        //Read server config file
        obj = getJsonObjet(SERVER_CONFIG_FILE);
        longTmp = (Long) obj.get("port");
        if (longTmp == null) {
            throw new RuntimeException("port attribute doesn't exist in server_config.json");
        }
        this.serverPort = longTmp.intValue();
        this.ipAddress = (String) obj.get("ip_address");

        //Read emails config file
        obj = getJsonObjet(EMAILS_FILE);
        longTmp = (Long) obj.get("number_of_groups");
        if (longTmp == null) {
            throw new RuntimeException("number_of_groups attribute doesn't exist in emails.json");
        }
        this.nbGroups = longTmp.intValue();
        this.emails = (ArrayList<String>) obj.get("emails");

        //Read pranks config file
        try {
            pranks = parsePranks();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



   /* public List<String> getAllEmails() throws IOException, ParseException {
        Path pathToFile = Paths.get(DEFAULT_CONFIG_DIRECTORY, "emails.json");
        File contactsFile = pathToFile.toFile();

        JSONParser parser = new JSONParser();
        BufferedReader br = new BufferedReader(new FileReader(contactsFile));

        //Read JSON file and create Contact
        JSONObject obj = (JSONObject) parser.parse(br);
        ArrayList<String> emails = (ArrayList<String>) obj.get("emails");
        if (emails == null) {
            throw new RuntimeException("emails attribute doesn't exist in emails.json");
        }

        return emails;
    }

    public int getNBGroups() throws IOException, ParseException {
        Path pathToFile = Paths.get(DEFAULT_CONFIG_DIRECTORY, "emails.json");
        File groupsFile = pathToFile.toFile();

        BufferedReader br = new BufferedReader(new FileReader(groupsFile));

        JSONParser parser = new JSONParser();

        //Read JSON file
        JSONObject obj = (JSONObject) parser.parse(br);

        Long nbGroups = (Long) obj.get("number_of_groups");
        if (nbGroups == null) {
            throw new RuntimeException("number_of_groups attribute doesn't exist in server_config.json");
        }
        return nbGroups.intValue();

    }
*/
    public List<Prank> parsePranks() throws IOException {
        Path pathToFile = Paths.get(DEFAULT_CONFIG_DIRECTORY, PRANKS_FILE);
        File pranksFile = pathToFile.toFile();

        BufferedReader br = new BufferedReader(new FileReader(pranksFile));

        //Read JSON file and create Joke
        Gson gson = new Gson();
        Type prankListType = new TypeToken<ArrayList<Prank>>() {}.getType();

        ArrayList<Prank> prank = gson.fromJson(br.lines().collect(Collectors.joining("\n")), prankListType);
        return prank;

    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public List<String> getEmails() {
        return emails;
    }

    public int getNbGroups() {
        return nbGroups;
    }

    public List<Prank> getPranks() {
        return pranks;
    }
}
