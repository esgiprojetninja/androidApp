package ninja.esgi.tvdbandroidapp.model;

public class Token {

    public static final String TOKEN_PREFIX = "Bearer ";

    private String token;

    public Token(String token) {
        setToken(token);
    }

    public static boolean validateToken(String token) {
        return token.startsWith(TOKEN_PREFIX);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        if (!validateToken(token)) {
            token = TOKEN_PREFIX.concat(token);
        }
        this.token = token;
    }

    public boolean validateToken() {
        return this.token.startsWith(TOKEN_PREFIX);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Token that = (Token) o;

        return token.equals(that.token);

    }

    @Override
    public int hashCode() {
        return token.hashCode();
    }

    @Override
    public String toString() {
        return this.token;
    }
}
