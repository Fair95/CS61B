package gitlet;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Branch implements Serializable {
    public String name;
    public Commit HEAD;
    public Map<String, Commit> history = new HashMap<>();
    Branch() {
        makeDefaultCommit();
    }
    Branch(String name, Commit HEAD, Map<String, Commit> history){
        this.name = name;
        this.HEAD = HEAD;
        this.history = history;
    }
    public void makeDefaultCommit() {
        Commit c = new Commit();
        c.saveCommit();

        name = "master";
        history.put(c.generateSha(), c);
        HEAD = c;
    }
    public Commit commit(String msg, StagingArea SA) {
        Commit c = HEAD.makeCommit(SA, msg);
        history.put(c.generateSha(), c);
        return c;
    }

    public Map<String, Commit> getHistory() {
        return history;
    }
}
