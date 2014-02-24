package sfrest.web;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

/**
 * Spring security integration for web authentication.
 */
public class WebAuthProvider implements AuthenticationProvider {

    private static final Logger logger = LoggerFactory.getLogger(WebAuthProvider.class);

    @Autowired
    private WebAuthRestClient client;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        logger.debug("Processing authentication: {}", authentication);

        if (client.isLoggedIn()) {
            return new UsernamePasswordAuthenticationToken(client.getCurrentUsername(),
                    client.getTokenStorage().getToken(),
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        } else {
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
