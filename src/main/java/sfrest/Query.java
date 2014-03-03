package sfrest;

import org.springframework.core.style.ToStringCreator;

import java.io.Serializable;

public class Query implements Serializable {

    private String soql;
    private String nextUri;

    public String getSoql() {
        return soql;
    }

    public String getNextUri() {
        return nextUri;
    }

    public void setNextUri(String nextUri) {
        this.nextUri = nextUri;
    }

    public Query(String soql) {
        this.soql = soql;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("soql", soql)
                .append("nextUri", nextUri)
                .toString();
    }
}
