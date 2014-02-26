package sfrest;

/**
 * Throws this exception when token expired or invalid.
 */
public class TokenException extends SFException {

    public TokenException(String errorCode, String message) {
        super(errorCode, message);
    }

    public TokenException(String errorCode, String message, String response) {
        super(errorCode, message, response);
    }
}
