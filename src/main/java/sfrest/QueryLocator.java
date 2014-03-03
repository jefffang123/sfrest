package sfrest;

import java.io.Serializable;

public class QueryLocator implements Serializable {

    private String key;

    public String getKey() {
        return key;
    }

    QueryLocator(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "QueryLocator [key=" + key + "]";
    }
}
