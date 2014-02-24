package sfrest.web;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sfrest.Token;
import sfrest.TokenStorage;

import javax.servlet.http.HttpSession;

@Component
public class SessionTokenStorage implements TokenStorage {

    private static final String SF_TOKEN_KEY = "SF_TOKEN";

    @Override
    public Token getToken() {
        return (Token) currentSession().getAttribute(SF_TOKEN_KEY);
    }

    @Override
    public void saveToken(Token token) {
        currentSession().setAttribute(SF_TOKEN_KEY, token);
    }

    @Override
    public void clearToken() {
        currentSession().removeAttribute(SF_TOKEN_KEY);
    }

    private static HttpSession currentSession() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession();
    }
}
