package sfrest.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;
import sfrest.Environment;
import sfrest.Token;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This controller implements requesting authorization code step of the <a href="http://www.salesforce.com/us/developer/docs/api_rest/Content/intro_understanding_web_server_oauth_flow.htm">Web server OAuth Authentication Flow</a>.
 *
 * @see WebAuthTokenProvider
 */
@Controller
@RequestMapping("/sf-login")
public class WebAuthController {

    private static final Logger logger = LoggerFactory.getLogger(WebAuthController.class);

    private String postLoginUri;
    private boolean localEnabled;
    private WebAuthRestClient client;

    @Value("${sfrest.postLogin.uri}")
    public void setPostLoginUri(String postLoginUri) {
        this.postLoginUri = postLoginUri;
    }

    @Value("${sfrest.local.enabled}")
    public void setLocalEnabled(boolean localEnabled) {
        this.localEnabled = localEnabled;
    }

    @Autowired
    public void setRestClient(WebAuthRestClient client) {
        this.client = client;
    }

    @RequestMapping(method = POST)
    public String login(String env, HttpServletRequest request) {
        logger.debug("Login request received, env: '{}', state: '{}'", env);

        Environment sfEnv = Environment.byName(env);
        WebAuthTokenProvider tokenProvider = getTokenProvider();
        tokenProvider.setEnvironment(sfEnv);

        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUri(sfEnv.getAuthURI())
                .queryParam("response_type", "code")
                .queryParam("client_id", tokenProvider.getClientId())
                .queryParam("redirect_uri", tokenProvider.getRedirectUri())
                .queryParam("display", "popup");

        // TODO: Support client provided UI state.
        if (localEnabled) {
            String url = request.getRequestURL().toString();
            if (!url.equals(tokenProvider.getRedirectUri())) {
                urlBuilder.queryParam("state", url);
            }
        }

        return "redirect:" + urlBuilder.build(true).toUriString();
    }

    @RequestMapping(method = GET)
    public String loginCallback(String code, @RequestParam(required = false) String state, HttpServletRequest request) {
        logger.debug("Login callback received, code: '{}', state: '{}'", code, state);

        if (localEnabled && state != null) {
            UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(state).queryParam("code", code);
            return "redirect:" + urlBuilder.build(true).toUriString();
        }

        request.setAttribute(WebAuthTokenProvider.SF_AUTH_CODE_KEY, code);
        Token token = getTokenProvider().requestToken(client.getRestTemplate());
        client.getTokenStorage().saveToken(token);

        return "redirect:" + postLoginUri;
    }

    private WebAuthTokenProvider getTokenProvider() {
        return (WebAuthTokenProvider) client.getTokenProvider();
    }
}
