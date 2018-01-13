package ninja.esgi.tvdbandroidapp.session;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class SharedStoragePrefs extends Service {
    private static SharedStoragePrefs sess = null;
    public Context context = null;

    public final static Long TOKEN_DURATION = Long.valueOf(60*60*24);
    public static final String PREFS_NAME = "tdvb.android.app";
    public final static String TOKEN_KEY = String.format("%s.tdvb_user_token", PREFS_NAME);
    public final static String TOKEN_TS = String.format("%s.tdvb_user_token_ts", PREFS_NAME);
    public final static String USER_KEY = String.format("%s.tdvb_user_key", PREFS_NAME);
    public final static String USER_NAME = String.format("%s.tdvb_user_name", PREFS_NAME);
    public static String username = null;
    public static String userkey = null;
    public static Long tokenTs = null;

    public SharedStoragePrefs(){}

    public SharedStoragePrefs SharedStoragePrefs(Context context) {
        if (sess == null) {
            sess = new SharedStoragePrefs();
        }
        sess.context = context;
        return sess;
    }

    public static boolean isUserConnected() {
        return username != null && userkey != null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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
