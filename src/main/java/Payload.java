import io.searchbox.annotations.JestId;

/**
 * Created by kristopherstevens on 17/03/2017.
 */
public class Payload {

    @JestId
    private final String id;

    private final String name;

    public Payload(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
