package sfrest;

public class SFException extends RuntimeException {

    private String errorCode;
    private String response;

    public String getErrorCode() {
        return errorCode;
    }

    public String getResponse() {
        return response;
    }

    public SFException(String message) {
        super(message);
    }

    public SFException(String message, Throwable cause) {
        super(message, cause);
    }

    public SFException(String errorCode, String message) {
        this(errorCode, message, null);
    }

    public SFException(String errorCode, String message, String response) {
        super(message);

        this.errorCode = errorCode;
        this.response = response;
    }
}
