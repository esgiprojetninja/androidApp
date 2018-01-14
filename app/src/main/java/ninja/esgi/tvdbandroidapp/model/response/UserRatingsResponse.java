package ninja.esgi.tvdbandroidapp.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dylanfoster on 14/01/18.
 */

public class UserRatingsResponse {
    @SerializedName("data")
    @Expose
    private List<UserRatingsDataResponse> data;

    @SerializedName("errors")
    @Expose
    private UserErrorsResponse errors;

    @SerializedName("links")
    @Expose
    private UserRatingsLinksResponse links;

    public List<UserRatingsDataResponse> getData() {
        return data;
    }

    public void setData(List<UserRatingsDataResponse> data) {
        this.data = data;
    }

    public UserErrorsResponse getErrors() {
        return errors;
    }

    public void setErrors(UserErrorsResponse errors) {
        this.errors = errors;
    }

    public UserRatingsLinksResponse getLinks() {
        return links;
    }

    public void setLinks(UserRatingsLinksResponse links) {
        this.links = links;
    }
}
