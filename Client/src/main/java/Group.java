import java.util.ArrayList;

public class Group {
    private int size;
    private ArrayList<Person> recepters;
    private Person sender;

    public Group(ArrayList<Person> recepters, Person sender) {
        this.size = 1 + recepters.size();
        this.recepters = recepters;
        this.sender = sender;
    }

    public int getSize() {
        return size;
    }

    public ArrayList<Person> getRecepters() {
        return recepters;
    }

    public Person getSender() {
        return sender;
    }
}
