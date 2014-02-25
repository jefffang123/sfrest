package sfrest;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URISyntaxException;

public abstract class AbstractTokenProvider implements TokenProvider {

    protected Environment environment;
    protected String clientId;
    protected String clientSecret;

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

    /**
     * When access token get timed out, can use this method to get a new access token.
     * <p>
     * This only works for web server OAuth authentication flow and user-agent flow, not for username-password OAuth authentication flow.
     * </p>
     *
     * @see <a href="http://www.salesforce.com/us/developer/docs/api_rest/Content/intro_understanding_refresh_token_oauth.htm">Understanding the OAuth Refresh Token Process</a>
     */
    @Override
    public void refreshToken(Token token) {
        // TODO:
        throw new UnsupportedOperationException();
    }

    @Override
    public void revokeToken(Token token) {
        HttpClient client = new DefaultHttpClient();
        URIBuilder builder = new URIBuilder(environment.getRevokeTokenURI());
        builder.addParameter("token", token.getAccessToken());

        try {
            HttpGet get = new HttpGet(builder.build());
            HttpResponse response = client.execute(get);
            EntityUtils.consume(response.getEntity());
        } catch (URISyntaxException | IOException e) {
            throw new SFException("Revoke Token failed", e);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}
