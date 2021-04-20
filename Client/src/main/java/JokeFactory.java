import java.util.ArrayList;
import java.util.Random;

public class JokeFactory {

    private ArrayList<String> kwownJokes;

    public Joke getJoke(){
        int nextJoke = new Random().nextInt(kwownJokes.size());
        return new Joke(kwownJokes.get(nextJoke));
    }

}
