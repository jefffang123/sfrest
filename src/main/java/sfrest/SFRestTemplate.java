package sfrest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A <code>RestTemplate</code> that knows how to translate salesforce errors into corresponding Exceptions.
 */
public class SFRestTemplate extends RestTemplate {

    private static final ResponseErrorHandler SF_ERROR_HANDLER = new SFResponseErrorHandler();

    public SFRestTemplate() {
        setErrorHandler(SF_ERROR_HANDLER);
    }

    public SFRestTemplate(ClientHttpRequestFactory requestFactory) {
        super(requestFactory);

        setErrorHandler(SF_ERROR_HANDLER);
    }

    private static class SFResponseErrorHandler extends DefaultResponseErrorHandler {

        private final Map<String, String> errorFields = new HashMap<>();

        private SFResponseErrorHandler() {
            errorFields.put("error", "error_description");
            errorFields.put("errorCode", "message");
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            String content = StreamUtils.copyToString(response.getBody(), Charset.defaultCharset());
            Object errors = new ObjectMapper().readValue(content, Object.class);

            Map<String, String> error;
            if (errors instanceof List) {
                error = (Map) ((List) errors).get(0);
            } else {
                error = (Map) errors;
            }

            String errorKey = null;
            if (error != null) {
                for (String key : errorFields.keySet()) {
                    if (error.containsKey(key)) {
                        errorKey = key;
                        break;
                    }
                }
            }

            if (errorKey == null) {
                super.handleError(response);
            } else {
                String errorCode = error.get(errorKey);
                String message = error.get(errorFields.get(errorKey));
                switch (errorCode) {
                    case "INVALID_SESSION_ID":
                        throw new TokenException(errorCode, message, content);
                    default:
                        throw new SFException(errorCode, message, content);
                }
            }
        }
    }
}
