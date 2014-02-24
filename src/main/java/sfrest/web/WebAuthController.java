package sfrest.web;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sfrest.Environment;
import sfrest.Token;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/sf-login")
public class WebAuthController {

    private static final Logger logger = LoggerFactory.getLogger(WebAuthController.class);

    private String postLoginUri;
    private boolean localEnabled;
    private WebAuthTokenProvider tokenProvider;
    private SessionTokenStorage tokenStorage;

    @Value("${sfrest.postLogin.uri}")
    public void setPostLoginUri(String postLoginUri) {
        this.postLoginUri = postLoginUri;
    }

    @Value("${sfrest.local.enabled}")
    public void setLocalEnabled(boolean localEnabled) {
        this.localEnabled = localEnabled;
    }

    @Autowired
    public void setTokenProvider(WebAuthTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Autowired
    public void setTokenStorage(SessionTokenStorage tokenStorage) {
        this.tokenStorage = tokenStorage;
    }

    @RequestMapping(method = POST)
    public String login(String env, HttpServletRequest request) {
        logger.debug("Login request received, env: '{}', state: '{}'", env);

        Environment sfEnv = Environment.byName(env);
        tokenProvider.setEnvironment(sfEnv);

        URIBuilder ub = new URIBuilder(sfEnv.getAuthURI());
        ub.addParameter("response_type", "code");
        ub.addParameter("client_id", tokenProvider.getClientId());
        ub.addParameter("redirect_uri", tokenProvider.getRedirectUri());
        ub.addParameter("display", "popup");

        // TODO: Support client provided UI state.
        if (localEnabled) {
            String url = request.getRequestURL().toString();
            if (!url.equals(tokenProvider.getRedirectUri())) {
                ub.addParameter("state", url);
            }
        }

        return "redirect:" + ub.toString();
    }

    @RequestMapping(method = GET)
    public String loginCallback(String code, @RequestParam(required = false) String state, HttpServletRequest request) {
        logger.debug("Login callback received, code: '{}', state: '{}'", code, state);

        if (localEnabled && state != null) {
            try {
                URIBuilder ub = new URIBuilder(state);
                ub.addParameter("code", code);

                return "redirect:" + ub.toString();
            } catch (URISyntaxException e) {
                logger.error("Invalid URI: {}", state);
            }
        }

        request.setAttribute(WebAuthTokenProvider.SF_AUTH_CODE_KEY, code);
        Token token = tokenProvider.requestToken();
        tokenStorage.saveToken(token);

        return "redirect:" + postLoginUri;
    }
}
