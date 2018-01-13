package ninja.esgi.tvdbandroidapp.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ninja.esgi.tvdbandroidapp.BuildConfig;

/**
 * Created by dylanfoster on 13/01/18.
 */

public class SessionStorage {
    public final static Long TOKEN_DURATION = Long.valueOf(60*60*24);
    public static final String PREFS_NAME = "tdvb.android.app";
    public final static String SESSION_TOKEN = String.format("%s.tdvb_session_token", PREFS_NAME);
    public final static String TOKEN_TS = String.format("%s.tdvb_user_token_ts", PREFS_NAME);
    public final static String USER_KEY = String.format("%s.tdvb_user_key", PREFS_NAME);
    public final static String USER_NAME = String.format("%s.tdvb_user_name", PREFS_NAME);
    public final static String API_KEY = String.format("%s.tdvb_api_key", PREFS_NAME);
    private static final SessionStorage ourInstance = new SessionStorage();

    public String sessionToken = null;
    public String apiKey = null;
    public String userName = null;
    public String userKey = null;
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

    private SessionStorage() {
    }
}
