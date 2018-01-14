package ninja.esgi.tvdbandroidapp.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserFavoritesResponse {
    @SerializedName("data")
    @Expose
    private UserFavoritesDataResponse data;
    @SerializedName("errors")
    @Expose
    private UserErrorsResponse errors;

    public UserFavoritesDataResponse getData() {
        return data;
    }

    public void setData(UserFavoritesDataResponse data) {
        this.data = data;
    }

    public UserErrorsResponse getErrors() {
        return errors;
    }

    public void setErrors(UserErrorsResponse errors) {
        this.errors = errors;
    }
}
