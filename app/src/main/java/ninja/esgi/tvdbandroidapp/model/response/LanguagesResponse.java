package ninja.esgi.tvdbandroidapp.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LanguagesResponse {
    @SerializedName("data")
    @Expose
    private List<LanguagesDataResponse> data;

    public List<LanguagesDataResponse> getData() {
        return data;
    }

    public void setData(List<LanguagesDataResponse> data) {
        this.data = data;
    }
}
