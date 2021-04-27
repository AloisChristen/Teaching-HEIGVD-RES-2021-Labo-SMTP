import java.util.List;
import java.util.Random;

public class JokeFactory {

    private List<String> kwownJokes;

    public Joke getJoke(){
        int nextJoke = new Random().nextInt(kwownJokes.size());
        return new Joke(kwownJokes.get(nextJoke));
    }

    public void setKwownJokes(List<String> kwownJokes) {
        this.kwownJokes = kwownJokes;
    }
}
