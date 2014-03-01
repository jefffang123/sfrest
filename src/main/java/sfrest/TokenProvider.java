package sfrest;

public interface TokenProvider {

    Environment getEnvironment();

    Token requestToken(SFRestTemplate template);

    boolean isRefreshable(Token token);

    /**
     * When access token get timed out, can use this method to get a new access token.
     * <p>
     * This only works for web server OAuth authentication flow and user-agent flow, not for username-password OAuth authentication flow.
     * </p>
     *
     * @see <a href="http://www.salesforce.com/us/developer/docs/api_rest/Content/intro_understanding_refresh_token_oauth.htm">Understanding the OAuth Refresh Token Process</a>
     */
    void refreshToken(SFRestTemplate template, Token token);

    /**
     * @see <a href="https://help.salesforce.com/HTViewHelpDoc?id=remoteaccess_revoke_token.htm">Revoking OAuth Tokens</a>
     */
    void revokeToken(SFRestTemplate template, Token token, boolean includingRefreshToken);
}
