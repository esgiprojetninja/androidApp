package ninja.esgi.tvdbandroidapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class SharedStoragePrefs extends Service {
    public static final String PREFS_NAME = "tdvb.android.app";
    public final static String TOKEN_KEY = String.format("%s.tdvb_user_token", PREFS_NAME);

    public SharedStoragePrefs() {
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
}
