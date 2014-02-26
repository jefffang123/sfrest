package sfrest;

import org.junit.Test;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SFRestClientTest {

    private SFRestClient restClient = new SFRestClient(new TestTokenProvider());

    @Test
    public void testGetList() {
        List<?> ret = restClient.getList("/services/data", HttpMethod.GET, null);
        assertFalse(ret.isEmpty());
    }

    @Test
    public void testGetMap() {
        Map<String, ?> ret = restClient.getMap(SFRestClient.BASE_URI_REST + "/sobjects/Lead/describe", HttpMethod.GET, null);
        assertEquals("Lead", ret.get("name"));
    }

    @Test
    public void testSimpleApexGet() {
        String name = "John";
        Map<String, ?> ret = restClient.getMap(SFRestClient.BASE_URI_APEX + "/sfapis/echo?name={name}", HttpMethod.GET, null, name);
        assertEquals("Hello " + name, ret.get("message"));
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
    public void testListSObjects() {
        Map<String, ?> ret = restClient.listSObjects();
        @SuppressWarnings("rawtypes")
        List sobjects = (List) ret.get("sobjects");
        assertTrue(sobjects.size() > 0);
    }

    @Test
    public void testGetSObjectMetadataBasic() {
        String type = "Account";
        Map<String, ?> ret = restClient.getSObjectMetadata(type, false);
        @SuppressWarnings("unchecked")
        Map<String, ?> metadata = (Map<String, ?>) ret.get("objectDescribe");
        assertEquals(type, metadata.get("name"));
        assertNotNull(ret.get("recentItems"));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testGetSObjectMetadataDetails() {
        String type = "Lead";
        Map<String, ?> ret = restClient.getSObjectMetadata(type, true);
        assertEquals(type, ret.get("name"));
        assertTrue(((List) ret.get("fields")).size() > 0);
    }

    @Test
    public void testGetCurrentUsername() {
        assertEquals("sanlyfang@gmail.com", restClient.getCurrentUsername());
    }

    @Test
    public void testSimpleQuery() {
        String soql = "SELECT Id, Name FROM Account LIMIT 10"; // Put in "Limit" to make sure no cursor involved.
        QueryResult qResult = restClient.query(soql);
        assertTrue(qResult.getTotalSize() > 0);
        assertTrue(qResult.isDone());
        assertEquals(qResult.getTotalSize(), qResult.getRecords().size());
        assertNull(qResult.getQueryLocator());
    }

    @Test
    public void testQueryWithCursor() {
        String soql = "SELECT LoginTime, SourceIp, Status FROM LoginHistory";

        QueryResult qResult = restClient.query(soql);
        int totalSize = qResult.getTotalSize();
        int size = qResult.getRecords().size();

        while (!qResult.isDone()) {
            assertTrue(qResult.getRecords().size() == 2000);
            assertNotNull(qResult.getQueryLocator());
            qResult = restClient.queryMore(qResult.getQueryLocator());
            size += qResult.getRecords().size();
        }

        assertTrue(qResult.isDone());
        assertNull(qResult.getQueryLocator());
        assertTrue(size == totalSize);
    }
}
