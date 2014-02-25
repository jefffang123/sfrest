package sfrest.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sfrest.AbstractTokenProvider;
import sfrest.SFRestTemplate;
import sfrest.Token;

/**
 * This class implements requesting access token step of the <a href="http://www.salesforce.com/us/developer/docs/api_rest/Content/intro_understanding_web_server_oauth_flow.htm">Web server OAuth Authentication Flow</a>.
 *
 * @see WebAuthController
 */
@Component
public class WebAuthTokenProvider extends AbstractTokenProvider {

    static final String SF_AUTH_CODE_KEY = "SF_AUTH_CODE";

    private String redirectUri;

    public String getRedirectUri() {
        return redirectUri;
    }

    @Value("${sfrest.redirect.uri}")
    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    @Override
    public Token requestToken() {
        String authCode = findAuthCode();
        if (authCode == null) {
            throw new IllegalStateException("Can't find auth code");
        }

        SFRestTemplate rt = new SFRestTemplate();
        rt.setErrorFieldMapping("error", "error_description");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("code", authCode);

        return rt.postForObject(environment.getTokenURI(), params, Token.class);
    }

    public String findAuthCode() {
        return (String) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute(SF_AUTH_CODE_KEY);
    }
}
