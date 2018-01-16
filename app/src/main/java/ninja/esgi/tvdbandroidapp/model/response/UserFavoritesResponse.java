package ninja.esgi.tvdbandroidapp.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserFavoritesResponse {
    @SerializedName("data")
    @Expose
    private UserFavoritesDataResponse data;
    @SerializedName("errors")
    @Expose
    private GenericErrorsResponse errors;

    public UserFavoritesDataResponse getData() {
        return data;
    }

    public void setData(UserFavoritesDataResponse data) {
        this.data = data;
    }

    public GenericErrorsResponse getErrors() {
        return errors;
    }

    public void setErrors(GenericErrorsResponse errors) {
        this.errors = errors;
    }
}
