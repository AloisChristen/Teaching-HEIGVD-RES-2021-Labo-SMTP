package ch.heigvd.res;

import java.util.List;

/**
 * Represent a group about to get pranked
 * @authors Aloïs Christen & Delphine Scherler
 * @date 2021/05/08
 */
public class Group {
    public String sender;
    public List<String> recepters;

    public Group(String sender, List<String> recepters) {
        this.sender = sender;
        this.recepters = recepters;
    }

}
