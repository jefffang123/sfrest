package sfrest;

import java.io.Serializable;

public class QueryLocator implements Serializable {

    private static final long serialVersionUID = 8925657334136850086L;

    private String key;

    public String getKey() {
        return key;
    }

    public QueryLocator(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "QueryLocator [key=" + key + "]";
    }
}
