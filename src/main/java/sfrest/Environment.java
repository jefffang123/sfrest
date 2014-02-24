package sfrest;

import java.net.URI;

/**
 * Salesforce environment, built-in environments are {@code Production} and {@code Sandbox}.
 * <p>
 * If login via custom domain is mandatory, can add additional environments using static {@code init} method.
 * </p>
 */
public class Environment {

    public static final Environment PRODUCTION = new Environment("Production", "login.salesforce.com");
    public static final Environment SANDBOX = new Environment("Sandbox", "test.salesforce.com");

    private static Environment[] ALL_ENVS = {PRODUCTION, SANDBOX};

    /**
     * Override default environments to support custom domain etc.
     */
    public static void init(Environment... envs) {
        ALL_ENVS = envs;
    }

    public static Environment[] all() {
        return ALL_ENVS;
    }

    private String name;
    private String host;

    public Environment(String name, String host) {
        this.name = name;
        this.host = host;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    /**
     * For authorization, e.g. https://login.salesforce.com/services/oauth2/authorize
     */
    public URI getAuthURI() {
        return getBaseURI().resolve("authorize");
    }

    /**
     * For token requests, e.g. https://login.salesforce.com/services/oauth2/token
     */
    public URI getTokenURI() {
        return getBaseURI().resolve("token");
    }

    /**
     * For revoking OAuth tokens, e.g. https://login.salesforce.com/services/oauth2/revoke
     */
    public URI getRevokeTokenURI() {
        return getBaseURI().resolve("revoke");
    }

    private URI getBaseURI() {
        return URI.create("https://" + host + "/services/oauth2/");
    }

    public static Environment byName(String name) {
        for (Environment env : all()) {
            if (env.name.equals(name)) {
                return env;
            }
        }
        return null;
    }

    public String toString() {
        return name;
    }
}
