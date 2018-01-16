package ninja.esgi.tvdbandroidapp.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchSeriesResponse {
    @SerializedName("data")
    @Expose
    private List<SearchSeriesDataResponse> data;

    public List<SearchSeriesDataResponse> getData() {
        return data;
    }

    public void setData(List<SearchSeriesDataResponse> data) {
        this.data = data;
    }
}
