package ninja.esgi.tvdbandroidapp.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dylanfoster on 14/01/18.
 */

public class UserRatingsDataResponse {
    @SerializedName("rating")
    @Expose
    private Long rating;

    @SerializedName("ratingItemId")
    @Expose
    private Long ratingItemId;

    @SerializedName("ratingType")
    @Expose
    private String ratingType;

    public Long getRating() {
        return rating;
    }

    public void setRating(Long rating) {
        this.rating = rating;
    }

    public Long getRatingItemId() {
        return ratingItemId;
    }

    public void setRatingItemId(Long ratingItemId) {
        this.ratingItemId = ratingItemId;
    }

    public String getRatingType() {
        return ratingType;
    }

    public void setRatingType(String ratingType) {
        this.ratingType = ratingType;
    }

    @Override
    public String toString() {
        return String.format("Rated %s : %d with %d", this.ratingType, this.ratingItemId, this.rating);
    }
}
