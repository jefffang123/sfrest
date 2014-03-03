package sfrest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SFRestClientTest {

    private SFRestClient restClient = new SFRestClient(new TestTokenProvider());

    @Test
    public void testGetString() {
        String str = restClient.getString(SFRestClient.BASE_URI_REST, HttpMethod.GET, null);
        verifyJsonString(str);
    }

    @Test
    public void testGetObject() {
        Object obj = restClient.getObject(SFRestClient.BASE_URI_REST, HttpMethod.GET, null);
        assertNotNull(obj);
        assertTrue(obj instanceof Map);
    }

    @Test
    public void testGetMap() {
        Map<String, ?> ret = restClient.getMap(SFRestClient.BASE_URI_REST + "/sobjects/Lead/describe", HttpMethod.GET, null);
        assertEquals("Lead", ret.get("name"));
    }

    @Test
    public void testGetMapList() {
        List<Map<String, ?>> ret = restClient.getMapList("/services/data", HttpMethod.GET, null);
        assertFalse(ret.isEmpty());
        assertTrue(ret.get(0).containsKey("url"));
    }

    @Test
    public void testGetList() {
        List<?> ret = restClient.getList("/services/data", HttpMethod.GET, null);
        assertFalse(ret.isEmpty());
    }

    @Test
    public void testSimpleApexGet() {
        String name = "John";
        Map<String, ?> ret = restClient.getMap(SFRestClient.BASE_URI_APEX + "/sfapis/echo?name={name}", HttpMethod.GET, null, name);
        assertEquals("Hello " + name, ret.get("message"));
    }

    @Test
    public void testListSObjects() {
        Map<String, ?> ret = restClient.listSObjects();

        assertTrue(ret.containsKey("encoding"));
        assertTrue(ret.containsKey("maxBatchSize"));
        List sobjects = (List) ret.get("sobjects");
        assertFalse(sobjects.isEmpty());
    }

    @Test
    public void testGetSObjectMetadataBasic() {
        String type = "Account";
        Map<String, ?> ret = restClient.getSObjectMetadata(type, false);
        assertTrue(ret.containsKey("recentItems"));
        Map metadata = (Map) ret.get("objectDescribe");
        assertEquals(type, metadata.get("name"));
    }

    @Test
    public void testGetSObjectMetadataDetails() {
        String type = "Lead";
        Map<String, ?> ret = restClient.getSObjectMetadata(type, true);
        assertEquals(type, ret.get("name"));
        assertFalse(((List) ret.get("fields")).isEmpty());
    }

    @Test
    public void testGetSObjectWithoutFields() {
        Map<String, ?> sobject = restClient.getSObject("User", "00590000001eMZoAAM");
        assertEquals("sanlyfang@gmail.com", sobject.get("Username"));
    }

    @Test
    public void testGetSObjectWithFields() {
        Map<String, ?> sobject = restClient.getSObject("User", "00590000001eMZoAAM", "FirstName", "LastName");
        assertEquals("Jeff", sobject.get("FirstName"));
        assertEquals("Fang", sobject.get("LastName"));
        assertNull(sobject.get("Username"));
    }

    @Test
    public void testSimpleQuery() {
        String soql = "SELECT Id, Name FROM Account LIMIT 5"; // Put in "Limit" to make sure no cursor involved.
        assertFalse(restClient.query(soql).isEmpty());
    }

    @Test
    public void testQueryWithCursor() {
        String soql = "SELECT LoginTime, SourceIp, Status FROM LoginHistory LIMIT 5000";

        Query query = new Query(soql);
        QueryResult qResult;
        int size = 0;
        do {
            qResult = restClient.query(query);
            size += qResult.getRecords().size();
        } while (!qResult.isDone());

        assertTrue(qResult.isDone());
        assertTrue(size == qResult.getTotalSize());
        assertNull(qResult.getQuery().getNextUri());
    }

    @Test
    public void testGetEnv() {
        assertSame(Environment.PRODUCTION, restClient.getEnvironment());
    }

    @Test
    public void testGetCurrentUser() {
        assertEquals("sanlyfang@gmail.com", restClient.getCurrentUser().get("Username"));
    }

    private void verifyJsonString(String str) {
        try {
            System.out.println("Verifying json string: " + str);
            new ObjectMapper().readValue(str, Object.class);
        } catch (IOException e) {
            fail("Not a json string: " + str);
        }
    }
}
