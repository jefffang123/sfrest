package sfrest;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class UserPassTokenProvider extends AbstractTokenProvider {

    private String username;
    private String password;
    private String securityToken;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    @Override
    public Token requestToken() {
        SFRestTemplate rt = new SFRestTemplate();
        rt.setErrorFieldMapping("error", "error_description");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("username", username);
        params.add("password", password + (securityToken != null ? securityToken : ""));

        return rt.postForObject(environment.getTokenURI(), params, Token.class);
    }
}
