package ninja.esgi.tvdbandroidapp.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dylanfoster on 14/01/18.
 */

public class UserFavoritesResponse {
    @SerializedName("data")
    @Expose
    private UserFavoritesDataResponse data;
    @SerializedName("errors")
    @Expose
    private UserFavoritesErrorsResponse errors;

    public UserFavoritesDataResponse getData() {
        return data;
    }

    public void setData(UserFavoritesDataResponse data) {
        this.data = data;
    }

    public UserFavoritesErrorsResponse getErrors() {
        return errors;
    }

    public void setErrors(UserFavoritesErrorsResponse errors) {
        this.errors = errors;
    }
}
