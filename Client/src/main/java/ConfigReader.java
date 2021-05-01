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
        Path pathToFile = Paths.get(DEFAULT_CONFIG_DIRECTORY, "mock_config.json");
        File mockConfigFile = pathToFile.toFile();

        //Json parser to parse read file
        JSONParser parser = new JSONParser();
        BufferedReader br = new BufferedReader(new FileReader(mockConfigFile));

        //Read JSON file
        JSONObject obj = (JSONObject) parser.parse(br);
        return ((Long) obj.get("port")).intValue();

    }

    public List<Contact> getAllContacts() throws IOException {
        Path pathToFile = Paths.get(DEFAULT_CONFIG_DIRECTORY, "email_contacts.json");
        File contactsFile = pathToFile.toFile();

        BufferedReader br = new BufferedReader(new FileReader(contactsFile));

        //Read JSON file and create Contact
        Gson gson = new Gson();
        Type contactListType = new TypeToken<ArrayList<Contact>>(){}.getType();
        ArrayList<Contact> contacts = gson.fromJson(br.lines().collect(Collectors.joining("\n")), contactListType);

        return contacts;
    }

    public List<Group> getGroups() throws IOException {
        Path pathToFile = Paths.get(DEFAULT_CONFIG_DIRECTORY, "email_address_group.json");
        File groupsFile = pathToFile.toFile();

        BufferedReader br = new BufferedReader(new FileReader(groupsFile));

        //Read JSON file and create Group
        Gson gson = new Gson();
        Type GroupListType = new TypeToken<ArrayList<Group>>(){}.getType();
        ArrayList<Group> groups = gson.fromJson(br.lines().collect(Collectors.joining("\n")), GroupListType);

        return groups;
    }

    public List<Joke> getJokes() throws IOException {
        Path pathToFile = Paths.get(DEFAULT_CONFIG_DIRECTORY, "jokes.json");
        File jokesFile = pathToFile.toFile();

        BufferedReader br = new BufferedReader(new FileReader(jokesFile));

        //Read JSON file and create Joke
        Gson gson = new Gson();
        Type JokeType = new TypeToken<ArrayList<Joke>>(){}.getType();
        ArrayList<Joke> joke = gson.fromJson(br.lines().collect(Collectors.joining("\n")), JokeType);

        return joke;
    }


}
