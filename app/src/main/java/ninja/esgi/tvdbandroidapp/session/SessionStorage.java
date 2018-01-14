package ninja.esgi.tvdbandroidapp.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ninja.esgi.tvdbandroidapp.BuildConfig;

public class SessionStorage {
    public final static Long TOKEN_DURATION = Long.valueOf(60*60*24);
    public static final String PREFS_NAME = "tdvb.android.app";
    public final static String SESSION_TOKEN = String.format("%s.tdvb_session_token", PREFS_NAME);
    public final static String TOKEN_TS = String.format("%s.tdvb_user_token_ts", PREFS_NAME);
    public final static String USER_KEY = String.format("%s.tdvb_user_key", PREFS_NAME);
    public final static String USER_NAME = String.format("%s.tdvb_user_name", PREFS_NAME);
    public final static String API_KEY = String.format("%s.tdvb_api_key", PREFS_NAME);
    protected static final SessionStorage ourInstance = new SessionStorage();

    public String sessionToken = null;
    public String apiKey = null;
    public String userName = null;
    public String userKey = null;
    public String userLanguage = null;
    public String favoritesDisplaymode = null;
    public Long sessionTokenTS = null;

    public Context context = null;
    public SharedPreferences preferences = null;


    public static SessionStorage getInstance(Context context) {
        ourInstance.context = context;
        ourInstance.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        ourInstance.defineApiKey();
        return ourInstance;
    }

    public void defineApiKey() {
        if (this.apiKey == null) {
            this.apiKey = BuildConfig.TDVB_API_KEY;
        }
    }

    public String getApiKey() {
        return this.apiKey;
    }
    public String getUserName() { return this.userName; }
    public String getSessionToken() { return this.sessionToken; }
    public String getUserLanguage() { return this.userLanguage; }
    public String getFavoritesDisplaymode() { return this.favoritesDisplaymode; }

    public SessionStorage setSessionToken(String token) {
        ourInstance.sessionToken = "Bearer " + token;
        return ourInstance;
    }
    public SessionStorage setUserName(String name) {
        ourInstance.userName = name;
        return ourInstance;
    }
    public SessionStorage setUserKey(String key) {
        ourInstance.userKey = key;
        return ourInstance;
    }
    public SessionStorage setApiKey(String key) {
        ourInstance.apiKey = key;
        return ourInstance;
    }
    public SessionStorage setUserLanguage(String lngge) {
        ourInstance.userLanguage = lngge;
        return ourInstance;
    }
    public SessionStorage setFavoriteDisplayMode(String mode) {
        ourInstance.favoritesDisplaymode = mode;
        return ourInstance;
    }

    public boolean saveCredentials() {
        SharedPreferences.Editor editor = this.preferences.edit();
        Long tsLong = System.currentTimeMillis()/1000;
        this.sessionTokenTS = tsLong;
        editor.putString(API_KEY, this.apiKey);
        editor.putString(TOKEN_TS, this.sessionTokenTS.toString());
        editor.putString(USER_KEY, this.userKey);
        editor.putString(USER_NAME, this.userName);
        editor.putString(SESSION_TOKEN, this.sessionToken);
        return editor.commit();
    }

    public boolean isUserConnected() {
        return this.sessionToken != null
                && this.userName != null
                && this.userKey != null
                && this.apiKey != null
                && this.sessionToken.length() > 0
                && this.userName.length() > 0;
    }

    public boolean isUserInfoLoaded() {
        return this.userLanguage != null
                && this.favoritesDisplaymode != null
                && this.userLanguage.length() > 0
                && this.favoritesDisplaymode.length() > 0;
    }

    public void clearSession() {
        this
            .setUserKey(null)
            .setSessionToken(null)
            .setUserName(null)
            .setApiKey(null);
        SharedPreferences.Editor editor = this.preferences.edit();
        editor.remove(TOKEN_TS);
        editor.remove(USER_KEY);
        editor.remove(USER_NAME);
        editor.remove(API_KEY);
        editor.remove(SESSION_TOKEN);
        editor.apply();
    }

    private SessionStorage() {
    }
}
