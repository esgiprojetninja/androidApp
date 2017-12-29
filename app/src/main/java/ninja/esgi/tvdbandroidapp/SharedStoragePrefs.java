package ninja.esgi.tvdbandroidapp;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class SharedStoragePrefs extends Service {
    public final static String TOKEN_KEY = "tdvb_token";
    public static final String PREFS_NAME = "MyPrefsFile";

    public SharedStoragePrefs() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void saveToken(String token) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SharedStoragePrefs.TOKEN_KEY, token);
        // Commit the edition
        editor.commit();
    }

    public String getToken() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(SharedStoragePrefs.TOKEN_KEY, null);
    }
}
