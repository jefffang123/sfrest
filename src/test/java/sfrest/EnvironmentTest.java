package sfrest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static sfrest.Environment.PRODUCTION;
import static sfrest.Environment.SANDBOX;

public class EnvironmentTest {

    @Test
    public void testURIs() {
        assertEquals("https://login.salesforce.com/services/oauth2/authorize", PRODUCTION.getAuthURI().toString());
        assertEquals("https://login.salesforce.com/services/oauth2/token", PRODUCTION.getTokenURI().toString());
        assertEquals("https://login.salesforce.com/services/oauth2/revoke", PRODUCTION.getRevokeTokenURI().toString());

        assertEquals("https://test.salesforce.com/services/oauth2/authorize", SANDBOX.getAuthURI().toString());
        assertEquals("https://test.salesforce.com/services/oauth2/token", SANDBOX.getTokenURI().toString());
        assertEquals("https://test.salesforce.com/services/oauth2/revoke", SANDBOX.getRevokeTokenURI().toString());
    }
}
