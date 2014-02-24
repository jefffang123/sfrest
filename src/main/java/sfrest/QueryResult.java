package sfrest;

import java.util.List;
import java.util.Map;

public class QueryResult {

    private int totalSize;
    private boolean done;
    private List<Map<String, ?>> records;
    private QueryLocator queryLocator;

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public List<Map<String, ?>> getRecords() {
        return records;
    }

    public void setRecords(List<Map<String, ?>> records) {
        this.records = records;
    }

    public QueryLocator getQueryLocator() {
        return queryLocator;
    }

    public void setQueryLocator(QueryLocator queryLocator) {
        this.queryLocator = queryLocator;
    }

}
