package sfrest;

public interface TokenStorage {

    Token getToken();

    void saveToken(Token token);

    void clearToken();
}
