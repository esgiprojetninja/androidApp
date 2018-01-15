package ninja.esgi.tvdbandroidapp.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dylanfoster on 15/01/18.
 */

public class SearchSeriesDataResponse {
    @SerializedName("aliases")
    @Expose
    private List<String> aliases;

    @SerializedName("banner")
    @Expose
    private String banner;

    @SerializedName("firstAired")
    @Expose
    private String firstAired;

    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("network")
    @Expose
    private String network;

    @SerializedName("overview")
    @Expose
    private String overview;

    @SerializedName("seriesName")
    @Expose
    private String seriesName;

    @SerializedName("status")
    @Expose
    private String status;

    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getFirstAired() {
        return firstAired;
    }

    public void setFirstAired(String firstAired) {
        this.firstAired = firstAired;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
