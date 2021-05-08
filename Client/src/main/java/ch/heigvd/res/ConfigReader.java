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

    public ConfigReader() {
        // Contruire avec le chemin
        // Mettre des attributs privé, et faire une méthode qui va tout lire
        // Renvoyer les attributs quand demandés
    }

    public int getMockPort() {
        Path pathToFile = Paths.get(DEFAULT_CONFIG_DIRECTORY, "general_config.json");
        File mockConfigFile = pathToFile.toFile();

        //Json parser to parse read file
        JSONParser parser = new JSONParser();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(mockConfigFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //Read JSON file
        JSONObject obj = null;
        try {
            obj = (JSONObject) parser.parse(br);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Long port = (Long) obj.get("port");
        if (port == null) {
            throw new RuntimeException("port attribute doesn't exist in general_config.json");
        }
        return port.intValue();

    }

    public List<String> getAllEmails() throws IOException, ParseException {
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
        Path pathToFile = Paths.get(DEFAULT_CONFIG_DIRECTORY, "general_config.json");
        File groupsFile = pathToFile.toFile();

        BufferedReader br = new BufferedReader(new FileReader(groupsFile));

        //Read JSON file and create ch.heigvd.res.Group
        JSONParser parser = new JSONParser();

        //Read JSON file
        JSONObject obj = (JSONObject) parser.parse(br);

        Long nbGroups = (Long) obj.get("number_of_groups");
        if (nbGroups == null) {
            throw new RuntimeException("number_of_groups attribute doesn't exist in general_config.json");
        }
        return nbGroups.intValue();

    }

    public List<Prank> getPranks() throws IOException {
        Path pathToFile = Paths.get(DEFAULT_CONFIG_DIRECTORY, "pranks.json");
        File pranksFile = pathToFile.toFile();

        BufferedReader br = new BufferedReader(new FileReader(pranksFile));

        //Read JSON file and create Joke
        Gson gson = new Gson();
        Type prankListType = new TypeToken<ArrayList<Prank>>() {}.getType();

        ArrayList<Prank> prank = gson.fromJson(br.lines().collect(Collectors.joining("\n")), prankListType);
        return prank;

    }

}
