package ninja.esgi.tvdbandroidapp.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dylanfoster on 14/01/18.
 */

public class UserFavoritesDataResponse {
    @SerializedName("favorites")
    @Expose
    private List<String> favorites = null;

    public List<String> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<String> favorites) {
        this.favorites = favorites;
    }

    public boolean checkForDisplayableFavs() {
        if (this.favorites.size() == 0) return false;
        for (String fav: this.favorites) {
            if (fav != null && fav.trim().length() > 0)
                return true;
        }
        return false;
    }
}
