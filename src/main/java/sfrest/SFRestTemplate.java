package sfrest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class SFRestTemplate extends RestTemplate {

    public SFRestTemplate() {
    }

    public SFRestTemplate(ClientHttpRequestFactory requestFactory) {
        super(requestFactory);
    }

    public void setErrorFieldMapping(String errorCodeField, String messageField) {
        setErrorHandler(new SFResponseErrorHandler(errorCodeField, messageField));
    }

    private class SFResponseErrorHandler extends DefaultResponseErrorHandler {

        private String errorCodeField;
        private String messageField;

        SFResponseErrorHandler(String errorCodeField, String messageField) {
            this.errorCodeField = errorCodeField;
            this.messageField = messageField;
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

            if (error == null || !error.containsKey(errorCodeField) || !error.containsKey(messageField)) {
                super.handleError(response);
            } else {
                String errorCode = error.get(errorCodeField);
                String message = error.get(messageField);
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
