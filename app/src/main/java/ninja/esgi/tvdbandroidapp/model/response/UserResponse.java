package ninja.esgi.tvdbandroidapp.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dylanfoster on 13/01/18.
 */

public class UserResponse {
    @SerializedName("data")
    @Expose
    private UserDetailResponse data;

    public UserDetailResponse getData() {
        return data;
    }

    public void setData(UserDetailResponse data) {
        this.data = data;
    }
}
