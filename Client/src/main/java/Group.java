import java.util.List;

public class Group {
    private String name;
    private List<String> recepters;

    public Group(String name, List<String> recepters) {
        this.name = name;
        this.recepters = recepters;
    }

    public String getName() {
        return name;
    }

    public List<String> getRecepters() {
        return recepters;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRecepters(List<String> recepters) {
        this.recepters = recepters;
    }
}
