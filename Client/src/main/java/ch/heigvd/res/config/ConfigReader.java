package ch.heigvd.res.config;

import ch.heigvd.res.GroupFactory;
import ch.heigvd.res.Prank;
import ch.heigvd.res.config.ConfigReadException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Config Reader reads the config from the files, and parse the data needed.
 * Throw runtimeException is something went wrong
 *@authors Aloïs Christen & Delphine Scherler
 * @date 2021/05/08
 */
public class ConfigReader {

    static final Logger LOG = Logger.getLogger(ConfigReader.class.getName());

    private static String DEFAULT_CONFIG_DIRECTORY = "./ressources/config";
    private static String EMAILS_FILE = "emails.json";
    private static String PRANKS_FILE = "pranks.json";
    private static String SERVER_CONFIG_FILE = "server_config.json";
    private String ipAddress;
    private int serverPort;
    private List<String> emails;
    private int nbGroups;
    private List<Prank> pranks;

    /**
     * Retrieve a JSONObject from a file
     * @param fileName The file containing the json
     * @return The parsed JSONObject
     * @throws ConfigReadException
     */
    public JSONObject getJsonObjet(String fileName) throws ConfigReadException {
        Path pathToFile = Paths.get(DEFAULT_CONFIG_DIRECTORY, fileName);
        File mockConfigFile = pathToFile.toFile();

        //Json parser to parse read file
        JSONParser parser = new JSONParser();

        try {
            BufferedReader br = new BufferedReader(new FileReader(mockConfigFile));
            return (JSONObject) parser.parse(br);
        } catch (IOException e) {
            throw new ConfigReadException("File " + fileName + " could not be read");
        } catch (ParseException e){
            throw new ConfigReadException("Data at  " + fileName + " could not be parsed");
        }

    }

    /**
     * Read all config files and parse the values
     * @throws ConfigReadException
     */
    public void readConfig() throws ConfigReadException {
        LOG.log(Level.INFO, "Reading config files...");
        Long longTmp;
        JSONObject obj;

        //Read server config file
        obj = getJsonObjet(SERVER_CONFIG_FILE);
        longTmp = (Long) obj.get("port");
        if (longTmp == null) {
            throw new ConfigReadException("port attribute doesn't exist in server_config.json");
        }
        this.serverPort = longTmp.intValue();


        this.ipAddress = (String) obj.get("ip_address");
        if (ipAddress == null) {
            throw new ConfigReadException("ip_address attribute doesn't exist in server_config.json");
        }

        //Read emails config file
        obj = getJsonObjet(EMAILS_FILE);
        longTmp = (Long) obj.get("number_of_groups");
        if (longTmp == null) {
            throw new ConfigReadException("number_of_groups attribute doesn't exist in emails.json");
        }
        this.nbGroups = longTmp.intValue();

        this.emails = (ArrayList<String>) obj.get("emails");
        if (emails == null) {
            throw new ConfigReadException("emails attribute doesn't exist in emails.json");
        }

        //Read pranks config file
        pranks = parsePranks();
    }

    /**
     * Parse pranks, using GSON.
     * @details as pranks is a Class and not a primitive type,
     * it's easier to use GSON to parse directly into a Prank object
     * @return the list of Pranks parsed
     * @throws ConfigReadException
     */
    public List<Prank> parsePranks() throws ConfigReadException {
        Path pathToFile = Paths.get(DEFAULT_CONFIG_DIRECTORY, PRANKS_FILE);
        File pranksFile = pathToFile.toFile();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(pranksFile));
        } catch(IOException e){
            throw new ConfigReadException("File " + PRANKS_FILE + " could not be read");
        }
        Gson gson = new Gson();
        Type prankListType = new TypeToken<ArrayList<Prank>>() {}.getType();
        try {
            ArrayList<Prank> prank = gson.fromJson(br.lines().collect(Collectors.joining("\n")), prankListType);
            return prank;
        } catch (JsonSyntaxException e){
            throw new ConfigReadException("Data in " + PRANKS_FILE + " could not be parsed");
        }

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
