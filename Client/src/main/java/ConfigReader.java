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
    private String configDirectory;

    public ConfigReader() {
        this(DEFAULT_CONFIG_DIRECTORY);
    }

    public ConfigReader(String configDirectory) {
        this.configDirectory = configDirectory;
    }

    public int getMockPort() throws IOException, ParseException {
        Path pathToFile = Paths.get(DEFAULT_CONFIG_DIRECTORY, "general_config.json");
        File mockConfigFile = pathToFile.toFile();

        //Json parser to parse read file
        JSONParser parser = new JSONParser();
        BufferedReader br = new BufferedReader(new FileReader(mockConfigFile));

        //Read JSON file
        JSONObject obj = (JSONObject) parser.parse(br);
        return ((Long) obj.get("port")).intValue();

    }

    public List<String> getAllEmails() throws IOException, ParseException {
        Path pathToFile = Paths.get(DEFAULT_CONFIG_DIRECTORY, "email_contacts.json");
        File contactsFile = pathToFile.toFile();

        JSONParser parser = new JSONParser();
        BufferedReader br = new BufferedReader(new FileReader(contactsFile));

        //Read JSON file and create Contact
        JSONObject obj = (JSONObject) parser.parse(br);
        Type stringListType = new TypeToken<ArrayList<String>>(){}.getType();
        ArrayList<String> emails = (ArrayList<String>) obj.get("emails");

        return emails;
    }

    public int getNBGroups() throws IOException, ParseException {
        Path pathToFile = Paths.get(DEFAULT_CONFIG_DIRECTORY, "general_config.json");
        File groupsFile = pathToFile.toFile();

        BufferedReader br = new BufferedReader(new FileReader(groupsFile));

        //Read JSON file and create Group
        JSONParser parser = new JSONParser();

        //Read JSON file
        JSONObject obj = (JSONObject) parser.parse(br);
        return ((Long) obj.get("number_of_groups")).intValue();
    }

    public List<Prank> getPranks() throws IOException {
        Path pathToFile = Paths.get(DEFAULT_CONFIG_DIRECTORY, "pranks.json");
        File pranksFile = pathToFile.toFile();

        BufferedReader br = new BufferedReader(new FileReader(pranksFile));

        //Read JSON file and create Joke
        Gson gson = new Gson();
        Type prankListType = new TypeToken<ArrayList<Prank>>(){}.getType();
        ArrayList<Prank> prank = gson.fromJson(br.lines().collect(Collectors.joining("\n")), prankListType);

        return prank;
    }


}
