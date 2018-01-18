package ninja.esgi.tvdbandroidapp.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.List;

import ninja.esgi.tvdbandroidapp.BuildConfig;
import ninja.esgi.tvdbandroidapp.model.response.LanguagesDataResponse;
import ninja.esgi.tvdbandroidapp.model.response.UserRatingsDataResponse;

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
    public List<LanguagesDataResponse> languages;
    public List<String> userFavoritesShows = null;
    public List<UserRatingsDataResponse> userRatings = null;

    public Context context = null;
    public SharedPreferences preferences = null;


    public static SessionStorage getInstance(Context context) {
        ourInstance.context = context;
        ourInstance.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        ourInstance.defineApiKey();
        return ourInstance;
    }
    private SessionStorage() {
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
    public List<LanguagesDataResponse> getLanguages() { return this.languages; }
    public List<String> getUserFavoritesShows() { return this.userFavoritesShows; }
    public List<UserRatingsDataResponse> getUserRatings() { return this.userRatings; }

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
    public SessionStorage setLanguages(List langs) {
        ourInstance.languages = langs;
        return ourInstance;
    }
    public SessionStorage setUserFavoritesShows(List userFavoritesShows) {
        ourInstance.userFavoritesShows = userFavoritesShows;
        return ourInstance;
    }
    public SessionStorage setUserRatings(List<UserRatingsDataResponse> userRatings) {
        ourInstance.userRatings = userRatings;
        return ourInstance;
    }
    public SessionStorage addUserRating(UserRatingsDataResponse userRating) {
        ourInstance.userRatings.add(userRating);
        return ourInstance;
    }
    public SessionStorage removeUserRating(String itemType, String itemId) {
        if (getUserRatings() == null) return this;
        UserRatingsDataResponse toRemove = null;
        for(UserRatingsDataResponse userRating: getUserRatings()) {
            if (userRating.getRatingType().compareTo(itemType) == 0
                    && userRating.getRatingItemId().toString().compareTo(itemId) == 0) {
                toRemove = userRating;
                break;
            }
        }
        if (toRemove != null)
            getUserRatings().remove(toRemove);
        return this;
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
        return ourInstance.sessionToken != null
                && ourInstance.userName != null
                && ourInstance.userKey != null
                && ourInstance.apiKey != null
                && ourInstance.sessionToken.length() > 0
                && ourInstance.userName.length() > 0;
    }

    public boolean isUserInfoLoaded() {
        return ourInstance.userLanguage != null
                && ourInstance.favoritesDisplaymode != null
                && ourInstance.userLanguage.length() > 0
                && ourInstance.favoritesDisplaymode.length() > 0;
    }

    public boolean areLanguagesLoaded() {
        return (ourInstance.languages != null && ourInstance.languages.size() > 0) ? true : false;
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

    public boolean isShowFavorite(String showId) {
        if (getUserFavoritesShows() == null) return false;
        for(String id: getUserFavoritesShows()) {
            if (id.compareTo(showId) == 0) return true;
        }
        return false;
    }

    public UserRatingsDataResponse getRatingIfExists(String itemType, String itemId) {
        if (getUserRatings() == null) return null;
        for(UserRatingsDataResponse userRating: getUserRatings()) {
            if (userRating.getRatingType().compareTo(itemType) == 0
                    && userRating.getRatingItemId().toString().compareTo(itemId) == 0) return userRating;
        }
        return null;
    }
}
