package sfrest.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sfrest.SFRestClient;

@Component
public class WebAuthRestClient extends SFRestClient {

    private SessionTokenStorage tokenStorage;

    @Autowired
    public WebAuthRestClient(WebAuthTokenProvider tokenProvider, SessionTokenStorage tokenStorage) {
        super(tokenProvider, tokenStorage);

        this.tokenStorage = tokenStorage;
    }

    public boolean isLoggedIn() {
        return tokenStorage.getToken() != null;
    }
}
