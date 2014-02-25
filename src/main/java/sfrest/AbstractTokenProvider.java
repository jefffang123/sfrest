package sfrest;

import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractTokenProvider implements TokenProvider {

    protected Environment environment;
    protected String clientId;
    protected String clientSecret; // Not used for User-Agent OAuth Authentication Flow.

    @Override
    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public String getClientId() {
        return clientId;
    }

    @Value("${sfrest.client.id}")
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    @Value("${sfrest.client.secret}")
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @Override
    public boolean isRefreshable(Token token) {
        return token.getRefreshToken() != null;
    }

    @Override
    public void refreshToken(Token token) {
        // TODO:
        throw new UnsupportedOperationException();
    }

    @Override
    public void revokeToken(Token token, boolean includingRefreshToken) {
        // TODO:
    }
}
