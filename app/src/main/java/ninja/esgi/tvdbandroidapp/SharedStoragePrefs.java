package ninja.esgi.tvdbandroidapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class SharedStoragePrefs extends Service {
    public final static Long TOKEN_DURATION = Long.valueOf(60*60*24);
    public static final String PREFS_NAME = "tdvb.android.app";
    public final static String TOKEN_KEY = String.format("%s.tdvb_user_token", PREFS_NAME);
    public final static String TOKEN_TS = String.format("%s.tdvb_user_token_ts", PREFS_NAME);
    public final static String USER_KEY = String.format("%s.tdvb_user_key", PREFS_NAME);
    public final static String USER_NAME = String.format("%s.tdvb_user_name", PREFS_NAME);
    public static String userName = null;
    public static String userKey = null;
    public static Long tokenTs = null;

    public SharedStoragePrefs() {
    }

    public static boolean isUserConnected() {
        return userName != null && userKey != null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public boolean saveToken(String token) {
        final Context context = getApplicationContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SharedStoragePrefs.TOKEN_KEY, token);
        // Commit the edition
        return editor.commit();
    }

    public String getToken() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(SharedStoragePrefs.TOKEN_KEY, null);
    }

    public boolean isTokenExpired() {
        Long tsLong = System.currentTimeMillis()/1000;
        if (tokenTs == null) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            tokenTs = Long.valueOf(settings.getString(SharedStoragePrefs.TOKEN_TS, null));
        }
        return tsLong - tokenTs <= TOKEN_DURATION;
    }
}
