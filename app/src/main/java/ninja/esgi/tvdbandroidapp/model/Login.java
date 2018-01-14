package ninja.esgi.tvdbandroidapp.model;

public class Login {
    private String apikey;
    private String username;
    private String userkey;

    public Login(String apikey, String username, String userkey) {
        this.apikey = apikey;
        this.username = username;
        this.userkey = userkey;
    }

    public String getApiKey() {
        return apikey;
    }

    public void setApiKey(String apiKey) {
        this.apikey = apiKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserkey() {
        return userkey;
    }

    public void setUserkey(String userkey) {
        this.userkey = userkey;
    }

    @Override
    public String toString() {
        return String.format("Login{apikey='%s', username='%s', userkey='%s'}", this.apikey, this.username, this.userkey);
    }
}
