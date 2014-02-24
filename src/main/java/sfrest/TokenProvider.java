package sfrest;

public interface TokenProvider {

    Environment getEnvironment();

    Token requestToken();

    boolean isRefreshable(Token token);

    void refreshToken(Token token);

    void revokeToken(Token token);
}
